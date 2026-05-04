package com.example.demo.proyectoAddons.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Subscripcion;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.SubscripcionService;
import com.example.demo.proyectoAddons.service.UsuarioService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscripcion")
public class SubscripcionController {
    @Autowired
    private SubscripcionService subsService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CreadorService creadorService;
    @Autowired
    private JWTService jwtService;

    @PostMapping("/susbscribe/{idCreador}")
    public ResponseEntity<?> createSubscripcion(
            @RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long idCreador) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        // Si ya está suscrito, la eliminamos (unsubscribe)
        Long subId = subsService.getSubcripcionID(idCreador, userId);
        if (subId != null) {
            subsService.deleteSubscripcion(subId);
            return ResponseEntity.ok(Map.of("respuesta", "Subscripcion eliminada"));
        }

        Creador creadoraSuscribir = creadorService.devolverCreador(idCreador);
        Usuario nuevoSubscriptor = usuarioService.devolverUsuario(userId);

        if (creadoraSuscribir == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Creador No Valido"));
        }

        Subscripcion objSubs = new Subscripcion();
        objSubs.setNotificar(false);
        objSubs.setCreador(creadoraSuscribir);
        objSubs.setUsuario(nuevoSubscriptor);

        return ResponseEntity.ok(subsService.createSubscripcion(objSubs));
    }

    @GetMapping("/estado/{idCreador}")
    public ResponseEntity<?> checkEstado(
            @RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long idCreador) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.ok(Map.of("subscrito", false));
        }

        boolean subscrito = subsService.getSubcripcionID(idCreador, userId) != null;
        return ResponseEntity.ok(Map.of("subscrito", subscrito));
    }

    @GetMapping("/subscritos")
    public ResponseEntity<?> verSubscripciones(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        return ResponseEntity.ok(subsService.getSubscribidos(userId));
    }

    @GetMapping("/detalles-subscritos")
    public ResponseEntity<?> verDetallesSubscripciones(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        List<Creador> creadores = subsService.getSubscribidos(userId);
        List<Map<String, Object>> detalles = creadores.stream().map(c -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            map.put("especialidad", c.getEspecialidad());
            map.put("nombre", c.getUsuario() != null ? c.getUsuario().getNombre() : "Desconocido");
            return map;
        }).toList();

        return ResponseEntity.ok(detalles);
    }

    @PutMapping("modificar/{idCreador}")
    public ResponseEntity<?> modificarNotificaciones(
            @RequestHeader(name = "Authorization", required = false) String authHeader, @PathVariable Long idCreador) {

        Long userId = jwtService.obtenerId(authHeader);
        Creador creador = creadorService.devolverCreador(idCreador);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (creador == null || !(subsService.getSubscribidos(userId).contains(creador))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Creador No Valido"));
        }

        // la respuesta nunca dara false en este caso, ya que miramos antea.
        boolean respuesta = subsService.modificarNotificaciones(subsService.getSubcripcionID(idCreador, userId));

        return ResponseEntity.ok(Map.of("respuesta", "Subscripcion alterada"));
    }

}