package com.fretemais.drivermanager.infrastructure.controllers;

import com.fretemais.drivermanager.application.dtos.DriverRequestDTO;
import com.fretemais.drivermanager.application.dtos.DriverResponseDTO;
import com.fretemais.drivermanager.application.services.DriverService;
import com.fretemais.drivermanager.domain.enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(DriverController.class)
@DisplayName("DriverController - Testes de API")
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

    private DriverResponseDTO responseDTO;
    private UUID driverId;
    private String validRequestJson;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();

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

        responseDTO = DriverResponseDTO.builder()
                .id(driverId)
                .name("João Silva")
                .email("joao.silva@email.com")
                .phone("11999999999")
                .cpf("52998224725")
                .cnh("12345678900")
                .city("São Paulo")
                .state("SP")
                .available(true)
                .vehicleTypes(List.of(VehicleType.CAR, VehicleType.MOTORCYCLE))
                .build();
    }

    @Nested
    @DisplayName("POST /api/drivers - Criar Motorista")
    class CreateDriverTests {

        @Test
        @WithMockUser
        @DisplayName("Deve criar motorista com sucesso e retornar 201 CREATED")
        void shouldCreateDriverSuccessfully() throws Exception {
            when(driverService.create(any(DriverRequestDTO.class))).thenReturn(responseDTO);

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(driverId.toString()))
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao.silva@email.com"))
                    .andExpect(jsonPath("$.phone").value("11999999999"))
                    .andExpect(jsonPath("$.cpf").value("52998224725"))
                    .andExpect(jsonPath("$.cnh").value("12345678900"))
                    .andExpect(jsonPath("$.city").value("São Paulo"))
                    .andExpect(jsonPath("$.state").value("SP"))
                    .andExpect(jsonPath("$.available").value(true))
                    .andExpect(jsonPath("$.vehicleTypes", hasSize(2)));

            verify(driverService).create(any(DriverRequestDTO.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando nome está em branco")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            String invalidJson = """
                {
                    "name": "",
                    "email": "joao.silva@email.com",
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando email é inválido")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            String invalidJson = """
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando CPF é inválido")
        void shouldReturn400WhenCpfIsInvalid() throws Exception {
            String invalidJson = """
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando UF tem tamanho incorreto")
        void shouldReturn400WhenStateHasInvalidSize() throws Exception {
            String invalidJson = """
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando lista de veículos está vazia")
        void shouldReturn400WhenVehicleTypesIsEmpty() throws Exception {
            String invalidJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": []
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando telefone está em branco")
        void shouldReturn400WhenPhoneIsBlank() throws Exception {
            String invalidJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "",
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando CNH está em branco")
        void shouldReturn400WhenCnhIsBlank() throws Exception {
            String invalidJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "",
                    "city": "São Paulo",
                    "state": "SP",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando cidade está em branco")
        void shouldReturn400WhenCityIsBlank() throws Exception {
            String invalidJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "city": "",
                    "state": "SP",
                    "vehicleTypes": ["CAR"]
                }
                """;

            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).create(any());
        }
    }

    @Nested
    @DisplayName("GET /api/drivers - Listar Motoristas")
    class ListDriversTests {

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com paginação padrão")
        void shouldListDriversWithDefaultPagination() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(0, 10),
                    1
            );

            when(driverService.list(any(), any(), any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id").value(driverId.toString()))
                    .andExpect(jsonPath("$.content[0].name").value("João Silva"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(driverService).list(any(), any(), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com filtro de texto")
        void shouldListDriversWithTextFilter() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(0, 10),
                    1
            );

            when(driverService.list(eq("João"), any(), any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .param("text", "João")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(driverService).list(eq("João"), any(), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com filtro de estado")
        void shouldListDriversWithStateFilter() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(0, 10),
                    1
            );

            when(driverService.list(any(), eq("SP"), any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .param("state", "SP")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(driverService).list(any(), eq("SP"), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com filtro de cidade")
        void shouldListDriversWithCityFilter() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(0, 10),
                    1
            );

            when(driverService.list(any(), any(), eq("São Paulo"), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .param("city", "São Paulo")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(driverService).list(any(), any(), eq("São Paulo"), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com filtro de veículos")
        void shouldListDriversWithVehicleFilter() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(0, 10),
                    1
            );

            when(driverService.list(any(), any(), any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .param("vehicles", "CAR", "MOTORCYCLE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)));

            verify(driverService).list(any(), any(), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar página vazia quando não há motoristas")
        void shouldReturnEmptyPageWhenNoDrivers() throws Exception {
            Page<DriverResponseDTO> emptyPage = new PageImpl<>(
                    List.of(),
                    PageRequest.of(0, 10),
                    0
            );

            when(driverService.list(any(), any(), any(), any(), any())).thenReturn(emptyPage);

            mockMvc.perform(get("/api/drivers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(driverService).list(any(), any(), any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve listar motoristas com paginação customizada")
        void shouldListDriversWithCustomPagination() throws Exception {
            Page<DriverResponseDTO> page = new PageImpl<>(
                    List.of(responseDTO),
                    PageRequest.of(1, 5),
                    6
            );

            when(driverService.list(any(), any(), any(), any(), any())).thenReturn(page);

            mockMvc.perform(get("/api/drivers")
                            .param("page", "1")
                            .param("size", "5")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.size").value(5));

            verify(driverService).list(any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/drivers/{id} - Buscar Motorista por ID")
    class GetDriverByIdTests {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar motorista quando ID existe")
        void shouldReturnDriverWhenIdExists() throws Exception {
            when(driverService.getById(driverId)).thenReturn(responseDTO);

            mockMvc.perform(get("/api/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId.toString()))
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao.silva@email.com"));

            verify(driverService).getById(driverId);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 500 quando motorista não existe")
        void shouldReturn500WhenDriverNotExists() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            when(driverService.getById(nonExistentId))
                    .thenThrow(new IllegalArgumentException("Motorista não encontrado"));

            mockMvc.perform(get("/api/drivers/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(driverService).getById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("PUT /api/drivers/{id} - Atualizar Motorista")
    class UpdateDriverTests {

        @Test
        @WithMockUser
        @DisplayName("Deve atualizar motorista com sucesso")
        void shouldUpdateDriverSuccessfully() throws Exception {
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

            DriverResponseDTO updatedResponse = DriverResponseDTO.builder()
                    .id(driverId)
                    .name("João Silva Atualizado")
                    .email("joao.atualizado@email.com")
                    .phone("11988888888")
                    .cpf("52998224725")
                    .cnh("12345678900")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .available(true)
                    .vehicleTypes(List.of(VehicleType.TRUCK))
                    .build();

            when(driverService.updateById(eq(driverId), any(DriverRequestDTO.class)))
                    .thenReturn(updatedResponse);

            mockMvc.perform(put("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(driverId.toString()))
                    .andExpect(jsonPath("$.name").value("João Silva Atualizado"))
                    .andExpect(jsonPath("$.email").value("joao.atualizado@email.com"))
                    .andExpect(jsonPath("$.city").value("Rio de Janeiro"))
                    .andExpect(jsonPath("$.state").value("RJ"));

            verify(driverService).updateById(eq(driverId), any(DriverRequestDTO.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 BAD REQUEST quando dados são inválidos na atualização")
        void shouldReturn400WhenUpdateDataIsInvalid() throws Exception {
            String invalidJson = """
                {
                    "name": "",
                    "email": "joao.silva@email.com",
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
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());

            verify(driverService, never()).updateById(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 500 quando motorista não existe na atualização")
        void shouldReturn500WhenDriverNotExistsOnUpdate() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            when(driverService.updateById(eq(nonExistentId), any(DriverRequestDTO.class)))
                    .thenThrow(new IllegalArgumentException("Motorista não encontrado"));

            mockMvc.perform(put("/api/drivers/{id}", nonExistentId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isInternalServerError());

            verify(driverService).updateById(eq(nonExistentId), any(DriverRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/drivers/{id} - Deletar Motorista")
    class DeleteDriverTests {

        @Test
        @WithMockUser
        @DisplayName("Deve deletar motorista com sucesso e retornar 204 NO CONTENT")
        void shouldDeleteDriverSuccessfully() throws Exception {
            doNothing().when(driverService).deleteById(driverId);

            mockMvc.perform(delete("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(driverService).deleteById(driverId);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 500 quando motorista não existe ao deletar")
        void shouldReturn500WhenDriverNotExistsOnDelete() throws Exception {
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new IllegalArgumentException("Motorista não encontrado"))
                    .when(driverService).deleteById(nonExistentId);

            mockMvc.perform(delete("/api/drivers/{id}", nonExistentId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(driverService).deleteById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("Testes de Segurança")
    class SecurityTests {

        @Test
        @DisplayName("Deve retornar 401 UNAUTHORIZED quando não autenticado no POST")
        void shouldReturn401WhenNotAuthenticatedOnPost() throws Exception {
            mockMvc.perform(post("/api/drivers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isUnauthorized());

            verify(driverService, never()).create(any());
        }

        @Test
        @DisplayName("Deve retornar 401 UNAUTHORIZED quando não autenticado no GET lista")
        void shouldReturn401WhenNotAuthenticatedOnGetList() throws Exception {
            mockMvc.perform(get("/api/drivers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(driverService, never()).list(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("Deve retornar 401 UNAUTHORIZED quando não autenticado no GET por ID")
        void shouldReturn401WhenNotAuthenticatedOnGetById() throws Exception {
            mockMvc.perform(get("/api/drivers/{id}", driverId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(driverService, never()).getById(any());
        }

        @Test
        @DisplayName("Deve retornar 401 UNAUTHORIZED quando não autenticado no PUT")
        void shouldReturn401WhenNotAuthenticatedOnPut() throws Exception {
            mockMvc.perform(put("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson))
                    .andExpect(status().isUnauthorized());

            verify(driverService, never()).updateById(any(), any());
        }

        @Test
        @DisplayName("Deve retornar 401 UNAUTHORIZED quando não autenticado no DELETE")
        void shouldReturn401WhenNotAuthenticatedOnDelete() throws Exception {
            mockMvc.perform(delete("/api/drivers/{id}", driverId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            verify(driverService, never()).deleteById(any());
        }
    }
}

