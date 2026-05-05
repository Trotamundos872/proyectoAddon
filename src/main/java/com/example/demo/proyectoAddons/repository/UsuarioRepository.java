package com.example.demo.proyectoAddons.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.proyectoAddons.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

@Query(value = "SELECT * FROM usuario WHERE usuario.es_pago = true",nativeQuery = true)
List<Usuario> listaDePago();

boolean existsByEmail(String email);

java.util.Optional<Usuario> findByEmail(String email);




//private List<Usuario> findByEsDePago(Boolean esDePago);
}

//sudo -u postgres  psql
// \c mydb 
// SELECT * FROM TABLE;