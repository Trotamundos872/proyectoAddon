package com.example.demo.proyectoAddons.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;


import jakarta.validation.Valid;

@Service
public class CreadorService {

    @Autowired
    private CreadorrRepository creadorRepository;
    @Autowired
    private UsuarioService usuarioService;

    public Creador createCreador(Creador creador) {
        creador.setId(creador.getUsuario().getId());
        return creadorRepository.save(creador);
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

    public boolean actualizarPerfil(Long id, String nombreNuevo, String especialidadNueva) {
        Creador creadorAct = devolverCreador(id);
        if (creadorAct == null || creadorAct.getUsuario() == null) {
            return false;
        }

        Usuario usuarioAct = creadorAct.getUsuario();
        usuarioAct.setNombre(nombreNuevo);
        creadorAct.setEspecialidad(especialidadNueva);

        usuarioService.guardarUsuario(usuarioAct);
        creadorRepository.save(creadorAct);
        return true;
    }
}
