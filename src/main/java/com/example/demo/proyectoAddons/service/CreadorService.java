package com.example.demo.proyectoAddons.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Archivo;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.CreadorAddon;
import com.example.demo.proyectoAddons.repository.ArchivoRepository;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;

@Service
public class CreadorService {

    @Autowired
    private CreadorrRepository creadorRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AddonService addonService;
    @Autowired
    private ArchivoRepository archivoRepository;

    public Creador createCreador(Creador creador) {
        creador.setId(creador.getUsuario().getId());
        return creadorRepository.save(creador);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRanking() {
        // Cargamos los creadores con sus relaciones base (creadorAddons)
        List<Creador> creadores = creadorRepository.findAllWithAddons();
        List<Map<String, Object>> ranking = new ArrayList<>();

        for (Creador creador : creadores) {
            long totalDescargas = 0;
            Addon mejorAddon = null;
            int maxLikes = -1;

            if (creador.getCreadorAddons() != null) {
                for (CreadorAddon rel : creador.getCreadorAddons()) {
                    // Filtramos por estado aceptado u original
                    if ("colaborador".equals(rel.getStatus()) || "original".equals(rel.getStatus()) || rel.getStatus() == null) {
                        Addon addon = rel.getAddon();
                        if (addon != null) {
                            List<Archivo> archivos = archivoRepository.findByAddonId(addon.getId());
                            if (archivos != null) {
                                for (Archivo archivo : archivos) {
                                    totalDescargas += (archivo.getNumeroDescargas() != null ? archivo.getNumeroDescargas() : 0);
                                }
                            }

                            //  Addon con más likes
                            int addonLikes = (addon.getLikes() != null ? addon.getLikes() : 0);
                            if (addonLikes > maxLikes) {
                                maxLikes = addonLikes;
                                mejorAddon = addon;
                            }
                        }
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", creador.getId());
            data.put("nombre", creador.getUsuario() != null ? creador.getUsuario().getNombre() : "Desconocido");
            data.put("totalDescargas", totalDescargas);
            data.put("mejorAddonNombre", mejorAddon != null ? mejorAddon.getNombre() : "N/A");
            data.put("mejorAddonLikes", mejorAddon != null ? mejorAddon.getLikes() : 0);
            
            ranking.add(data);
        }

        // .ordenar
        ranking.sort((a, b) -> Long.compare((long) b.get("totalDescargas"), (long) a.get("totalDescargas")));

        return ranking;
    }

    public List<Creador> getAll() {
        return creadorRepository.findAll();
    }

    public String borra() {
        creadorRepository.deleteAll();
        return "Datos Borrados";
    }

    public boolean creadorExiste(Long id) {
        return creadorRepository.findById(id).isPresent();
    }

    public Creador devolverCreador(Long id) {
        if (id == null) return null;
        return creadorRepository.findById(id).orElse(null);
    }

    public boolean modificarEspecialidad(Long id, String especialidadNueva) {
        Creador creadorAct = devolverCreador(id);
        if (creadorAct != null) {
            creadorAct.setEspecialidad(especialidadNueva);
            creadorRepository.save(creadorAct);
            return true;
        }
        return false;
    }
}
