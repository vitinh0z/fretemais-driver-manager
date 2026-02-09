# Uso de IA no Desenvolvimento

Este documento registra os prompts utilizados durante o desenvolvimento do projeto, os resultados gerados pela IA e as adaptações manuais realizadas.

---

## 1. Arquitetura e Estrutura Inicial do Projeto

### Prompt Utilizado
```
Crie a estrutura de pastas para um projeto Spring Boot seguindo arquitetura em camadas 
(Domain, Application, Infrastructure) para gerenciamento de motoristas.
```

### O que a IA Gerou
- Estrutura de pacotes:
  - `domain/model/`
  - `domain/enums/`
  - `application/services/`
  - `application/dtos/`
  - `application/mappers/`
  - `infrastructure/controllers/`
  - `infrastructure/persistence/`
  - `infrastructure/security/`
  - `infrastructure/exceptions/`
  - `config/`
- Classes vazias com apenas declaração de package

### Adaptações Manuais

**Implementação Completa Manual**

Toda a lógica foi implementada manualmente:

**1. Entidade Driver**
```java
@Getter
@Setter
@Entity
@Builder
@Table(name = "drivers")
@AllArgsConstructor
@NoArgsConstructor
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "driver_vehicle_types",
            joinColumns = @JoinColumn(name = "driver_id"))
    private List<VehicleType> vehicleType;

    @CPF
    @Column(unique = true)
    private String cpf;
    
    @Column(unique = true)
    private String cnh;
    
    @Column(unique = true)
    private String phoneNumber;
    
    @Column(unique = true)
    private String email;
    
    private boolean available;
    private String city;
    private String state;
}
```

**2. Enum VehicleType**
```java
public enum VehicleType {
    CAR,
    MOTORCYCLE,
    TRUCK,
    VAN
}
```

**3. Repository**
```java
@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID>, 
                                          JpaSpecificationExecutor<Driver> {
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByCnh(String cnh);
}
```

**4. DTOs**
```java
public record DriverRequestDTO(
    @NotBlank String name,
    @Email String email,
    @NotBlank String phone,
    @CPF String cpf,
    @NotBlank String cnh,
    @NotBlank String city,
    @NotBlank String state,
    List<VehicleType> vehicleTypes
) {}

@Builder
public record DriverResponseDTO(
    UUID id,
    String name,
    String email,
    String phone,
    String cpf,
    String cnh,
    String city,
    String state,
    boolean available,
    List<VehicleType> vehicleTypes
) {}
```

---

## 2. Implementação de Filtros Dinâmicos

### Prompt Utilizado
```
Implemente busca de motoristas com filtros opcionais usando JPA Specifications:
- Filtro por texto (nome, email, CPF, CNH, telefone)
- Filtro por estado
- Filtro por cidade
- Filtro por tipos de veículos
Com suporte a paginação.
```

### O que a IA Gerou
- Classe `DriverSpecification` com predicados dinâmicos
- Métodos estáticos para cada tipo de filtro
- Integração com `Pageable` para paginação

```java
public class DriverSpecification {

    public static Specification<Driver> filterBy(String text, String state, 
                                                  String city, List<VehicleType> vehicles) {
        return Specification
                .where(hasText(text))
                .and(hasState(state))
                .and(hasCity(city))
                .and(hasVehicles(vehicles));
    }

    private static Specification<Driver> hasText(String text) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(text)) return null;
            String likePattern = "%" + text + "%";
            return cb.or(
                    cb.like(root.get("name"), likePattern),
                    cb.like(root.get("email"), likePattern),
                    cb.like(root.get("cpf"), likePattern),
                    cb.like(root.get("cnh"), likePattern)
            );
        };
    }
    
    // Outros métodos...
}
```

### Adaptações Manuais

**1. Case-insensitive Search**
```java
// Adicionado manualmente em todos os predicados:
private static Specification<Driver> hasText(String text) {
    return (root, query, cb) -> {
        if (!StringUtils.hasText(text)) return null;
        String likePattern = "%" + text.toLowerCase() + "%";  // toLowerCase
        return cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),  // cb.lower
                cb.like(cb.lower(root.get("email")), likePattern),
                cb.like(cb.lower(root.get("cpf")), likePattern),
                cb.like(cb.lower(root.get("cnh")), likePattern),
                cb.like(cb.lower(root.get("phoneNumber")), likePattern)  // Adicionado
        );
    };
}
```

**2. Filtro de Telefone**
```java
// IA esqueceu phoneNumber, adicionei manualmente:
cb.like(cb.lower(root.get("phoneNumber")), likePattern)
```

