package com.fretemais.drivermanager.application.services;

import com.fretemais.drivermanager.application.dtos.DriverRequestDTO;
import com.fretemais.drivermanager.application.dtos.DriverResponseDTO;
import com.fretemais.drivermanager.application.mappers.DriverMapper;
import com.fretemais.drivermanager.domain.enums.VehicleType;
import com.fretemais.drivermanager.domain.model.Driver;
import com.fretemais.drivermanager.infrastructure.persistence.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DriverService - Testes Unitários")
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @InjectMocks
    private DriverService driverService;

    private DriverRequestDTO validRequestDTO;
    private Driver driver;
    private DriverResponseDTO responseDTO;
    private UUID driverId;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();

        validRequestDTO = DriverRequestDTO.builder()
                .name("João Silva")
                .email("joao.silva@email.com")
                .phone("11999999999")
                .cpf("12345678901")
                .cnh("12345678900")
                .city("São Paulo")
                .state("SP")
                .vehicleTypes(List.of(VehicleType.CAR, VehicleType.MOTORCYCLE))
                .build();

        driver = Driver.builder()
                .id(driverId)
                .name("João Silva")
                .email("joao.silva@email.com")
                .phoneNumber("11999999999")
                .cpf("12345678901")
                .cnh("12345678900")
                .city("São Paulo")
                .state("SP")
                .available(true)
                .vehicleType(List.of(VehicleType.CAR, VehicleType.MOTORCYCLE))
                .build();

        responseDTO = DriverResponseDTO.builder()
                .id(driverId)
                .name("João Silva")
                .email("joao.silva@email.com")
                .phone("11999999999")
                .cpf("12345678901")
                .cnh("12345678900")
                .city("São Paulo")
                .state("SP")
                .available(true)
                .vehicleTypes(List.of(VehicleType.CAR, VehicleType.MOTORCYCLE))
                .build();
    }

    @Nested
    @DisplayName("Testes do método create()")
    class CreateTests {

        @Test
        @DisplayName("Deve criar motorista com sucesso quando dados são válidos")
        void shouldCreateDriverSuccessfully() {
            // Arrange
            when(driverRepository.existsByEmail(validRequestDTO.email())).thenReturn(false);
            when(driverRepository.existsByCpf(validRequestDTO.cpf())).thenReturn(false);
            when(driverRepository.existsByCnh(validRequestDTO.cnh())).thenReturn(false);
            when(driverMapper.toEntity(validRequestDTO)).thenReturn(driver);
            when(driverRepository.save(driver)).thenReturn(driver);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            DriverResponseDTO result = driverService.create(validRequestDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(driverId);
            assertThat(result.name()).isEqualTo("João Silva");
            assertThat(result.email()).isEqualTo("joao.silva@email.com");
            assertThat(result.available()).isTrue();

            verify(driverRepository).existsByEmail(validRequestDTO.email());
            verify(driverRepository).existsByCpf(validRequestDTO.cpf());
            verify(driverRepository).existsByCnh(validRequestDTO.cnh());
            verify(driverRepository).save(driver);
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já existe")
        void shouldThrowExceptionWhenEmailExists() {
            // Arrange
            when(driverRepository.existsByEmail(validRequestDTO.email())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.create(validRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email já cadastrado");

            verify(driverRepository).existsByEmail(validRequestDTO.email());
            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando CPF já existe")
        void shouldThrowExceptionWhenCpfExists() {
            // Arrange
            when(driverRepository.existsByEmail(validRequestDTO.email())).thenReturn(false);
            when(driverRepository.existsByCpf(validRequestDTO.cpf())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.create(validRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("CPF já cadastrado");

            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando CNH já existe")
        void shouldThrowExceptionWhenCnhExists() {
            // Arrange
            when(driverRepository.existsByEmail(validRequestDTO.email())).thenReturn(false);
            when(driverRepository.existsByCpf(validRequestDTO.cpf())).thenReturn(false);
            when(driverRepository.existsByCnh(validRequestDTO.cnh())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.create(validRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("CNH já cadastrada");

            verify(driverRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método list()")
    class ListTests {

        @Test
        @DisplayName("Deve listar motoristas com paginação")
        void shouldListDriversWithPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> driverPage = new PageImpl<>(List.of(driver), pageable, 1);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(driverPage);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            Page<DriverResponseDTO> result = driverService.list(null, null, null, null, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().name()).isEqualTo("João Silva");

            verify(driverRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve listar motoristas com filtro de texto")
        void shouldListDriversWithTextFilter() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> driverPage = new PageImpl<>(List.of(driver), pageable, 1);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(driverPage);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            Page<DriverResponseDTO> result = driverService.list("João", null, null, null, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(driverRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve listar motoristas com filtro de estado")
        void shouldListDriversWithStateFilter() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> driverPage = new PageImpl<>(List.of(driver), pageable, 1);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(driverPage);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            Page<DriverResponseDTO> result = driverService.list(null, "SP", null, null, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(driverRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve listar motoristas com filtro de cidade")
        void shouldListDriversWithCityFilter() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> driverPage = new PageImpl<>(List.of(driver), pageable, 1);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(driverPage);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            Page<DriverResponseDTO> result = driverService.list(null, null, "São Paulo", null, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(driverRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve listar motoristas com filtro de veículos")
        void shouldListDriversWithVehicleFilter() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> driverPage = new PageImpl<>(List.of(driver), pageable, 1);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(driverPage);
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            Page<DriverResponseDTO> result = driverService.list(null, null, null, List.of(VehicleType.CAR), pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(driverRepository).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há motoristas")
        void shouldReturnEmptyPageWhenNoDrivers() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Driver> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(driverRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            // Act
            Page<DriverResponseDTO> result = driverService.list(null, null, null, null, pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("Testes do método getById()")
    class GetByIdTests {

        @Test
        @DisplayName("Deve retornar motorista quando ID existe")
        void shouldReturnDriverWhenIdExists() {
            // Arrange
            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverMapper.toResponse(driver)).thenReturn(responseDTO);

            // Act
            DriverResponseDTO result = driverService.getById(driverId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(driverId);
            assertThat(result.name()).isEqualTo("João Silva");

            verify(driverRepository).findById(driverId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe")
        void shouldThrowExceptionWhenIdNotExists() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(driverRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> driverService.getById(nonExistentId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Motorista não encontrado");

            verify(driverRepository).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("Testes do método deleteById()")
    class DeleteByIdTests {

        @Test
        @DisplayName("Deve deletar motorista quando ID existe")
        void shouldDeleteDriverWhenIdExists() {
            // Arrange
            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            doNothing().when(driverRepository).delete(driver);

            // Act
            driverService.deleteById(driverId);

            // Assert
            verify(driverRepository).findById(driverId);
            verify(driverRepository).delete(driver);
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe ao deletar")
        void shouldThrowExceptionWhenIdNotExistsOnDelete() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(driverRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> driverService.deleteById(nonExistentId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Motorista não encontrado");

            verify(driverRepository).findById(nonExistentId);
            verify(driverRepository, never()).deleteById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("Testes do método updateById()")
    class UpdateByIdTests {

        private DriverRequestDTO updateRequestDTO;

        @BeforeEach
        void setUpUpdate() {
            updateRequestDTO = DriverRequestDTO.builder()
                    .name("João Silva Atualizado")
                    .email("joao.atualizado@email.com")
                    .phone("11988888888")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .vehicleTypes(List.of(VehicleType.TRUCK))
                    .build();
        }

        @Test
        @DisplayName("Deve atualizar motorista com sucesso")
        void shouldUpdateDriverSuccessfully() {
            // Arrange
            Driver updatedDriver = Driver.builder()
                    .id(driverId)
                    .name("João Silva Atualizado")
                    .email("joao.atualizado@email.com")
                    .phoneNumber("11988888888")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .available(true)
                    .vehicleType(List.of(VehicleType.TRUCK))
                    .build();

            DriverResponseDTO updatedResponseDTO = DriverResponseDTO.builder()
                    .id(driverId)
                    .name("João Silva Atualizado")
                    .email("joao.atualizado@email.com")
                    .phone("11988888888")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .available(true)
                    .vehicleTypes(List.of(VehicleType.TRUCK))
                    .build();

            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.existsByEmail(updateRequestDTO.email())).thenReturn(false);
            when(driverRepository.save(any(Driver.class))).thenReturn(updatedDriver);
            when(driverMapper.toResponse(updatedDriver)).thenReturn(updatedResponseDTO);

            // Act
            DriverResponseDTO result = driverService.updateById(driverId, updateRequestDTO);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("João Silva Atualizado");
            assertThat(result.email()).isEqualTo("joao.atualizado@email.com");
            assertThat(result.city()).isEqualTo("Rio de Janeiro");
            assertThat(result.state()).isEqualTo("RJ");

            verify(driverRepository).findById(driverId);
            verify(driverRepository).save(any(Driver.class));
        }

        @Test
        @DisplayName("Deve atualizar motorista mantendo mesmo email")
        void shouldUpdateDriverKeepingSameEmail() {
            // Arrange
            DriverRequestDTO sameEmailRequest = DriverRequestDTO.builder()
                    .name("João Silva Atualizado")
                    .email("joao.silva@email.com") // mesmo email
                    .phone("11988888888")
                    .cpf("12345678901")
                    .cnh("12345678900")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .vehicleTypes(List.of(VehicleType.TRUCK))
                    .build();

            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.save(any(Driver.class))).thenReturn(driver);
            when(driverMapper.toResponse(any())).thenReturn(responseDTO);

            // Act
            DriverResponseDTO result = driverService.updateById(driverId, sameEmailRequest);

            // Assert
            assertThat(result).isNotNull();
            verify(driverRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Deve lançar exceção quando novo email já existe em outro motorista")
        void shouldThrowExceptionWhenNewEmailExists() {
            // Arrange
            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.existsByEmail(updateRequestDTO.email())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.updateById(driverId, updateRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email já cadastrado");

            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando novo CPF já existe em outro motorista")
        void shouldThrowExceptionWhenNewCpfExists() {
            // Arrange
            DriverRequestDTO newCpfRequest = DriverRequestDTO.builder()
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("98765432100") // CPF diferente
                    .cnh("12345678900")
                    .city("São Paulo")
                    .state("SP")
                    .vehicleTypes(List.of(VehicleType.CAR))
                    .build();

            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.existsByCpf(newCpfRequest.cpf())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.updateById(driverId, newCpfRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("CPF já cadastrado");

            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando nova CNH já existe em outro motorista")
        void shouldThrowExceptionWhenNewCnhExists() {
            // Arrange
            DriverRequestDTO newCnhRequest = DriverRequestDTO.builder()
                    .name("João Silva")
                    .email("joao.silva@email.com")
                    .phone("11999999999")
                    .cpf("12345678901")
                    .cnh("99999999999") // CNH diferente
                    .city("São Paulo")
                    .state("SP")
                    .vehicleTypes(List.of(VehicleType.CAR))
                    .build();

            when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
            when(driverRepository.existsByCnh(newCnhRequest.cnh())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> driverService.updateById(driverId, newCnhRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("CNH já cadastrada");

            verify(driverRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID não existe ao atualizar")
        void shouldThrowExceptionWhenIdNotExistsOnUpdate() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(driverRepository.findById(nonExistentId)).thenReturn(Optional.empty());


            assertThatThrownBy(() -> driverService.updateById(nonExistentId, updateRequestDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Motorista não encontrado");

            verify(driverRepository).findById(nonExistentId);
            verify(driverRepository, never()).save(any());
        }
    }
}

