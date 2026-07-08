package com.BuildMyPC.msvc_ram_service.Controllers;

import com.BuildMyPC.msvc_ram_service.Models.Dtos.RamDTO;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import com.BuildMyPC.msvc_ram_service.Services.RamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rams")
@Tag(name = "RAM Controller V1", description = "Operaciones CRUD puras para el componente RAM")
public class RamController {

    private final RamService service;

    public RamController(RamService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva memoria RAM", description = "Permite registrar un módulo RAM validando sus propiedades.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "RAM creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<Ram> crear(@Valid @RequestBody RamDTO dto) {
        return new ResponseEntity<>(service.crearRam(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas las RAMs", description = "Retorna una lista plana de todas las memorias en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<Ram>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar RAM por ID", description = "Retorna una memoria RAM específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RAM encontrada"),
            @ApiResponse(responseCode = "404", description = "RAM no encontrada")
    })
    public ResponseEntity<Ram> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}