---

## 3. DTOs para Segurança e Privacidade

### Prompt Utilizado
```
Crie DTOs separados para listagem e detalhes de motoristas. A listagem não deve 
expor CPF e CNH por questões de privacidade.
```

### O que a IA Gerou
```java
public record DriverSummaryDTO(
    UUID id,
    String name,
    String city,
    String state,
    boolean available,
    List<VehicleType> vehicleTypes
) {}
```

### Adaptações Manuais

**Email na Listagem**
```java
// IA não incluiu email, mas é necessário para contato
public record DriverSummaryDTO(
    UUID id,
    String name,
    String email,  // Adicionado manualmente
    String city,
    String state,
    boolean available,
    List<VehicleType> vehicleTypes
) {}
```

**Mapper Manual**
```java
// Implementado manualmente
@Component
public class DriverMapper {
    
    public DriverResponseDTO toResponse(Driver driver) {
        return DriverResponseDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .email(driver.getEmail())
                .phone(driver.getPhoneNumber())
                .cpf(driver.getCpf())
                .cnh(driver.getCnh())
                .city(driver.getCity())
                .state(driver.getState())
                .available(driver.isAvailable())
                .vehicleTypes(driver.getVehicleType())
                .build();
    }
    
    public DriverSummaryDTO toSummary(Driver driver) {
        return new DriverSummaryDTO(
            driver.getId(),
            driver.getName(),
            driver.getEmail(),
            driver.getCity(),
            driver.getState(),
            driver.isAvailable(),
            driver.getVehicleType()
        );
    }
}
```

---

## 4. Tratamento Global de Exceções

### Prompt Utilizado
```
Implemente GlobalExceptionHandler com @RestControllerAdvice para tratar:
- ResourceNotFoundException (404)
- DuplicateResourceException (409)
- Erros de validação (400)
```

### O que a IA Gerou
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(
        ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(
        DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
```

### Adaptações Manuais

**DTO de Erro Estruturado**
```java
// Substituído ResponseEntity<String> por DTO customizado:
public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp
) {}

@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
    ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now()));
}
```

**Handler de Validação**
```java
// Adicionado manualmente (IA não incluiu):
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
    MethodArgumentNotValidException ex) {
    // Implementação manual
}
```

---

## 5. Autenticação JWT

### Prompt Utilizado
```
Implemente autenticação JWT simples com Spring Security:
- Endpoint /api/auth/login
- JwtTokenProvider para gerar e validar tokens
- JwtAuthenticationFilter para interceptar requisições
- SecurityConfig permitindo acesso público ao login e Swagger
```

### O que a IA Gerou
- `JwtTokenProvider` com geração e validação de tokens
- `JwtAuthenticationFilter` estendendo `OncePerRequestFilter`
- `SecurityConfig` com configuração de segurança
- `AuthController` com endpoint de login

```java
@Component
public class JwtTokenProvider {
    
    private static final long EXPIRE_TIME = 3600000; // 1 hora
    
    @Value("${jwt.secret}")
    protected String secret;
    
    public String genereateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(getSecretKey())
                .compact();
    }
}
```

### Adaptações Manuais

**1. Correção de Typo**
```java
// IA gerou:
public String genereateToken(String username)

// Corrigido para:
public String generateToken(String username)
```

**2. Tempo de Expiração**
```java
// IA definiu 1 hora, ajustado para 24h:
private static final long EXPIRE_TIME = 86400000; // 24 horas
```

**3. Compatibilidade com Spring Boot 4**
```xml
<!-- IA sugeriu versão específica, removido para usar gerenciamento do Spring Boot -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <!-- versão gerenciada automaticamente -->
</dependency>
```

---

## 6. Otimização do Dockerfile

### Prompt Utilizado
```
Crie um Dockerfile otimizado para Java 21 com foco em performance:
- Multi-stage build
- Class Data Sharing (CDS)
- AOT Compilation
- ZGC Garbage Collector
- Imagem Alpine para reduzir tamanho
```

### O que a IA Gerou
```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -Dspring-boot.aot.enabled=true

FROM eclipse-temurin:21-jre-alpine AS cds_builder
RUN java -XX:ArchiveClassesAtExit=application.jsa \
    -Dspring.context.exit=onRefresh \
    org.springframework.boot.loader.launch.JarLauncher

FROM eclipse-temurin:21-jre-alpine
ENTRYPOINT ["java", \
    "-XX:+UseZGC", \
    "-XX:+ZGenerational", \
    "-XX:SharedArchiveFile=application.jsa", \
    "org.springframework.boot.loader.launch.JarLauncher"]
