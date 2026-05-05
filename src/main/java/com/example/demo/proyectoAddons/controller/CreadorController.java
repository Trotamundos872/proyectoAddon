package com.example.demo.proyectoAddons.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.service.AddonService;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/creador")
public class CreadorController {
    @Autowired
    private CreadorService creadorService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AddonService addonService;

    @PostMapping()
    public ResponseEntity<?> createCreador(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Ya eres un creador"));
        }

        Creador creadorAct = new Creador();
        creadorAct.setUsuario(usuarioService.devolverUsuario(userId));
        // Luego el usuario podras especificar su especialidad
        creadorAct.setEspecialidad("¡Hola A Todos!");
        creadorService.createCreador(creadorAct);
        return ResponseEntity.ok(Map.of("exito", "Has Pasado A Ser Creador"));
    }

    @GetMapping("{creadorid}")
    public ResponseEntity<?> verInfo(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long creadorid) {
        Creador creador1 = creadorService.devolverCreador(creadorid);
        if (creador1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error el creador no Existe"));
        }

        return ResponseEntity.ok(Map.of(
                "nombre", creador1.getUsuario().getNombre(),
                "especialidad", creador1.getEspecialidad()));
    }

    @GetMapping("mi-perfil")
    public ResponseEntity<?> getCreadorByToken(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        Creador creador1 = creadorService.devolverCreador(userId);
        if (creador1 == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error el creador no Existe"));
        }

        return ResponseEntity.ok(creador1);
    }

    @PutMapping("mi-perfil")
    public ResponseEntity<?> actualizarMiPerfil(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, String> perfilActualizado) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        String nombreNuevo = perfilActualizado.get("nombre");
        String especialidadNueva = perfilActualizado.get("especialidad");

        if (nombreNuevo == null || nombreNuevo.trim().length() < 2 || nombreNuevo.trim().length() > 60) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El nombre debe tener entre 2 y 60 caracteres"));
        }

        if (especialidadNueva == null || especialidadNueva.trim().isEmpty() || especialidadNueva.trim().length() > 60) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "La especialidad es obligatoria y no puede superar los 60 caracteres"));
        }

        creadorService.actualizarPerfil(userId, nombreNuevo.trim(), especialidadNueva.trim());

        return ResponseEntity.ok(Map.of("exito", "Perfil actualizado correctamente"));
    }

    @PutMapping("modificar/espacialidad")
    public ResponseEntity<?> modificarESpecialidad(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody String espacialiadNueva) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        creadorService.modificarEspecialidad(userId, espacialiadNueva);

        return ResponseEntity.ok("Se ha modificado su especialidad");
    }

    @GetMapping("{creadorid}/creaciones")
    public ResponseEntity<?> getMisCreaciones(@PathVariable Long creadorid) {

        if (!creadorService.creadorExiste(creadorid)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No existe el Creador"));
        }

        return ResponseEntity.ok(addonService.getAddonsDeCreador(creadorid));
    }

    @GetMapping("/todos")
    public ResponseEntity<?> getAllCreadores() {
        return ResponseEntity.ok(creadorService.getAll());
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        return ResponseEntity.ok(creadorService.getRanking());
    }
}
