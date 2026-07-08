package com.BuildMyPC.msvc_quotation_service.Controllers;

import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import com.BuildMyPC.msvc_quotation_service.Models.Dtos.QuotationDTO;
import com.BuildMyPC.msvc_quotation_service.Services.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quotations")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones V1", description = "Endpoints CRUD estándar para el cierre comercial de builds")
public class QuotationController {

    private final QuotationService service;

    @PostMapping
    @Operation(summary = "Emitir una nueva Cotización", description = "Verifica la compatibilidad técnica remota de la build y calcula montos y vencimientos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cotización emitida con éxito"),
            @ApiResponse(responseCode = "400", description = "Error financiero o la configuración técnica es incompatible")
    })
    public ResponseEntity<Quotation> crearCotizacion(@Valid @RequestBody QuotationDTO dto) {
        return new ResponseEntity<>(service.generarCotizacion(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cotización por ID")
    public ResponseEntity<Quotation> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar cotizaciones históricas de un cliente")
    public ResponseEntity<List<Quotation>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado comercial de la cotización")
    public ResponseEntity<Quotation> cambiarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
    }

    @PutMapping("/{id}/verificar-vencimiento")
    @Operation(summary = "Anular cotización en caso de obsolescencia temporal")
    public ResponseEntity<Quotation> verificarVencimiento(@PathVariable Long id) {
        return ResponseEntity.ok(service.anularCotizacionVencida(id));
    }
}