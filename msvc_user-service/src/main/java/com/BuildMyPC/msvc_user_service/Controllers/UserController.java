package com.BuildMyPC.msvc_user_service.Controllers;

import com.BuildMyPC.msvc_user_service.Models.Dtos.UserDTO;
import com.BuildMyPC.msvc_user_service.Models.User;
import com.BuildMyPC.msvc_user_service.Services.UserService;
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
@RequestMapping("/api/v1/users")
@Tag(name = "User Controller V1", description = "Operaciones CRUD puras para el dominio de Usuarios")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un usuario en el sistema validando sus campos obligatorios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Esquema de datos inválido")
    })
    public ResponseEntity<User> crear(@Valid @RequestBody UserDTO dto) {
        return new ResponseEntity<>(service.crearUser(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista plana de todos los usuarios registrados.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<User>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID", description = "Retorna los detalles del usuario según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar un usuario", description = "Cambia el estado de un usuario a INACTIVO de forma lógica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivarUser(id));
    }
}