package com.example.demo.proyectoAddons.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.UsuarioService;

import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CreadorService creadorService;

    @Autowired
    private JWTService jwtService;

    @PostMapping
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario codBar) {
        try {
            Usuario nuevoUsuario = usuarioService.createUsuario(codBar);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<?> VerInfo(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }
        
        Usuario user = usuarioService.devolverUsuario(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado"));
        }

        boolean esCreador = creadorService.creadorExiste(userId);
        
        return ResponseEntity.ok(Map.of(
            "id", userId,
            "nombre", user.getNombre(),
            "email", user.getEmail(),
            "esCreador", esCreador
        ));
    }

    //cualquier usuario puede ver si otro usuario existe
    @GetMapping("/{id}")
    public boolean usuarioExiste(@PathVariable Long id) {
        return usuarioService.usuarioExiste(id);
    }


    @GetMapping("/totalusuarios")
    public Integer depago() {
        return usuarioService.totalUsuarios();
    }
}