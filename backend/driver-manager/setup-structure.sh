#!/bin/bash

# =============================================================================
# Script: setup-structure.sh
# Descrição: Cria a estrutura inicial de pastas e arquivos do projeto Spring Boot
# =============================================================================

set -e

# Caminho base do projeto
BASE_PATH="src/main/java/com/fretemais/drivermanager"
RESOURCES_PATH="src/main/resources"
PACKAGE_BASE="com.fretemais.drivermanager"

echo "🚀 Iniciando criação da estrutura do projeto..."

# =============================================================================
# Função para criar arquivo Java com declaração de package
# =============================================================================
create_java_file() {
    local folder=$1
    local filename=$2
    local package_suffix=$3

    local full_path="${BASE_PATH}/${folder}"
    local full_package="${PACKAGE_BASE}.${package_suffix}"
    local file_path="${full_path}/${filename}"

    mkdir -p "$full_path"

    cat > "$file_path" << EOF
package ${full_package};

public class ${filename%.java} {

}
EOF

    echo "✅ Criado: ${file_path}"
}

# =============================================================================
# Criando estrutura de pastas e arquivos Java
# =============================================================================

echo ""
echo "📁 Criando pastas e arquivos Java..."
echo "----------------------------------------"

# config/
create_java_file "config" "SecurityConfig.java" "config"
create_java_file "config" "SwaggerConfig.java" "config"

# domain/model/
create_java_file "domain/model" "Driver.java" "domain.model"

# domain/enums/
mkdir -p "${BASE_PATH}/domain/enums"
cat > "${BASE_PATH}/domain/enums/VehicleType.java" << EOF
package ${PACKAGE_BASE}.domain.enums;

public enum VehicleType {

}
EOF
echo "✅ Criado: ${BASE_PATH}/domain/enums/VehicleType.java"

# application/dtos/
create_java_file "application/dtos" "DriverRequestDTO.java" "application.dtos"
create_java_file "application/dtos" "DriverResponseDTO.java" "application.dtos"

# application/mappers/
create_java_file "application/mappers" "DriverMapper.java" "application.mappers"

# application/services/
create_java_file "application/services" "DriverService.java" "application.services"

# infrastructure/controllers/
create_java_file "infrastructure/controllers" "DriverController.java" "infrastructure.controllers"

# infrastructure/persistence/
create_java_file "infrastructure/persistence" "DriverRepository.java" "infrastructure.persistence"
create_java_file "infrastructure/persistence" "DriverSpecification.java" "infrastructure.persistence"

# infrastructure/security/
create_java_file "infrastructure/security" "JwtTokenProvider.java" "infrastructure.security"
create_java_file "infrastructure/security" "JwtAuthenticationFilter.java" "infrastructure.security"

# infrastructure/exceptions/
create_java_file "infrastructure/exceptions" "GlobalExceptionHandler.java" "infrastructure.exceptions"

# =============================================================================
# Criando arquivos de configuração YAML
# =============================================================================

echo ""
echo "📄 Criando arquivos de configuração..."
echo "----------------------------------------"

mkdir -p "$RESOURCES_PATH"

# application.yml
cat > "${RESOURCES_PATH}/application.yml" << 'EOF'
spring:
  profiles:
    active: dev

  application:
    name: driver-manager
EOF
echo "✅ Criado: ${RESOURCES_PATH}/application.yml"

# application-dev.yml
cat > "${RESOURCES_PATH}/application-dev.yml" << 'EOF'
spring:
  # Configuração H2 (banco em memória)
  datasource:
    url: jdbc:h2:mem:drivermanager
    driver-class-name: org.h2.Driver
    username: sa
    password:

  # Configuração alternativa PostgreSQL local
  # datasource:
  #   url: jdbc:postgresql://localhost:5432/drivermanager
  #   driver-class-name: org.postgresql.Driver
  #   username: postgres
  #   password: postgres

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: INFO
    com.fretemais.challenge.drivermanager: DEBUG
    org.springframework.security: DEBUG
EOF
echo "✅ Criado: ${RESOURCES_PATH}/application-dev.yml"

# application-prod.yml
cat > "${RESOURCES_PATH}/application-prod.yml" << 'EOF'
spring:
  datasource:
    url: ${DB_URL}
    driver-class-name: ${DB_DRIVER:org.postgresql.Driver}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    root: INFO
    com.fretemais.challenge.drivermanager: INFO
EOF
echo "✅ Criado: ${RESOURCES_PATH}/application-prod.yml"

# =============================================================================
# Resumo final
# =============================================================================

echo ""
echo "========================================"
echo "🎉 Estrutura criada com sucesso!"
echo "========================================"
echo ""
echo "📂 Estrutura de pastas:"
echo ""
find ${BASE_PATH} -type f -name "*.java" | sort | sed 's/^/   /'
echo ""
find ${RESOURCES_PATH} -type f -name "*.yml" | sort | sed 's/^/   /'
echo ""
