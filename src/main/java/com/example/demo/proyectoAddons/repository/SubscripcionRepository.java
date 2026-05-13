package com.example.demo.proyectoAddons.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.demo.proyectoAddons.model.Subscripcion;

public interface SubscripcionRepository extends JpaRepository<Subscripcion, Long> {

    @Query("SELECT COUNT(s) FROM Subscripcion s WHERE s.creador.id = :idCreador")
    long countByCreadorId(@Param("idCreador") Long idCreador);

}

//sudo -u postgres  psql
// \c mydb 
// SELECT * FROM TABLE;