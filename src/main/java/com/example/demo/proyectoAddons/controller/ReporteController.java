package com.example.demo.proyectoAddons.controller;

import com.example.demo.proyectoAddons.model.Reporte;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.ReporteService;
import com.example.demo.proyectoAddons.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reporte")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AdministradorService administradorService;

    /**
     * Crear un reporte. Cualquier usuario logueado puede reportar.
     * Body: { "tipo": "addon"|"usuario"|"archivo", "referenciaId": 1, "razon": "..." }
     */
    @PostMapping
    public ResponseEntity<?> crearReporte(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> body) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        String tipo = (String) body.get("tipo");
        Object referenciaIdObj = body.get("referenciaId");
        String razon = (String) body.get("razon");

        if (tipo == null || referenciaIdObj == null || razon == null || razon.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Faltan campos obligatorios: tipo, referenciaId, razon"));
        }

        if (razon.trim().length() > 500) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La razón no puede superar los 500 caracteres"));
        }

        Long referenciaId;
        try {
            referenciaId = Long.valueOf(referenciaIdObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "referenciaId debe ser un número válido"));
        }

        try {
            Reporte reporte = reporteService.crearReporte(userId, tipo.toLowerCase(), referenciaId, razon.trim());
            return ResponseEntity.ok(Map.of("exito", "Reporte enviado correctamente", "id", reporte.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todos los reportes (solo admin).
     */
    @GetMapping
    public ResponseEntity<?> getTodos(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo los administradores pueden ver los reportes"));
        }

        return ResponseEntity.ok(reporteService.getTodos());
    }

    /**
     * Obtener reportes filtrados por tipo (solo admin).
     * Ej: GET /api/reporte/tipo/addon
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> getPorTipo(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable String tipo) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo los administradores pueden ver los reportes"));
        }

        return ResponseEntity.ok(reporteService.getPorTipo(tipo.toLowerCase()));
    }

    /**
     * Obtener reportes sobre una referencia concreta (solo admin).
     * Ej: GET /api/reporte/tipo/addon/5
     */
    @GetMapping("/tipo/{tipo}/{referenciaId}")
    public ResponseEntity<?> getPorReferencia(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable String tipo,
            @PathVariable Long referenciaId) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo los administradores pueden ver los reportes"));
        }

        return ResponseEntity.ok(reporteService.getPorReferencia(tipo.toLowerCase(), referenciaId));
    }

    /**
     * Eliminar un reporte (solo admin).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo los administradores pueden eliminar reportes"));
        }

    reporteService.eliminarReporte(id);
        return ResponseEntity.ok(Map.of("exito", "Reporte eliminado"));
    }

    /**
     * Deshabilitar la entidad referenciada por un reporte (solo admin).
     */
    @PostMapping("/deshabilitar/{id}")
    public ResponseEntity<?> deshabilitar(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo los administradores pueden realizar esta acción"));
        }

        try {
            reporteService.deshabilitarReferencia(id);
            return ResponseEntity.ok(Map.of("exito", "Entidad deshabilitada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
