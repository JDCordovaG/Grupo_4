package com.BuildMyPC.msvc_user_service.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, name = "nombre_user")
    private String nombre;

    @Column(nullable = false, name = "apellido_user")
    private String apellido;

    @Column(nullable = false, name = "email_user", unique = true)
    @Email
    private String email;

    @Column(nullable = false, name = "telefono_user")
    private String telefono;

    @Column(nullable = false, name = "rol_funcional_user")
    private String rolFuncional;

    @Column(nullable = false, name = "estado_user")
    private String estado;

    @Column(nullable = false, name = "fecha_registro_user")
    private LocalDate fechaRegistro;

    @Embedded
    private Audit audit = new Audit();
}