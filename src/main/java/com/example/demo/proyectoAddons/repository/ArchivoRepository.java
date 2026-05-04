package com.example.demo.proyectoAddons.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.proyectoAddons.model.Archivo;
import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Long> {
    List<Archivo> findByAddonId(Long addonId);
}
