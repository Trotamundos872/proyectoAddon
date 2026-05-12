package com.example.demo.proyectoAddons.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.service.AddonService;
import com.example.demo.proyectoAddons.service.CreadorService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/addon")
public class AddonController {

    private final UsuarioService usuarioService;
    @Autowired
    private AddonService addonService;
    @Autowired
    private CreadorService creadorService;
    @Autowired
    private JWTService jwtService;

    @Autowired
    AddonController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/creadores-full/{idAddon}")
    public ResponseEntity<?> getCreadoresFullDeUnAddon(@PathVariable Long idAddon) {
        return ResponseEntity.ok(addonService.getCreadoresFullDeUnAddon(idAddon));
    }

    @GetMapping("/perfil/{idcreador}")
    public ResponseEntity<?> getMisCreacionesdeCreador(@PathVariable Long idcreador) {

        if (idcreador == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "ID de creador no proporcionado"));
        }

        if (!creadorService.creadorExiste(idcreador)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "El creador no existe"));
        }

        return ResponseEntity.ok(addonService.getAddonsDeCreador(idcreador));
    }

    @PostMapping
    public ResponseEntity<?> createAddon(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Addon addonContent) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        Addon addonCreado = addonService.createAddon(addonContent);
        addonService.linkAddonCreador(userId, addonCreado.getId(), "creador");
        return ResponseEntity.ok(addonCreado);
    }

    @PutMapping("/{idAddon}")
    public ResponseEntity<?> updateAddon(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon, @Valid @RequestBody Addon addonContent) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!addonService.addonExiste(idAddon)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No existe este Addon"));
        }

        if (!addonService.esCreadorOriginal(userId, idAddon) && !addonService.esColaborador(userId, idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No tienes permisos para editar este Addon"));
        }

        Addon actualizado = addonService.updateAddon(idAddon, addonContent);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping
    public List<Addon> gettAllAddons() {
        return addonService.getAlLAddons();
    }

    @GetMapping("/{idAddon}")
    public Addon devolverAddon(@PathVariable Long idAddon) {
        return addonService.devolverAddon(idAddon);
    }

    @GetMapping("/buscar")
    public List<Addon> buscarPorCoincidencia(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String categoria) {
        return addonService.buscarPorCoincidencia(buscar, orden, categoria);
    }

    @GetMapping("/creadores/{idAddon}")
    public List<String> getCreadorNombreDeUnAddon(@PathVariable Long idAddon) {
        return addonService.getCreadorNombreDeUnAddon(idAddon);
    }

    @PostMapping("/darlike/{idAddon}")
    public ResponseEntity<?> darLike(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon) {

        Long userId = jwtService.obtenerId(authHeader);

        // Al pasar de aqui, es un usuario logueado
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!addonService.addonExiste(idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No existe este Addon"));
        }

        String resultadoLike = addonService.darLike(idAddon, userId);
        Addon addonActualizado = addonService.devolverAddon(idAddon);
        
        if (resultadoLike.equals("Añadido")) {
            return ResponseEntity.ok(Map.of(
                "exito", "Le has dado like a este Addon",
                "likes", addonActualizado.getLikes()
            ));
        } else if (resultadoLike.equals("Quitado")) {
            return ResponseEntity.ok(Map.of(
                "exito", "Has quitado tu like de este Addon",
                "likes", addonActualizado.getLikes()
            ));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error procesando el like"));
    }

    @GetMapping("/darlike/comprobar/{idAddon}")
    public ResponseEntity<?> comprobarLike(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!addonService.addonExiste(idAddon)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No existe este Addon"));
        }

        boolean haDadoLike = addonService.haDadoLike(idAddon, userId);
        
        return ResponseEntity.ok(Map.of(
            "haDadoLike", haDadoLike
        ));
    }

    @GetMapping("/invitar/enviar/{idAddon}/{idCreador}")
    public ResponseEntity<?> invitarCreador(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon, @PathVariable Long idCreador) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        if (!creadorService.creadorExiste(idCreador)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No hay ningun creador con ese id"));
        }

        if (userId == idCreador) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "no puedes invitarte a ti mismo"));
        }

        // COMPROBAR QUE EL userId es el creador original Y que existe.
        if (!addonService.esCreadorOriginal(userId, idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "no eres el creador original del addon"));
        }

        addonService.linkAddonCreador(idCreador, idAddon, "pendiente");

        return ResponseEntity.ok(Map.of("exito", "creador invitado al proyecto"));
    }

    @GetMapping("/invitar/bloquear/{idAddon}/{idCreador}")
    public ResponseEntity<?> bloquearCreador(@RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon, @PathVariable Long idCreador) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        if (!creadorService.creadorExiste(idCreador)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No hay ningun creador con ese id"));
        }

        if (userId == idCreador) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "no puedes bloquearte a ti mismo"));
        }

        // COMPROBAR QUE EL userId es el creador original Y que existe.
        if (!addonService.esCreadorOriginal(userId, idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "no eres el creador original del addon"));
        }

        addonService.linkAddonCreador(idCreador, idAddon, "rechazado");

        return ResponseEntity.ok(Map.of("exito", "has bloqueado al creador"));
    }

    @GetMapping("/invitar/aceptar/{idAddon}")
    public ResponseEntity<?> aceptarInvitacion(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        if (!addonService.esCreadorInvitado(userId, idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No te han invitado a este proyecto"));
        }

        addonService.actualizarStatusCreadorAddon(userId, idAddon, "colaborador");

        return ResponseEntity.ok(Map.of("exito", "Has aceptado la invitacion al proyecto"));
    }

    @GetMapping("/invitar/rechazar/{idAddon}")
    public ResponseEntity<?> rechazarInvitacion(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @PathVariable Long idAddon) {
        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        if (!addonService.esCreadorInvitado(userId, idAddon)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No te han invitado a este proyecto"));
        }

        addonService.actualizarStatusCreadorAddon(userId, idAddon, "rechazado");

        return ResponseEntity.ok(Map.of("exito", "Has rechazado la invitacion al proyecto"));
    }

    @GetMapping("/mis-creaciones")
    public ResponseEntity<?> getMisCreaciones(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }

        return ResponseEntity.ok(addonService.getAddonsDeCreador(userId));
    }

    @GetMapping("/mis-invitaciones")
    public ResponseEntity<?> getMisInvitaciones(
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        Long userId = jwtService.obtenerId(authHeader);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        if (!creadorService.creadorExiste(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No eres un Creador"));
        }
        return ResponseEntity.ok(addonService.getAddonsDeCreadorPendiente(userId));
    }
}
