package com.example.demo.proyectoAddons.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 60)
    @NotBlank
    @Length(min = 2, max = 60)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Length(min = 6, max = 100)
    private String password;

    @Column(name = "es_pago", nullable = false)
    private Boolean esDePago = false;

    @Column(name = "deprecado", nullable = true)
    private Boolean deprecado = false;

    @Column(name = "codigo_recuperacion", nullable = true)
    private String codigoRecuperacion;

    @Column(name = "expiracion_codigo", nullable = true)
    private java.time.LocalDateTime expiracionCodigo;

    // Un usuario puede tener muchas subscripciones
    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference(value = "usuario-subs")
    private List<Subscripcion> subscripciones;

    // Un usuario puede ser creador     
    @OneToOne(mappedBy = "usuario")
    @JsonBackReference(value = "creador-usuario")
    private Creador creador;

    // Un usuario puede ser administrador
    @OneToOne(mappedBy = "usuario")
    @JsonBackReference(value = "admin-usuario")
    private Administrador administrador;
}
