package com.example.demo.proyectoAddons.repository;

import com.example.demo.proyectoAddons.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipo(String tipo);

    List<Reporte> findByUsuarioId(Long usuarioId);

    List<Reporte> findByTipoAndReferenciaId(String tipo, Long referenciaId);
}