```

### Adaptações Manuais

**1. Usuário Não-Root (Segurança)**
```dockerfile
# Adicionado manualmente:
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY --from=cds_builder --chown=appuser:appgroup /app/ ./

USER appuser
```

**2. Configurações de Memória**
```dockerfile
# Ajustado percentuais de RAM:
"-XX:MaxRAMPercentage=75.0",
"-XX:InitialRAMPercentage=50.0",
```

**3. Flags JVM Adicionais**
```dockerfile
# Adicionados manualmente para melhor performance:
"-XX:+TieredCompilation",
"-XX:TieredStopAtLevel=1",
"-XX:+DisableExplicitGC",
"-Djava.security.egd=file:/dev/./urandom"
```

---

## 7. Docker Compose e PostgreSQL

### Prompt Utilizado
```
Configure docker-compose.yml com:
- Aplicação Spring Boot
- PostgreSQL 16
- Health checks
- Configurações de performance otimizadas
- Limites de recursos
```

### O que a IA Gerou
- Estrutura básica de serviços (app + db)
- Variáveis de ambiente
- Volumes para persistência

```yaml
services:
  app:
    build: ./backend/driver-manager
    ports:
      - "8080:8080"
    depends_on:
      - db
  
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: drivermanager
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
```

### Adaptações Manuais

**1. Health Checks**
```yaml
# Adicionado manualmente para garantir inicialização correta:
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U postgres -d drivermanager"]
  interval: 10s
  timeout: 5s
  retries: 5

depends_on:
  db:
    condition: service_healthy  # Adicionado
```

**2. Tuning PostgreSQL**
```yaml
# Configurações de performance adicionadas manualmente:
command:
  - "postgres"
  - "-c"
  - "shared_buffers=128MB"
  - "-c"
  - "effective_cache_size=384MB"
  - "-c"
  - "max_connections=100"
  - "-c"
  - "work_mem=4MB"
  - "-c"
  - "min_wal_size=1GB"
  - "-c"
  - "max_wal_size=4GB"
```

**3. Limites de Recursos**
```yaml
# Adicionado manualmente:
deploy:
  resources:
    limits:
      cpus: '2'
      memory: 1024M
    reservations:
      cpus: '0.5'
      memory: 512M
```

**4. Simplificação**
```yaml
# IA sugeriu adicionar Redis, Grafana, Prometheus
# Removido manualmente para manter escopo enxuto (apenas app + db)
```

---

## 8. Testes Automatizados

### Prompt Utilizado
```
Crie testes completos para o projeto:
- Testes unitários do DriverService com Mockito
- Testes de integração do DriverController com MockMvc
- Testes de autenticação JWT
- Cobertura de casos de sucesso e falha
```

### O que a IA Gerou
- Estrutura de testes com JUnit 5
- Uso de Mockito para mocks
- MockMvc para testes de controllers
- Testes de autenticação

```java
@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    
    @Mock
    private DriverRepository repository;
    
    @Mock
    private DriverMapper mapper;
    
    @InjectMocks
    private DriverService service;
    
    @Test
    void shouldCreateDriverSuccessfully() {
        // Arrange
        DriverRequestDTO request = new DriverRequestDTO(...);
        Driver driver = new Driver(...);
        
        when(repository.save(any())).thenReturn(driver);
        
        // Act & Assert
        assertThat(service.create(request)).isNotNull();
    }
}
```

### Adaptações Manuais

**1. Annotations Atualizadas**
```java
// IA usou @MockBean (deprecated no Spring Boot 4), corrigido para:
@MockitoBean
private DriverService driverService;

// IA usou @WebMvcTest simples, especifiquei a classe:
@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(DriverController.class)
```

**2. Dados de Teste Válidos**
```java
// IA usou CPF sequencial inválido "12345678901"
// Substituído por CPF válido:
.cpf("52998224725")
```

**3. Casos de Teste Adicionais**
```java
// Adicionados manualmente:
@Test
void shouldThrowExceptionWhenDuplicateCpf() {
    when(repository.existsByCpf(anyString())).thenReturn(true);
    assertThrows(DuplicateResourceException.class, () -> service.create(dto));
}

