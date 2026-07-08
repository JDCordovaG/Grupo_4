package com.BuildMyPC.msvc_quotation_service.Models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long buildId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double descuento;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false)
    private LocalDate fechaVencimiento;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleQuotation> detalles = new ArrayList<>();

    @Embedded
    @Builder.Default
    private Audit audit = new Audit();

    public void agregarDetalle(DetalleQuotation detalle) {
        detalles.add(detalle);
        detalle.setQuotation(this);
    }
}