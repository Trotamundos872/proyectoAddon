package com.example.demo.proyectoAddons.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "archivo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_mostrado", nullable = false)
    @NotBlank
    private String nombreMostrado;

    @Column(name = "url", nullable = false)
    @NotBlank
    private String url;

    @Column(name = "version_juego", nullable = false)
    @NotBlank
    private String versionJuego;

    @Column(name = "version_addon", nullable = false)
    @NotBlank
    private String versionAddon;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = false;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "registro_cambios", columnDefinition = "TEXT")
    private String registroCambios;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "numero_descargas")
    private Integer numeroDescargas = 0;

    @ManyToOne
    @JoinColumn(name = "id_addon")
    @JsonIgnore
    private Addon addon;
}
