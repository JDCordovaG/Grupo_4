package com.BuildMyPC.msvc_user_service.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "directions")
public class Direction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "direction_id")
    private Long id;

    @Column(nullable = false, name = "user_id_direction")
    private Long usuarioId;

    @Column(nullable = false, name = "comuna_direction")
    private String comuna;

    @Column(nullable = false, name = "ciudad_direction")
    private String ciudad;

    @Column(nullable = false, name = "detalle_direction")
    private String detalle;

    @Embedded
    private Audit audit = new Audit();
}