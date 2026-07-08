package com.BuildMyPC.msvc_motherboard_service.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "placas_madre")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Motherboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placamadre_id")
    private Long id;

    @Column(nullable = false, name = "componente_id_placamadre")
    private Long componenteId;

    @Column(nullable = false, name = "socket_placamadre")
    private String socket;

    @Column(nullable = false, name = "chipset_placamadre")
    private String chipset;

    @Column(nullable = false, name = "tipo_ram_soportada")
    private String tipoRamSoportada;

    @Column(nullable = false, name = "slots_ram")
    private Integer slotsRam;

    @Column(nullable = false, name = "max_ram_gb")
    private Integer maxRamGb;

    @Column(nullable = false, name = "formato_placamadre")
    private String formato;

    @Column(nullable = false)
    private Boolean activo;

    @Embedded
    private Audit audit = new Audit();
}