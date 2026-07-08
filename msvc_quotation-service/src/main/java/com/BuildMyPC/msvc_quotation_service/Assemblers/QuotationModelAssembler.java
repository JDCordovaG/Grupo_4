package com.BuildMyPC.msvc_quotation_service.Assemblers;

import com.BuildMyPC.msvc_quotation_service.Controllers.QuotationControllerV2;
import com.BuildMyPC.msvc_quotation_service.Models.Quotation;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class QuotationModelAssembler implements RepresentationModelAssembler<Quotation, EntityModel<Quotation>> {

    @Override
    public EntityModel<Quotation> toModel(Quotation quotation) {
        return EntityModel.of(
                quotation,
                linkTo(methodOn(QuotationControllerV2.class).buscarPorId(quotation.getId())).withSelfRel(),
                linkTo(methodOn(QuotationControllerV2.class).listarPorUsuario(quotation.getUsuarioId())).withRel("cotizaciones-usuario")
        );
    }
}