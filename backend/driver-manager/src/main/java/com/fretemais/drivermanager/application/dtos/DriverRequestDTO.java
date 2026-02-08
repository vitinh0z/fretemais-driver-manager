package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Builder
@Schema(description = "Dados para criação ou atualização de um motorista")
public record DriverRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Schema(description = "Nome completo do motorista", example = "João da Silva")
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        @Schema(description = "Endereço de e-mail do motorista", example = "joao.silva@email.com")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        @Schema(description = "Número de telefone para contato", example = "(11) 98765-4321")
        String phone,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        @Schema(description = "CPF do motorista (apenas números ou formatado)", example = "123.456.789-00")
        String cpf,

        @NotBlank(message = "A CNH é obrigatória")
        @Schema(description = "Número da Carteira Nacional de Habilitação", example = "12345678901")
        String cnh,

        @NotBlank(message = "A cidade é obrigatória")
        @Schema(description = "Cidade de residência do motorista", example = "São Paulo")
        String city,

        @NotBlank(message = "A UF é obrigatória")
        @Size(min = 2, max = 2, message = "A UF deve ter 2 letras (ex: SP)")
        @Schema(description = "Sigla do estado (UF)", example = "SP")
        String state,

        @NotEmpty(message = "Selecione pelo menos um tipo de veículo")
        @Schema(description = "Lista de tipos de veículos que o motorista opera")
        List<VehicleType> vehicleTypes
) {}
