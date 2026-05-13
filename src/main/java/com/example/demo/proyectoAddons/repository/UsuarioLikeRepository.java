package com.example.demo.proyectoAddons.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.proyectoAddons.model.UsuarioLike;

public interface UsuarioLikeRepository extends JpaRepository<UsuarioLike, String> {

    //SI DA 1, es like, si da 0, es no like
    @Query(value = "SELECT COUNT(*) FROM usuario_like WHERE addon_id = :idAddon AND usuario_id = :idUsuario", nativeQuery = true)
    int getSiDarLike( @Param("idUsuario") Long idUsuario,@Param("idAddon") Long idAddon);

    @org.springframework.data.jpa.repository.Modifying
    @Query(value = "DELETE FROM usuario_like WHERE addon_id = :idAddon", nativeQuery = true)
    void deleteByAddonId(@Param("idAddon") Long idAddon);

}

//sudo -u postgres  psql
// \c mydb 
// SELECT * FROM TABLE;