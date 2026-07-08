package com.BuildMyPC.msvc_quotation_service.Models.Dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuotationDTO {

    @NotNull(message = "El ID de la build es obligatorio")
    private Long buildId;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    private Double descuento = 0.0;

    @NotEmpty(message = "La cotización debe incluir al menos un componente seleccionado")
    @Valid
    private List<ComponenteCotizadoDTO> componentesSeleccionados;

    /**
     * Método auxiliar utilizado por el QuotationServiceImpl
     * para extraer rápidamente la lista de precios y calcular el subtotal.
     */
    public List<Double> getPreciosComponentes() {
        if (componentesSeleccionados == null) return List.of();
        return componentesSeleccionados.stream()
                .map(ComponenteCotizadoDTO::getPrecio)
                .collect(Collectors.toList());
    }

    /**
     * DTO anidado para estructurar los elementos individuales del carrito/build.
     */
    @Data
    public static class ComponenteCotizadoDTO {

        @NotNull(message = "El ID del componente es obligatorio")
        private Long componenteId;

        @NotBlank(message = "El nombre del componente no puede estar vacío")
        private String nombre;

        @NotNull(message = "El precio del componente es obligatorio")
        @Min(value = 1, message = "El precio del componente debe ser mayor a 0")
        private Double precio;
    }
}