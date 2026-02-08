
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .

RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -Dspring-boot.aot.enabled=true -T 1C

RUN java -Djarmode=layertools -jar target/*.jar extract

FROM eclipse-temurin:21-jre-alpine AS cds_builder
WORKDIR /app

COPY --from=build /app/dependencies/ ./
COPY --from=build /app/spring-boot-loader/ ./
COPY --from=build /app/snapshot-dependencies/ ./
COPY --from=build /app/application/ ./

RUN java -XX:ArchiveClassesAtExit=application.jsa \
    -Dspring.context.exit=onRefresh \
    -Dspring.aot.enabled=true \
    org.springframework.boot.loader.launch.JarLauncher || true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=cds_builder --chown=appuser:appgroup /app/dependencies/ ./
COPY --from=cds_builder --chown=appuser:appgroup /app/spring-boot-loader/ ./
COPY --from=cds_builder --chown=appuser:appgroup /app/snapshot-dependencies/ ./
COPY --from=cds_builder --chown=appuser:appgroup /app/application/ ./
COPY --from=cds_builder --chown=appuser:appgroup /app/application.jsa ./

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:+UseZGC", \
    "-XX:+ZGenerational", \
    "-XX:SharedArchiveFile=application.jsa", \
    "-Dspring.aot.enabled=true", \
    "-Dspring.threads.virtual.enabled=true", \
    "-XX:+TieredCompilation", \
    "-XX:TieredStopAtLevel=1", \
    "-XX:+DisableExplicitGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "org.springframework.boot.loader.launch.JarLauncher"]