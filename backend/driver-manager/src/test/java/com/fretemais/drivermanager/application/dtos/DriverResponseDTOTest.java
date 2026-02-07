package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DriverResponseDTO - Testes de Record")
class DriverResponseDTOTest {

    @Nested
    @DisplayName("Testes de criação")
    class CreationTests {

        @Test
        @DisplayName("Deve criar DTO com todos os campos")
        void shouldCreateDtoWithAllFields() {

            UUID id = UUID.randomUUID();
            List<VehicleType> vehicles = List.of(VehicleType.CAR, VehicleType.MOTORCYCLE);

            DriverResponseDTO dto = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(vehicles)
                    .build();

            assertThat(dto.id()).isEqualTo(id);
            assertThat(dto.name()).isEqualTo("João Silva");
            assertThat(dto.email()).isEqualTo("joao.silva@email.com");
            assertThat(dto.phone()).isEqualTo("11999999999");
            assertThat(dto.cpf()).isEqualTo("12345678901");
            assertThat(dto.cnh()).isEqualTo("12345678900");
            assertThat(dto.city()).isEqualTo("São Paulo");
            assertThat(dto.state()).isEqualTo("SP");
            assertThat(dto.available()).isTrue();
            assertThat(dto.vehicleTypes()).isEqualTo(vehicles);
        }

        @Test
        @DisplayName("Deve criar DTO com motorista indisponível")
        void shouldCreateDtoWithUnavailableDriver() {
            // Act
            DriverResponseDTO dto = DriverResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .name("Maria Santos")
                    .email("maria.santos@email.com")
                    .phone("21988888888")
                    .cpf("98765432100")
                    .cnh("98765432100")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .available(false)
                    .vehicleTypes(List.of(VehicleType.TRUCK))
                    .build();

            assertThat(dto.available()).isFalse();
        }

        @Test
        @DisplayName("Deve criar DTO com lista vazia de veículos")
        void shouldCreateDtoWithEmptyVehicleList() {

            DriverResponseDTO dto = DriverResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .name("Carlos Oliveira")
                    .vehicleTypes(List.of())
                    .build();

            assertThat(dto.vehicleTypes()).isEmpty();
        }

        @Test
        @DisplayName("Deve criar DTO com todos os tipos de veículo")
        void shouldCreateDtoWithAllVehicleTypes() {
            // Act
            DriverResponseDTO dto = DriverResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .name("Ana Costa")
                    .vehicleTypes(List.of(VehicleType.CAR, VehicleType.MOTORCYCLE, VehicleType.TRUCK))
                    .build();

            // Assert
            assertThat(dto.vehicleTypes()).hasSize(3);
            assertThat(dto.vehicleTypes()).containsExactlyInAnyOrder(
                    VehicleType.CAR,
                    VehicleType.MOTORCYCLE,
                    VehicleType.TRUCK
            );
        }
    }

    @Nested
    @DisplayName("Testes de acesso aos campos")
    class FieldAccessTests {

        private final UUID id = UUID.randomUUID();
        private final List<VehicleType> vehicles = List.of(VehicleType.CAR);
        private final DriverResponseDTO dto = DriverResponseDTO.builder()
                .id(id)
                .name("João Silva")
                .email("joao.silva@email.com")
                .phone("11999999999")
                .cpf("12345678901")
                .cnh("12345678900")
                .city("São Paulo")
                .state("SP")
                .available(true)
                .vehicleTypes(vehicles)
                .build();

        @Test
        @DisplayName("Deve acessar id corretamente")
        void shouldAccessIdCorrectly() {
            assertThat(dto.id()).isEqualTo(id);
        }

        @Test
        @DisplayName("Deve acessar name corretamente")
        void shouldAccessNameCorrectly() {
            assertThat(dto.name()).isEqualTo("João Silva");
        }

        @Test
        @DisplayName("Deve acessar email corretamente")
        void shouldAccessEmailCorrectly() {
            assertThat(dto.email()).isEqualTo("joao.silva@email.com");
        }

        @Test
        @DisplayName("Deve acessar phone corretamente")
        void shouldAccessPhoneCorrectly() {
            assertThat(dto.phone()).isEqualTo("11999999999");
        }

        @Test
        @DisplayName("Deve acessar cpf corretamente")
        void shouldAccessCpfCorrectly() {
            assertThat(dto.cpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("Deve acessar cnh corretamente")
        void shouldAccessCnhCorrectly() {
            assertThat(dto.cnh()).isEqualTo("12345678900");
        }

        @Test
        @DisplayName("Deve acessar city corretamente")
        void shouldAccessCityCorrectly() {
            assertThat(dto.city()).isEqualTo("São Paulo");
        }

        @Test
        @DisplayName("Deve acessar state corretamente")
        void shouldAccessStateCorrectly() {
            assertThat(dto.state()).isEqualTo("SP");
        }

        @Test
        @DisplayName("Deve acessar available corretamente")
        void shouldAccessAvailableCorrectly() {
            assertThat(dto.available()).isTrue();
        }

        @Test
        @DisplayName("Deve acessar vehicleTypes corretamente")
        void shouldAccessVehicleTypesCorrectly() {
            assertThat(dto.vehicleTypes()).isEqualTo(vehicles);
        }
    }

    @Nested
    @DisplayName("Testes de igualdade")
    class EqualityTests {

        @Test
        @DisplayName("Deve ser igual quando todos os campos são iguais")
        void shouldBeEqualWhenAllFieldsAreEqual() {
            // Arrange
            UUID id = UUID.randomUUID();
            List<VehicleType> vehicles = List.of(VehicleType.CAR);

            DriverResponseDTO dto1 = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(vehicles)
                    .build();

            DriverResponseDTO dto2 = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(vehicles)
                    .build();

            // Assert
            assertThat(dto1).isEqualTo(dto2);
        }

        @Test
        @DisplayName("Deve ser diferente quando ID é diferente")
        void shouldBeDifferentWhenIdIsDifferent() {
            // Arrange
            DriverResponseDTO dto1 = DriverResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .name("João Silva")
                    .build();

            DriverResponseDTO dto2 = DriverResponseDTO.builder()
                    .id(UUID.randomUUID())
                    .name("João Silva")
                    .build();

            // Assert
            assertThat(dto1).isNotEqualTo(dto2);
        }

        @Test
        @DisplayName("Deve ter mesmo hashCode quando são iguais")
        void shouldHaveSameHashCodeWhenEqual() {
            // Arrange
            UUID id = UUID.randomUUID();
            List<VehicleType> vehicles = List.of(VehicleType.CAR);

            DriverResponseDTO dto1 = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(vehicles)
                    .build();

            DriverResponseDTO dto2 = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(vehicles)
                    .build();

            // Assert
            assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        }
    }

    @Nested
    @DisplayName("Testes de toString")
    class ToStringTests {

        @Test
        @DisplayName("Deve incluir todos os campos no toString")
        void shouldIncludeAllFieldsInToString() {
            // Arrange
            UUID id = UUID.randomUUID();
            DriverResponseDTO dto = DriverResponseDTO.builder()
                    .id(id)
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .available(true)
                    .vehicleTypes(List.of(VehicleType.CAR))
                    .build();

            // Act
            String toString = dto.toString();

            // Assert
            assertThat(toString).contains("João Silva");
            assertThat(toString).contains("joao.silva@email.com");
            assertThat(toString).contains("11999999999");
            assertThat(toString).contains("12345678901");
            assertThat(toString).contains("12345678900");
            assertThat(toString).contains("São Paulo");
            assertThat(toString).contains("SP");
            assertThat(toString).contains("CAR");
        }
    }
}

