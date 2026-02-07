package com.fretemais.drivermanager.application.dtos;

import com.fretemais.drivermanager.domain.enums.VehicleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Builder
public record DriverRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        String phone,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank(message = "A CNH é obrigatória")
        String cnh,

        @NotBlank(message = "A cidade é obrigatória")
        String city,

        @NotBlank(message = "A UF é obrigatória")
        @Size(min = 2, max = 2, message = "A UF deve ter 2 letras (ex: SP)")
        String state,

        @NotEmpty(message = "Selecione pelo menos um tipo de veículo")
        List<VehicleType> vehicleTypes
) {}
