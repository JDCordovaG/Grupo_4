package com.BuildMyPC.msvc_ram_service.Controllers;

import com.BuildMyPC.msvc_ram_service.Assemblers.RamModelAssembler;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import com.BuildMyPC.msvc_ram_service.Services.RamService;
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
@RequestMapping("/api/v2/rams")
@Tag(name = "RAM Controller V2", description = "Operaciones avanzadas con soporte hipermedia HATEOAS")
public class RamControllerV2 {

    private final RamService service;
    private final RamModelAssembler assembler;

    public RamControllerV2(RamService service, RamModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Listar todas las RAMs con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Ram>>> listar() {
        List<EntityModel<Ram>> rams = service.listarTodas().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(rams,
                linkTo(methodOn(RamControllerV2.class).listar()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una RAM por ID con enlaces de navegación autorreferenciados")
    public ResponseEntity<EntityModel<Ram>> buscarPorId(@PathVariable Long id) {
        Ram ram = service.buscarPorId(id);
        return ResponseEntity.ok(assembler.toModel(ram));
    }
}