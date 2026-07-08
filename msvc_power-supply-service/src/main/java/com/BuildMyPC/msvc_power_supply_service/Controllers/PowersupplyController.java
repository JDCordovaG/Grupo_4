package com.BuildMyPC.msvc_power_supply_service.Controllers;

import com.BuildMyPC.msvc_power_supply_service.Models.Dtos.PowersupplyDTO;
import com.BuildMyPC.msvc_power_supply_service.Services.PowersupplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fuentes-poder")
@Tag(name = "Catálogo Fuentes de Poder - V1", description = "Endpoints CRUD para unidades de suministro de energía (PSU)")
public class PowersupplyController {

    private final PowersupplyService service;

    public PowersupplyController(PowersupplyService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar fuente de poder", description = "Agrega una nueva PSU validando su potencia y certificación")
    @ApiResponse(responseCode = "201", description = "Fuente de poder creada")
    public ResponseEntity<PowersupplyDTO> crear(@Valid @RequestBody PowersupplyDTO dto) {
        return new ResponseEntity<>(service.crearFuentePoder(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PowersupplyDTO>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PowersupplyDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/certificacion/{certificacion}")
    public ResponseEntity<List<PowersupplyDTO>> buscarPorCertificacion(@PathVariable String certificacion) {
        return ResponseEntity.ok(service.buscarPorCertificacion(certificacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PowersupplyDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PowersupplyDTO dto) {
        return ResponseEntity.ok(service.actualizarFuentePoder(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Baja lógica", description = "Deshabilita una fuente sin afectar el histórico de computadores armados")
    @ApiResponse(responseCode = "204", description = "Fuente inactiva")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        service.desactivarFuentePoder(id);
        return ResponseEntity.noContent().build();
    }
}