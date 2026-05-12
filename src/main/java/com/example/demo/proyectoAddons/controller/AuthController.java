package com.example.demo.proyectoAddons.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CreadorService creadorService;

    private final JWTService jwtService;

    public AuthController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    // Valida las credenciales y devuelve un token JWT si son correctas.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario req) {
        // El usuario SOLO nos pasa el email y contraseña

        if (!usuarioService.verificarUsuario(req)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }

        Usuario userVerificado = usuarioService
                .devolverUsuario(usuarioService.devolverUsuarioPorCorreo(req.getEmail()));

        if (userVerificado.getDeprecado() != null && userVerificado.getDeprecado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Esta cuenta ha sido deshabilitada por administración"));
        }

        Long usuarioId = userVerificado.getId();
        String username = userVerificado.getNombre();

        String token = jwtService.generarToken(username, usuarioId);

        String rolUsuario = "usuario";
        if (creadorService.creadorExiste(usuarioId)) {
            rolUsuario = "creador";
        }

        return ResponseEntity.ok(Map.of(
                "token", token,
                "rol", rolUsuario,
                "tipo", "Bearer",
                "expiracion", "1 hora"));
    }

    @GetMapping("/testlogin")
    public ResponseEntity<?> testear(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }
        return ResponseEntity.ok(Map.of("userId", userId));
    }

    // Elimina el token.
    @GetMapping("/removerJWT")
    public ResponseEntity<?> remover(@RequestHeader(name = "Authorization", required = false) String authHeader) {

        return ResponseEntity.ok(Map.of("respuesta", "Token Borrado"));
    }

    // Renueva el token JWT. Valida el token y genera uno nuevo.
    @GetMapping("/renovarJWT")
    public ResponseEntity<?> renovar(@RequestHeader(name = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Token no proporcionado o formato incorrecto"));
        }

        String token = authHeader.substring(7); // Elimina el prefijo "Bearer "
        String usuario = jwtService.validarYObtenerUsuario(token);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        Long usuarioId = jwtService.obtenerId(token);

        String nuevaToken = jwtService.generarToken(usuario, usuarioId);

        return ResponseEntity.ok(Map.of("token", nuevaToken));
    }
}
