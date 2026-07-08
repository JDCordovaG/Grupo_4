package com.BuildMyPC.msvc_quotation_service.Controllers;

import com.BuildMyPC.msvc_quotation_service.Assemblers.QuotationModelAssembler;
import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import com.BuildMyPC.msvc_quotation_service.Models.Dtos.QuotationDTO;
import com.BuildMyPC.msvc_quotation_service.Services.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/quotations")
@RequiredArgsConstructor
@Tag(name = "Cotizaciones V2", description = "Gestión comercial avanzada con enlaces hipermedia de navegación autónoma")
public class QuotationControllerV2 {

    private final QuotationService service;
    private final QuotationModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Emitir cotización con respuestas autodescriptivas")
    public ResponseEntity<EntityModel<Quotation>> crearCotizacion(@Valid @RequestBody QuotationDTO dto) {
        Quotation q = service.generarCotizacion(dto);
        return new ResponseEntity<>(assembler.toModel(q), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cotización enriquecida con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Quotation>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(service.buscarPorId(id)));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener colección navegable de cotizaciones")
    public ResponseEntity<CollectionModel<EntityModel<Quotation>>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<Quotation>> models = service.listarPorUsuario(usuarioId).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(QuotationControllerV2.class).listarPorUsuario(usuarioId)).withSelfRel()));
    }
}