@Test
void shouldThrowExceptionWhenDriverNotFound() {
    when(repository.findById(any())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> service.getById(UUID.randomUUID()));
}
```

---

## 9. Frontend Next.js

### Prompt Utilizado
```
Crie interface Next.js para gerenciamento de motoristas:
- Página de listagem com filtros e paginação
- Formulário de cadastro/edição
- Integração com API REST
- Autenticação JWT
- Validação de formulários
- Design responsivo com Tailwind CSS
```

### O que a IA Gerou
- Estrutura de páginas Next.js
- Componentes de formulário
- Hook customizado para autenticação
- Service layer para chamadas HTTP
- Componentes de UI (tabela, filtros, modals)

### Adaptações Manuais

**1. Validação de CPF/CNH**
```typescript
// Adicionado validação brasileira manualmente:
const validateCPF = (cpf: string): boolean => {
  const cleaned = cpf.replace(/\D/g, '');
  if (cleaned.length !== 11) return false;
  
  // Implementação completa do algoritmo de validação de CPF
  let sum = 0;
  for (let i = 0; i < 9; i++) {
    sum += parseInt(cleaned.charAt(i)) * (10 - i);
  }
  let remainder = 11 - (sum % 11);
  let digit1 = remainder >= 10 ? 0 : remainder;
  
  // Validação do segundo dígito...
  return true; // ou false
}
```

**2. Tratamento de Erros HTTP**
```typescript
// Melhorado tratamento de erros:
try {
  await api.post('/drivers', data);
  showToast('Motorista criado com sucesso', 'success');
} catch (error) {
  if (error.response?.status === 409) {
    setError('CPF, CNH ou Email já cadastrados');
  } else if (error.response?.status === 400) {
    setError('Dados inválidos. Verifique os campos.');
  } else {
    setError('Erro ao cadastrar motorista');
  }
}
```

**3. UX e Loading States**
```typescript
// Adicionado manualmente:
const [isLoading, setIsLoading] = useState(false);
const [toast, setToast] = useState<Toast | null>(null);

const handleSubmit = async (data: FormData) => {
  setIsLoading(true);
  try {
    // ...
  } finally {
    setIsLoading(false);
  }
}
```

---

## 10. População do Banco de Dados

### Prompt Utilizado
```
Crie script SQL para popular banco de dados com dados de teste:
- 50 motoristas fictícios
- Dados realistas (nomes brasileiros, CPFs válidos, endereços)
- Distribuição variada de tipos de veículos
- Estados e cidades do Brasil
```

### O que a IA Gerou
```sql
INSERT INTO drivers (id, name, email, cpf, cnh, phone_number, city, state, available) 
VALUES 
  (gen_random_uuid(), 'João Silva', 'joao.silva@email.com', '12345678901', '12345678900', '11999999999', 'São Paulo', 'SP', true),
  (gen_random_uuid(), 'Maria Santos', 'maria.santos@email.com', '12345678902', '12345678901', '11999999998', 'Rio de Janeiro', 'RJ', true);
```

### Adaptações Manuais

**1. CPFs Válidos**
```sql
-- IA gerou CPFs sequenciais inválidos
-- Substituído por CPFs válidos manualmente:
INSERT INTO drivers VALUES 
  (gen_random_uuid(), 'João Silva', 'joao@email.com', '52998224725', '12345678900', '11999999999', 'São Paulo', 'SP', true),
  (gen_random_uuid(), 'Maria Santos', 'maria@email.com', '73951394620', '98765432100', '21999999999', 'Rio de Janeiro', 'RJ', true),
  (gen_random_uuid(), 'Pedro Oliveira', 'pedro@email.com', '28476215380', '45678912300', '31999999999', 'Belo Horizonte', 'MG', true);
```

**2. Dados Geográficos Reais**
```sql
-- Ajustado para cidades e estados reais do Brasil:
('São Paulo', 'SP'),
('Rio de Janeiro', 'RJ'),
('Belo Horizonte', 'MG'),
('Curitiba', 'PR'),
('Porto Alegre', 'RS'),
('Salvador', 'BA'),
('Brasília', 'DF'),
('Fortaleza', 'CE'),
('Recife', 'PE'),
('Manaus', 'AM')
```

**3. Tabela de Tipos de Veículos**
```sql
-- Implementado manualmente insert na tabela de associação:
INSERT INTO driver_vehicle_types (driver_id, vehicle_type) VALUES
  ((SELECT id FROM drivers WHERE email = 'joao@email.com'), 'CAR'),
  ((SELECT id FROM drivers WHERE email = 'joao@email.com'), 'MOTORCYCLE'),
  ((SELECT id FROM drivers WHERE email = 'maria@email.com'), 'TRUCK'),
  ((SELECT id FROM drivers WHERE email = 'pedro@email.com'), 'VAN'),
  ((SELECT id FROM drivers WHERE email = 'pedro@email.com'), 'CAR');
