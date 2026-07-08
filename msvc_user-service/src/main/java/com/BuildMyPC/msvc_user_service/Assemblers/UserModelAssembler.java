package com.BuildMyPC.msvc_user_service.Assemblers;

import com.BuildMyPC.msvc_user_service.Controllers.UserControllerV2;
import com.BuildMyPC.msvc_user_service.Models.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserControllerV2.class).buscarPorId(user.getId())).withSelfRel(),
                linkTo(methodOn(UserControllerV2.class).desactivar(user.getId())).withRel("desactivar"),
                linkTo(methodOn(UserControllerV2.class).listar()).withRel("users")
        );
    }
}