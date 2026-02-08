package com.fretemais.drivermanager.integration;

import com.fretemais.drivermanager.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Integração - Autenticação e Acesso à API")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String DRIVERS_URL = "/api/drivers";

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Deve retornar token JWT com credenciais válidas")
        void shouldReturnTokenWithValidCredentials() throws Exception {
            String loginJson = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

            MvcResult result = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = result.getResponse().getContentAsString();

            assertThat(token).isNotBlank();
            assertThat(jwtTokenProvider.validateToken(token)).isEqualTo("admin");
        }

        @Test
        @DisplayName("Deve retornar 401 com senha incorreta")
        void shouldReturn401WithWrongPassword() throws Exception {
            String loginJson = """
                {
                    "username": "admin",
                    "password": "senhaerrada"
                }
                """;

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 com usuário incorreto")
        void shouldReturn401WithWrongUsername() throws Exception {
            String loginJson = """
                {
                    "username": "usuario_invalido",
                    "password": "123456"
                }
                """;

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Deve retornar 401 com credenciais vazias")
        void shouldReturn401WithEmptyCredentials() throws Exception {
            String loginJson = """
                {
                    "username": "",
                    "password": ""
                }
                """;

            mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Acesso à API de Motoristas com Token")
    class DriverApiAccessTests {

        @Test
        @DisplayName("Deve acessar API de motoristas com token válido")
        void shouldAccessDriversApiWithValidToken() throws Exception {
            // Primeiro faz login
            String loginJson = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

            MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = loginResult.getResponse().getContentAsString();

            // Acessa a API de motoristas com o token
            mockMvc.perform(get(DRIVERS_URL)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Deve negar acesso à API de motoristas sem token")
        void shouldDenyAccessWithoutToken() throws Exception {
            mockMvc.perform(get(DRIVERS_URL))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve negar acesso à API de motoristas com token inválido")
        void shouldDenyAccessWithInvalidToken() throws Exception {
            mockMvc.perform(get(DRIVERS_URL)
                            .header("Authorization", "Bearer token_invalido_123"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve criar motorista com token válido")
        void shouldCreateDriverWithValidToken() throws Exception {
            // Primeiro faz login
            String loginJson = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

            MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = loginResult.getResponse().getContentAsString();

            // Cria motorista
            String driverJson = """
                {
                    "name": "João Silva",
                    "email": "joao.silva@email.com",
                    "phone": "11999999999",
                    "cpf": "52998224725",
                    "cnh": "12345678900",
                    "cnhCategory": "B",
                    "cnhExpiration": "2027-12-31",
                    "state": "SP",
                    "city": "São Paulo",
                    "vehicleTypes": ["TRUCK", "CAR"]
                }
                """;

            mockMvc.perform(post(DRIVERS_URL)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(driverJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("João Silva"))
                    .andExpect(jsonPath("$.email").value("joao.silva@email.com"));
        }
    }

    @Nested
    @DisplayName("Fluxo Completo de Autenticação")
    class FullAuthFlowTests {

        @Test
        @DisplayName("Deve completar fluxo: login -> criar motorista -> listar motoristas")
        void shouldCompleteFullAuthFlow() throws Exception {
            // 1. Login
            String loginJson = """
                {
                    "username": "admin",
                    "password": "123456"
                }
                """;

            MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String token = loginResult.getResponse().getContentAsString();
            assertThat(token).isNotBlank();

            // 2. Criar motorista
            String driverJson = """
                {
                    "name": "Maria Santos",
                    "email": "maria.santos@email.com",
                    "phone": "11988888888",
                    "cpf": "71428793860",
                    "cnh": "98765432100",
                    "cnhCategory": "AB",
                    "cnhExpiration": "2028-06-15",
                    "state": "RJ",
                    "city": "Rio de Janeiro",
                    "vehicleTypes": ["CAR", "MOTORCYCLE"]
                }
                """;

            mockMvc.perform(post(DRIVERS_URL)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(driverJson))
                    .andExpect(status().isCreated());

            // 3. Listar motoristas
            mockMvc.perform(get(DRIVERS_URL)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isNotEmpty());
        }
    }
}

