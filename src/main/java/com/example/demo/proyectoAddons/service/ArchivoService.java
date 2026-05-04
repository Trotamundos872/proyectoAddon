package com.example.demo.proyectoAddons.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.proyectoAddons.model.Archivo;
import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.repository.ArchivoRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArchivoService {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private AddonService addonService;

    public Archivo guardarArchivo(Archivo archivo, Long addonId, Long userId) {
        // Verificar si el addon existe
        Addon addon = addonService.devolverAddon(addonId);
        if (addon == null) {
            return null;
        }

        // Verificar si el usuario es el creador o colaborador del addon
        boolean esAutorizado = addonService.esCreadorOColaborador(userId, addonId);
        if (!esAutorizado) {
            return null;
        }

        // Asignar el addon al archivo y asegurar campos por defecto si no vienen
        archivo.setAddon(addon);
        
        // Si el tipo no viene informado, heredamos el del addon
        if (archivo.getTipo() == null || archivo.getTipo().isBlank()) {
            archivo.setTipo(addon.getTipo());
        }

        if (archivo.getFecha() == null) {
            archivo.setFecha(LocalDateTime.now());
        }
        if (archivo.getNumeroDescargas() == null) {
            archivo.setNumeroDescargas(0);
        }

        try {
            return archivoRepository.save(archivo);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Archivo> getArchivosPorAddon(Long addonId) {
        return archivoRepository.findByAddonId(addonId);
    }
}
