FROM eclipse-temurin:25-jdk-noble AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q
# Extract the fat JAR so native SDK JARs are regular files on disk
# (the Nutrient SDK resolves its JAR path via URI — nested JARs break this)
RUN java -Djarmode=tools -jar target/*.jar extract --destination extracted

FROM eclipse-temurin:25-jre-noble
# Pin to the Noble (Ubuntu 24.04) image variant: the unsuffixed 21-jre tag
# rebased onto a newer Ubuntu where libicu74 no longer exists, breaking the build.
RUN apt-get update && apt-get install -y --no-install-recommends \
    libxext6 libxrender1 libxtst6 libxi6 libfreetype6 fontconfig libicu74 \
    libgdiplus fonts-dejavu-core fonts-liberation \
    && rm -rf /var/lib/apt/lists/*
# HTML -> PDF: the SDK renders HTML with Google Chrome on Linux and requires a
# non-root user (Nutrient guide "Headless modes - Google Chrome"). Google ships
# Chrome for linux/amd64 only (Fly runs amd64); arm64 builds skip it, so HTML
# conversion only works in amd64 images.
# The SDK launches /usr/bin/google-chrome. Chrome's sandbox can't start on Fly
# VMs ("sandbox is not supported", tested on the live Fly app) or under Docker's
# default seccomp (Railway), and the SDK then times out after 60 s. So that path
# points at a wrapper adding --no-sandbox, after the SDK's own args so it wins.
# Uploaded HTML still renders with scripts, network and file access off
# (UntrustedInput), which removes most of what the sandbox would contain.
ARG TARGETARCH
RUN if [ "$TARGETARCH" = "amd64" ]; then \
        apt-get update && apt-get install -y --no-install-recommends ca-certificates curl \
        && curl -fsSL -o /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
        && apt-get install -y --no-install-recommends /tmp/chrome.deb \
        && rm -rf /tmp/chrome.deb /var/lib/apt/lists/* \
        && printf '#!/bin/sh\nexec /opt/google/chrome/google-chrome "$@" --no-sandbox\n' > /usr/local/bin/chrome-no-sandbox \
        && chmod 755 /usr/local/bin/chrome-no-sandbox \
        && ln -sf /usr/local/bin/chrome-no-sandbox /usr/bin/google-chrome \
        && ln -sf /usr/local/bin/chrome-no-sandbox /usr/bin/google-chrome-stable; \
    fi
RUN useradd --system --create-home nutrient
WORKDIR /app
COPY --from=build --chown=nutrient:nutrient /app/extracted/ ./
USER nutrient
EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
