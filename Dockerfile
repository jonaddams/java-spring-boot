FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q
# Extract the fat JAR so native SDK JARs are regular files on disk
# (the Nutrient SDK resolves its JAR path via URI — nested JARs break this)
RUN java -Djarmode=tools -jar target/*.jar extract --destination extracted

FROM eclipse-temurin:21-jre-noble
# Pin to the Noble (Ubuntu 24.04) image variant: the unsuffixed 21-jre tag
# rebased onto a newer Ubuntu where libicu74 no longer exists, breaking the build.
RUN apt-get update && apt-get install -y --no-install-recommends \
    libxext6 libxrender1 libxtst6 libxi6 libfreetype6 fontconfig libicu74 \
    libgdiplus fonts-dejavu-core fonts-liberation \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/extracted/ ./
EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
