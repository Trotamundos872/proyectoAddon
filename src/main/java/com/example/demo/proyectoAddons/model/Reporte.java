package com.example.demo.proyectoAddons.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo", nullable = false)
    private String tipo; // "addon", "usuario", "archivo"

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId; // ID del addon, usuario o archivo reportado (no es FK)

    @Column(name = "razon", nullable = false, length = 500)
    private String razon;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}
