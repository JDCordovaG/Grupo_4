package com.BuildMyPC.msvc_user_service.Models.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {

    @NotBlank(message = "El nombre del user es obligatorio")
    private String nombre;

    @NotBlank(message = "El campo apellido no puede estar vacio")
    private String apellido;

    @NotBlank(message = "El campo email no puede estar vacio")
    @Email(message = "El formato del email no es válido")
    private String email;

    @NotBlank(message = "El campo telefono no puede estar vacio")
    private String telefono;

    @NotBlank(message = "El campo rol Funcional es obligatorio")
    private String rolFuncional;

    @NotBlank(message = "El campo estado es obligatorio")
    private String estado;

    @NotNull(message = "El campo fecha de Registro no puede estar vacio")
    private LocalDate fechaRegistro;
}