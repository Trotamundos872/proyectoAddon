package com.example.demo.proyectoAddons.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.demo.proyectoAddons.service.ArchivoService;
import com.example.demo.proyectoAddons.service.JWTService;
import com.example.demo.proyectoAddons.model.Archivo;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Value("${app.media.base-url}")
    private String mediaBaseUrl;

    @Autowired
    private ArchivoService archivoService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(
            @RequestHeader(name = "Authorization", required = false) String authHeader,
            @RequestParam Long idAddon,
            @RequestBody Archivo archivo) {

        Long userId = jwtService.obtenerId(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o expirado"));
        }

        // Limpiar campos sensibles
        archivo.setMotivoRechazo("");
        archivo.setDisponible(true);

        Archivo guardado = archivoService.guardarArchivo(archivo, idAddon, userId);
        if (guardado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se pudo subir el archivo. Verifique que el addon existe y usted es el creador/colaborador."));
        }

        return ResponseEntity.ok(guardado);
    }

    @GetMapping("/addon/{idAddon}")
    public ResponseEntity<List<Archivo>> getArchivosPorAddon(@PathVariable Long idAddon) {
        List<Archivo> archivos = archivoService.getArchivosPorAddon(idAddon);
        
        // Formatear la URL para el frontend
        for (Archivo archivo : archivos) {
            String uuidUrl = archivo.getUrl().replaceAll("\\s+", "_");
            archivo.setUrl(buildMediaUrl(uuidUrl));
        }
        
        return ResponseEntity.ok(archivos);
    }

    @PostMapping("/descargar/{idArchivo}")
    public ResponseEntity<?> registrarDescarga(@PathVariable Long idArchivo) {
        archivoService.incrementarDescargas(idArchivo);
        return ResponseEntity.ok(Map.of("mensaje", "Descarga registrada"));
    }

    private String buildMediaUrl(String uuidUrl) {
        String normalizedMediaBaseUrl = mediaBaseUrl.endsWith("/")
                ? mediaBaseUrl.substring(0, mediaBaseUrl.length() - 1)
                : mediaBaseUrl;
        return normalizedMediaBaseUrl + "/" + uuidUrl;
    }
}
