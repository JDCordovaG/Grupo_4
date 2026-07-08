package com.BuildMyPC.msvc_quotation_service.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quotation_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetalleQuotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long componenteId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    @JsonIgnore
    private Quotation quotation;
}