```

---

## 11. Documentação do Projeto

### Prompt Utilizado
```
Crie um README.md completo para o projeto incluindo:
- Descrição do projeto
- Tecnologias utilizadas
- Instruções de instalação e execução
- Documentação da API
- Decisões técnicas justificadas
```

### O que a IA Gerou
- README estruturado com seções organizadas
- Badges de tecnologias
- Instruções de instalação
- Exemplos de uso da API
- Seção de decisões técnicas

### Adaptações Manuais

**1. Linguagem Menos Formal**
```markdown
<!-- IA gerou muito formal e corporativo -->
<!-- Ajustado manualmente para linguagem mais direta e profissional -->
```

**2. Remoção de Emojis**
```markdown
<!-- IA usou muitos emojis -->
<!-- Removidos manualmente para manter profissionalismo -->
```

**3. Ajuste de Informações Pessoais**
```markdown
<!-- IA não tinha meu nome correto -->
**Victor Gabriel**
- GitHub: [@vitinh0z](https://github.com/vitinh0z)
```

**4. Seção de Decisões Técnicas**
```markdown
<!-- Refinado manualmente para mostrar decisões reais tomadas no projeto -->
<!-- Adicionado justificativas técnicas baseadas no contexto real -->
```

---

## 12. Este Documento (AI_PROMPT.md)

### Prompt Utilizado
```
Crie um documento AI_JOURNEY.md detalhando:
- Prompts utilizados no desenvolvimento
- O que cada prompt gerou
- Adaptações manuais realizadas
- Estatísticas de uso da IA
```

### O que a IA Gerou
- Estrutura inicial do documento
- Seções organizadas por funcionalidade
- Exemplos de código
- Tabela de estatísticas

### Adaptações Manuais

**1. Correção de Informações**
```markdown
<!-- IA assumiu que tinha gerado código completo na estrutura inicial -->
<!-- Corrigido para refletir que apenas gerou pastas e classes vazias -->
```

**2. Detalhamento Real**
```markdown
<!-- Adicionado detalhes específicos do que realmente aconteceu -->
<!-- Removido exemplos genéricos, mantido apenas código real do projeto -->
```

**3. Ajuste de Tom**
```markdown
<!-- Primeiro estava muito formal/robótico -->
<!-- Depois muito informal/coloquial -->
<!-- Ajustado manualmente para tom técnico profissional -->
```

---

## 13 - Automatização de Issue
### Prompt Utilizado
```marwdown
Crie um arquivo .sh com Issue para organizar o meu projeto
```
### O que a IA Gerou
- Crie Issues
- Minestone
- Descrição das issues

## Estatísticas de Uso

| Componente | Gerado por IA | Implementado Manualmente | Criticidade dos Ajustes |
|------------|---------------|-------------------------|------------------------|
| Estrutura de Pastas | 100% | 0% | Baixa |
| Entidades e Models | 0% | 100% | Crítica |
| Filtros/Specifications | 75% | 25% | Média |
| DTOs | 60% | 40% | Média |
| Exception Handler | 70% | 30% | Média |
| JWT Authentication | 80% | 20% | Alta |
| Dockerfile | 60% | 40% | Alta |
| Docker Compose | 50% | 50% | Alta |
| Testes | 70% | 30% | Média |
| Frontend Next.js | 65% | 35% | Alta |
| Scripts SQL | 30% | 70% | Alta |
| README.md | 75% | 25% | Baixa |
| AI_PROMPT.md | 80% | 20% | Baixa |

**Resumo Geral:**
- Código gerado pela IA: ~60%
- Código implementado/adaptado manualmente: ~40%
- Tempo economizado estimado: 30-35%
- Bugs introduzidos pela IA: 12
- Bugs corrigidos manualmente: 12
- Features adicionadas além do proposto pela IA: 8

---

## Conclusão

A IA foi fundamental para:
- Acelerar criação de estrutura e boilerplate
- Sugerir padrões e boas práticas (Specifications, multi-stage Docker)
- Propor otimizações de performance (ZGC, CDS, AOT)
- Gerar testes automatizados

Porém, foi necessária intervenção manual em:
- Toda implementação de lógica de negócio
- Validações específicas do domínio brasileiro (CPF)
- Correções de segurança e privacidade
- Otimizações de performance específicas
- Ajustes de compatibilidade de versões
- Dados realistas e contextualizados

---

<div align="center">
  <strong>Victor Gabriel</strong><br>
  FreteMais Driver Manager - 2025
</div>
