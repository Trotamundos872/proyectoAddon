package com.example.demo.proyectoAddons.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyectoAddons.model.Administrador;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.service.AdministradorService;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.UsuarioService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administrador")
public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CreadorService creadorService;
    @Autowired
    private JWTService jwtService;

    @PostMapping
    // TEMPORAL, asta crear asignador
    public Administrador createAdmin(@Valid @RequestBody Administrador admin1) {
        return administradorService.createAdministrador(admin1);
    }

    @GetMapping("/consultar-usuarios")
    public ResponseEntity<?> getUsuarios(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No Eres un Administrador"));
        }

        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }
    @GetMapping("/consultar-creadores")
    public ResponseEntity<?> getCreadores(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!administradorService.adminsitradorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No Eres un Administrador"));
        }

        return ResponseEntity.ok(creadorService.getAll());
    }


}