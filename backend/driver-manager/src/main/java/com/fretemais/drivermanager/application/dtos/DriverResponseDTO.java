package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
@Schema(description = "Dados detalhados do motorista retornados pela API")
public record DriverResponseDTO(
        @Schema(description = "Identificador único do motorista", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        @Schema(description = "Nome completo do motorista", example = "João da Silva")
        String name,
        @Schema(description = "Endereço de e-mail do motorista", example = "joao.silva@email.com")
        String email,
        @Schema(description = "Número de telefone", example = "(11) 98765-4321")
        String phone,
        @Schema(description = "CPF do motorista", example = "123.456.789-00")
        String cpf,
        @Schema(description = "Número da CNH", example = "12345678901")
        String cnh,
        @Schema(description = "Cidade de residência", example = "São Paulo")
        String city,
        @Schema(description = "Sigla do estado", example = "SP")
        String state,
        @Schema(description = "Indica se o motorista está disponível no momento", example = "true")
        boolean available,
        @Schema(description = "Lista de tipos de veículos que o motorista opera")
        List<VehicleType> vehicleTypes
) {}
