package com.BuildMyPC.msvc_motherboard_service.Controllers;

import com.BuildMyPC.msvc_motherboard_service.Models.Dtos.MotherboardDTO;
import com.BuildMyPC.msvc_motherboard_service.Services.MotherboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/placas-madre")
@Tag(name = "Catálogo Placas Madre - V1", description = "Endpoints CRUD para tarjetas madre")
public class MotherboardController {

    private final MotherboardService service;

    public MotherboardController(MotherboardService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar placa madre", description = "Agrega una nueva motherboard al catálogo")
    @ApiResponse(responseCode = "201", description = "Placa madre creada")
    public ResponseEntity<MotherboardDTO> crear(@Valid @RequestBody MotherboardDTO dto) {
        return new ResponseEntity<>(service.crearPlacamadre(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MotherboardDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotherboardDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/socket/{socket}")
    public ResponseEntity<List<MotherboardDTO>> buscarPorSocket(@PathVariable String socket) {
        return ResponseEntity.ok(service.buscarPorSocket(socket));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotherboardDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MotherboardDTO dto) {
        return ResponseEntity.ok(service.actualizarPlacamadre(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Baja lógica de Placa Madre", description = "Deshabilita una placa sin eliminar el histórico")
    @ApiResponse(responseCode = "204", description = "Placa madre inactiva")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        service.desactivarPlacamadre(id);
        return ResponseEntity.noContent().build();
    }
}