FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
RUN ./mvnw package -DskipTests -q
# Extract the fat JAR so native SDK JARs are regular files on disk
# (the Nutrient SDK resolves its JAR path via URI — nested JARs break this)
RUN java -Djarmode=tools -jar target/*.jar extract --destination extracted

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/extracted/ ./
EXPOSE 8080
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
