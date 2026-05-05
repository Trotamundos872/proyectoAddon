package com.example.demo.proyectoAddons.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.proyectoAddons.model.Creador;


import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface CreadorrRepository extends JpaRepository<Creador, Long> {
    @Query("SELECT DISTINCT c FROM Creador c LEFT JOIN FETCH c.creadorAddons")
    List<Creador> findAllWithAddons();
}

//sudo -u postgres  psql
// \c mydb 
// SELECT * FROM TABLE;