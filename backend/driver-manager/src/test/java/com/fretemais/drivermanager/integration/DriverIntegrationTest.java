package com.fretemais.drivermanager.integration;

import com.fretemais.drivermanager.domain.model.Driver;
import com.fretemais.drivermanager.infrastructure.persistence.DriverRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - API de Motoristas")
class DriverIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    private String validRequestJson;

    @BeforeEach
    void setUp() {
        driverRepository.deleteAll();

        validRequestJson = """
            {
                "name": "João Silva",
                "email": "joao.silva@email.com",
                "phone": "11999999999",
                "cpf": "52998224725",
                "cnh": "12345678900",
                "city": "São Paulo",
                "state": "SP",
                "vehicleTypes": ["CAR", "MOTORCYCLE"]
            }
            """;
    }

    private UUID extractIdFromResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        Pattern pattern = Pattern.compile("\"id\":\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return UUID.fromString(matcher.group(1));
        }
        throw new RuntimeException("Could not extract ID from response");
    }

    @Nested
    @DisplayName("Fluxo completo de CRUD")
    class CrudFlowTests {

        @Test
        @WithMockUser
        @DisplayName("Deve executar fluxo completo: criar, buscar, atualizar e deletar motorista")
        void shouldExecuteCompleteCrudFlow() throws Exception {
            // 1. CRIAR motorista
            MvcResult createResult = mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andReturn();

            UUID driverId = extractIdFromResponse(createResult);

            // Verificar no banco
            assertThat(driverRepository.findById(driverId)).isPresent();

            // 2. BUSCAR motorista por ID
            mockMvc.perform(get("/api/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId.toString()))
                    .andExpect(jsonPath("$.name").value("João Silva"));

            // 3. LISTAR motoristas
            mockMvc.perform(get("/api/drivers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id").value(driverId.toString()));

            // 4. ATUALIZAR motorista
            String updateJson = """
                {
                    "name": "João Silva Atualizado",
                    "email": "joao.atualizado@email.com",
                    "phone": "11988888888",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["TRUCK"]
                }
                """;

            mockMvc.perform(put("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("João Silva Atualizado"))
                    .andExpect(jsonPath("$.email").value("joao.atualizado@email.com"))
                    .andExpect(jsonPath("$.city").value("Rio de Janeiro"))
                    .andExpect(jsonPath("$.state").value("RJ"));

            // Verificar atualização no banco
            Driver updatedDriver = driverRepository.findById(driverId).orElseThrow();
            assertThat(updatedDriver.getName()).isEqualTo("João Silva Atualizado");

            // 5. DELETAR motorista
            mockMvc.perform(delete("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verificar deleção no banco
            assertThat(driverRepository.findById(driverId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de criação")
    class CreateTests {

        @Test
        @WithMockUser
        @DisplayName("Deve criar motorista e persistir no banco")
        void shouldCreateAndPersistDriver() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            UUID driverId = extractIdFromResponse(result);

            assertThat(driverId).isNotNull();
            assertThat(driverRepository.count()).isEqualTo(1);

            Driver savedDriver = driverRepository.findById(driverId).orElseThrow();
            assertThat(savedDriver.getName()).isEqualTo("João Silva");
            assertThat(savedDriver.getEmail()).isEqualTo("joao.silva@email.com");
            assertThat(savedDriver.isAvailable()).isTrue();
        }

        @Test
        @WithMockUser
        @DisplayName("Deve rejeitar criação com email duplicado")
        void shouldRejectDuplicateEmail() throws Exception {
            // Criar primeiro motorista
            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated());

            // Criar segundo motorista com mesmo email
            String duplicateEmailJson = """
                {
                    "name": "Maria Santos",
                    "email": "joao.silva@email.com",
                    "phone": "21988888888",
                    "cpf": "71428793860",
                    "cnh": "98765432100",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateEmailJson))
                    .andExpect(status().isInternalServerError());

            assertThat(driverRepository.count()).isEqualTo(1);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve rejeitar criação com CPF duplicado")
        void shouldRejectDuplicateCpf() throws Exception {
            // Criar primeiro motorista
            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated());

            // Criar segundo motorista com mesmo CPF
            String duplicateCpfJson = """
                {
                    "name": "Maria Santos",
                    "email": "maria.santos@email.com",
                    "phone": "21988888888",
                    "cpf": "52998224725",
                    "cnh": "98765432100",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateCpfJson))
                    .andExpect(status().isInternalServerError());

            assertThat(driverRepository.count()).isEqualTo(1);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve rejeitar criação com CNH duplicada")
        void shouldRejectDuplicateCnh() throws Exception {
            // Criar primeiro motorista
            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated());

            // Criar segundo motorista com mesma CNH
            String duplicateCnhJson = """
                {
                    "name": "Maria Santos",
                    "email": "maria.santos@email.com",
                    "phone": "21988888888",
                    "cpf": "71428793860",
                    "cnh": "12345678900",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(duplicateCnhJson))
                    .andExpect(status().isInternalServerError());

            assertThat(driverRepository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Testes de listagem e filtros")
    class ListAndFilterTests {

        @BeforeEach
        void setUpDrivers() throws Exception {
            String driver1 = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": ["CAR", "MOTORCYCLE"]
                }
                """;

            String driver2 = """
                {
                    "name": "Maria Santos",
                    "email": "maria.santos@email.com",
                    "phone": "21988888888",
                    "cpf": "71428793860",
                    "cnh": "98765432100",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["TRUCK"]
                }
                """;

            String driver3 = """
                {
                    "name": "Carlos Oliveira",
                    "email": "carlos.oliveira@email.com",
                    "phone": "31977777777",
                    "cpf": "87748248800",
                    "cnh": "11122233344",
                    "city": "Belo Horizonte",
                    "state": "MG",
                    "vehicleTypes": ["CAR"]
                }
                """;

            for (String driverJson : List.of(driver1, driver2, driver3)) {
                mockMvc.perform(post("/api/drivers")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(driverJson))
                        .andExpect(status().isCreated());
            }
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar todos os motoristas")
        void shouldListAllDrivers() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(3)))
                    .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve filtrar por texto no nome")
        void shouldFilterByTextInName() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("text", "João")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("João Silva"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve filtrar por estado")
        void shouldFilterByState() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("state", "SP")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].state").value("SP"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve filtrar por cidade")
        void shouldFilterByCity() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("city", "Rio")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].city").value("Rio de Janeiro"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve filtrar por tipo de veículo")
        void shouldFilterByVehicleType() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("vehicles", "TRUCK")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("Maria Santos"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve aplicar paginação corretamente")
        void shouldApplyPaginationCorrectly() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("page", "0")
                            .param("size", "2")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.totalPages").value(2));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve combinar múltiplos filtros")
        void shouldCombineMultipleFilters() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .param("state", "SP")
                            .param("vehicles", "CAR")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].name").value("João Silva"));
        }
    }

    @Nested
    @DisplayName("Testes de atualização")
    class UpdateTests {

        private UUID driverId;

        @BeforeEach
        void setUpDriver() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            driverId = extractIdFromResponse(result);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve atualizar motorista mantendo mesmo email")
        void shouldUpdateDriverKeepingSameEmail() throws Exception {
            String updateJson = """
                {
                    "name": "João Silva Atualizado",
                    "email": "joao.silva@email.com",
                    "phone": "11988888888",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "Campinas",
                    "state": "SP",
                    "vehicleTypes": ["TRUCK"]
                }
                """;

            mockMvc.perform(put("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("João Silva Atualizado"))
                    .andExpect(jsonPath("$.city").value("Campinas"));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve rejeitar atualização para email já existente")
        void shouldRejectUpdateToExistingEmail() throws Exception {
            // Criar segundo motorista
            String secondDriver = """
                {
                    "name": "Maria Santos",
                    "email": "maria.santos@email.com",
                    "phone": "21988888888",
                    "cpf": "71428793860",
                    "cnh": "98765432100",
                    "city": "Rio de Janeiro",
                    "state": "RJ",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(secondDriver))
                    .andExpect(status().isCreated());

            // Tentar atualizar primeiro motorista com email do segundo
            String updateJson = """
                {
                    "name": "João Silva",
                    "email": "maria.santos@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(put("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Testes de deleção")
    class DeleteTests {

        @Test
        @WithMockUser
        @DisplayName("Deve deletar motorista e remover do banco")
        void shouldDeleteDriverAndRemoveFromDatabase() throws Exception {
            MvcResult result = mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated())
                    .andReturn();

            UUID driverId = extractIdFromResponse(result);
            assertThat(driverRepository.count()).isEqualTo(1);

            mockMvc.perform(delete("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            assertThat(driverRepository.count()).isZero();
            assertThat(driverRepository.findById(driverId)).isEmpty();
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar erro ao deletar motorista inexistente")
        void shouldReturnErrorWhenDeletingNonExistentDriver() throws Exception {
            UUID nonExistentId = UUID.randomUUID();

            mockMvc.perform(delete("/api/drivers/{id}", nonExistentId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("Testes de validação de dados")
    class ValidationTests {

        @Test
        @WithMockUser
        @DisplayName("Deve validar todos os campos obrigatórios")
        void shouldValidateAllRequiredFields() throws Exception {
            String emptyJson = """
                {
                    "name": "",
                    "email": "",
                    "phone": "",
                    "cpf": "",
                    "cnh": "",
                    "city": "",
                    "state": "",
                    "vehicleTypes": []
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve validar formato de email")
        void shouldValidateEmailFormat() throws Exception {
            String invalidEmailJson = """
                {
                    "name": "João Silva",
                    "email": "email-invalido",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidEmailJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve validar CPF inválido")
        void shouldValidateInvalidCpf() throws Exception {
            String invalidCpfJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "12345678900",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidCpfJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve validar UF com tamanho incorreto")
        void shouldValidateStateWithInvalidSize() throws Exception {
            String invalidStateJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SAO",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidStateJson))
                    .andExpect(status().isBadRequest());
        }
    }
}

