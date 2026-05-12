package com.example.demo.proyectoAddons.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 60)
    @NotBlank
    @Length(min = 2, max = 60)
    private String nombre;

    //Posibles, mod, map, skin
    @Column(name = "tipo", nullable = false)
    @NotBlank
    private String tipo;

    @Column(name = "tag", nullable = false)
    @NotBlank
    private String tag;

    @Column(name = "url_miniatura", nullable = false)
    @NotBlank
    private String urlMiniatura;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    @Length(min = 5, max = 2000)
    private String descripcion;

    @Column(name = "texto_addon", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    @Length(min = 5)
    private String textoAddon;

    @Column(name = "likes")
    private Integer likes = 0;

    @Column(name = "deprecado", nullable = true)
    private Boolean deprecado = false;

    @OneToMany(mappedBy = "addon")
    private List<CreadorAddon> creadorAddons;

    @OneToMany(mappedBy = "addon", cascade = CascadeType.ALL)
    private List<Archivo> archivos;

    @Transient
    private List<String> nombresCreadores;

    @Transient
    private List<Long> idsCreadores;
}
