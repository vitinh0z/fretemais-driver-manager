package com.fretemais.drivermanager.infrastructure.controllers;

import com.fretemais.drivermanager.application.dtos.DriverSummaryDTO;
import com.fretemais.drivermanager.application.dtos.DriverRequestDTO;
import com.fretemais.drivermanager.application.dtos.DriverResponseDTO;
import com.fretemais.drivermanager.application.services.DriverService;
import com.fretemais.drivermanager.domain.enums.VehicleType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Motoristas", description = "Endpoints para gerenciamento de motoristas")
public class DriverController {

    private final DriverService service;

    @PostMapping
    @Operation(summary = "Criar um novo motorista", description = "Cadastra um novo motorista no sistema com os dados fornecidos.")
    @ApiResponse(responseCode = "201", description = "Motorista criado com sucesso",
            content = @Content(schema = @Schema(implementation = DriverResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    @ApiResponse(responseCode = "409", description = "Conflito: Email ou CPF já cadastrado")
    public ResponseEntity<DriverResponseDTO> create(@RequestBody @Valid DriverRequestDTO dto) {
        DriverResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar motoristas com filtros", description = "Retorna uma lista paginada de motoristas. Permite filtrar por texto (nome/email/cpf/cnh), estado, cidade e tipos de veículo.")
    @ApiResponse(responseCode = "200", description = "Lista de motoristas retornada com sucesso")
    public ResponseEntity<Page<DriverSummaryDTO>> list(
            @Parameter(description = "Texto para busca (nome, e-mail, CPF ou CNH)")
            @RequestParam(required = false) String text,
            @Parameter(description = "Sigla do estado para filtro")
            @RequestParam(required = false) String state,
            @Parameter(description = "Nome da cidade para filtro")
            @RequestParam(required = false) String city,
            @Parameter(description = "Lista de tipos de veículos para filtro")
            @RequestParam(required = false) List<VehicleType> vehicles,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(service.list(text, state, city, vehicles, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter motorista por ID", description = "Retorna os detalhes completos de um motorista específico através do seu identificador único.")
    @ApiResponse(responseCode = "200", description = "Motorista encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    public ResponseEntity<DriverResponseDTO> getById(
            @Parameter(description = "ID único do motorista") @PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar motorista", description = "Atualiza os dados de um motorista existente.")
    @ApiResponse(responseCode = "200", description = "Motorista atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    public ResponseEntity<DriverResponseDTO> update(
            @Parameter(description = "ID único do motorista") @PathVariable UUID id,
            @RequestBody @Valid DriverRequestDTO dto) {
        return ResponseEntity.ok(service.updateById(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir motorista", description = "Remove permanentemente um motorista do sistema.")
    @ApiResponse(responseCode = "204", description = "Motorista excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único do motorista") @PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}