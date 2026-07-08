package com.BuildMyPC.msvc_ram_service.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rams")
public class Ram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ram_id")
    private Long id;

    @Column(nullable = false, name = "componente_id_ram")
    private Long componenteId;

    @Column(nullable = false, name = "tipo_Ddr_cpu_ram")
    private String tipoDdr;

    @Column(nullable = false, name = "capacidad_Gb_ram")
    private Integer capacidadGb;

    @Column(nullable = false, name = "frecuencia_Mhz_ram")
    private Integer frecuenciaMhz;

    @Column(nullable = false, name = "latencia_Cl_ram")
    private Double latenciaCl;

    @Column(nullable = false, name = "modulos_ram")
    private Double modulos;

    @Column(nullable = false, name = "voltaje_ram")
    private Integer voltaje;

    @Embedded
    private Audit audit = new Audit();
}