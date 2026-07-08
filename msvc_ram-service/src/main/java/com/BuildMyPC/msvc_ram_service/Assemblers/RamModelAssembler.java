package com.BuildMyPC.msvc_ram_service.Assemblers;

import com.BuildMyPC.msvc_ram_service.Controllers.RamControllerV2;
import com.BuildMyPC.msvc_ram_service.Models.Ram;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class RamModelAssembler implements RepresentationModelAssembler<Ram, EntityModel<Ram>> {

    @Override
    public EntityModel<Ram> toModel(Ram ram) {
        return EntityModel.of(ram,
                linkTo(methodOn(RamControllerV2.class).buscarPorId(ram.getId())).withSelfRel(),
                linkTo(methodOn(RamControllerV2.class).listar()).withRel("rams")
        );
    }
}