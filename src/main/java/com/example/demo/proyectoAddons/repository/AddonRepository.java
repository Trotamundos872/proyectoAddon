package com.example.demo.proyectoAddons.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.proyectoAddons.model.Addon;

public interface AddonRepository extends JpaRepository<Addon, Long> {

    // INSERT INTO PERSONALIZADO
    @Modifying
    @Query(value = "INSERT INTO creador_addon (creador_id, addon_id, status) VALUES (:idCreador, :idAddon, :status)", nativeQuery = true)
    int insertarCreadorAddon(
            @Param("idCreador") Long idCreador,
            @Param("idAddon") Long idAddon,
            @Param("status") String status);

    @Modifying
    @Query(value = "UPDATE creador_addon SET status = :status WHERE creador_id = :idCreador AND addon_id = :idAddon", nativeQuery = true)
    int actualizarStatusCreadorAddon(
            @Param("idCreador") Long idCreador,
            @Param("idAddon") Long idAddon,
            @Param("status") String status);

    @Modifying
    @Query(value = "DELETE FROM creador_addon WHERE creador_id = :idCreador AND addon_id = :idAddon", nativeQuery = true)
    int deleteDatosDeCreadorAddon(
            @Param("idCreador") Long idCreador,
            @Param("idAddon") Long idAddon);

    @Query(value = "SELECT creador_id FROM creador_addon WHERE addon_id = :idAddon AND status NOT LIKE 'pendiente' AND status NOT LIKE 'rechazado'", nativeQuery = true)
    List<Long> getRelacionesPorAddon(@Param("idAddon") Long idAddon);

    @Query(value = "SELECT STATUS FROM creador_addon WHERE addon_id = :idAddon AND creador_id = :idCreador", nativeQuery = true)
    List<String> getAddonsCreado(@Param("idAddon") Long idAddon, @Param("idCreador") Long idCreador);

    @Query(value = "SELECT addon_id FROM creador_addon WHERE creador_id = :idCreador AND status NOT LIKE 'pendiente'  AND status NOT LIKE 'rechazado'", nativeQuery = true)
    List<Long> getAddonsDeCreador(@Param("idCreador") Long idCreador);

@Query(value = "SELECT addon_id FROM creador_addon WHERE creador_id = :idCreador AND status LIKE 'pendiente'", nativeQuery = true)
    List<Long> getAddonsDeCreadorPendiente(@Param("idCreador") Long idCreador);
}

// sudo -u postgres psql
// \c mydb
// SELECT * FROM TABLE;