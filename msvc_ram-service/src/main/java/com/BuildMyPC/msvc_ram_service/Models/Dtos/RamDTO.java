package com.BuildMyPC.msvc_ram_service.Models.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RamDTO {

    @NotNull(message = "El ID del componente es obligatorio")
    private Long componenteId;

    @NotBlank(message = "El campo tipoDdr no puede estar vacio")
    private String tipoDdr;

    @NotNull(message = "El campo capacidadGb no puede estar vacio")
    @Positive(message = "La capacidadGb debe ser positiva")
    private Integer capacidadGb;

    @NotNull(message = "El campo frecuenciaMhz no puede estar vacio")
    @Positive(message = "La frecuenciaMhz debe ser positiva")
    private Integer frecuenciaMhz;

    @NotNull(message = "El campo latenciaCl no puede estar vacio")
    private Double latenciaCl;

    @NotNull(message = "El campo modulos no puede estar vacio")
    private Double modulos;

    @NotNull(message = "El campo voltaje no puede estar vacio")
    private Integer voltaje;
}