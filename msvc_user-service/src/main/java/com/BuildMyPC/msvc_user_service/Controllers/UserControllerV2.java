package com.BuildMyPC.msvc_user_service.Controllers;

import com.BuildMyPC.msvc_user_service.Assemblers.UserModelAssembler;
import com.BuildMyPC.msvc_user_service.Models.User;
import com.BuildMyPC.msvc_user_service.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Controller V2", description = "Endpoints avanzados de Usuarios con soporte Hipermedia")
public class UserControllerV2 {

    private final UserService service;
    private final UserModelAssembler assembler;

    public UserControllerV2(UserService service, UserModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<User>>> listar() {
        List<EntityModel<User>> users = service.listarTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(users,
                linkTo(methodOn(UserControllerV2.class).listar()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID incluyendo enlaces autodescriptivos de acciones disponibles")
    public ResponseEntity<EntityModel<User>> buscarPorId(@PathVariable Long id) {
        User user = service.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar usuario de forma lógica con enlaces de navegación actualizados")
    public ResponseEntity<EntityModel<User>> desactivar(@PathVariable Long id) {
        User user = service.desactivarUser(id);
        return ResponseEntity.ok(assembler.toModel(user));
    }
}