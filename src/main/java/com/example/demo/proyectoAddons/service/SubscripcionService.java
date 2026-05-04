package com.example.demo.proyectoAddons.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Subscripcion;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.SubscripcionRepository;

import jakarta.validation.Valid;

@Service
public class SubscripcionService {

    @Autowired
    private SubscripcionRepository subscripcionRepository;
    
    @Autowired
    private CreadorService creadorService;
    
    @Autowired
    private UsuarioService usuarioService;

    public Subscripcion createSubscripcion(Subscripcion sub1) {
        return subscripcionRepository.save(sub1);
    }

    public boolean createSubscripcion(Long idCreador, Long idUsuario) {

        Usuario usu1 = usuarioService.devolverUsuario(idUsuario);
        Creador cre1 = creadorService.devolverCreador(idCreador);

        if (usu1 == null || cre1 == null) {
            return false;
        }

        Subscripcion sub1 = new Subscripcion();
        sub1.setUsuario(usu1);
        sub1.setCreador(cre1);
        sub1.setNotificar(false);

        subscripcionRepository.save(sub1);
        return true;
    }

    public List<Subscripcion> getAllSubscripciones() {
        return subscripcionRepository.findAll();
    }

    public List<Creador> getSubscribidos(Long id) {
        List<Creador> listaCreadores = new ArrayList<>();

        for (Subscripcion subActu : this.getAllSubscripciones()) {
            if (subActu.getUsuario().getId() == id) {
                listaCreadores.add(subActu.getCreador());
            }
        }

        return listaCreadores;
    }
    public Long getSubcripcionID(Long idCreador, Long idUsuario) {
        for (Subscripcion subActu : this.getAllSubscripciones()) {
            if ((subActu.getCreador().getId()==idCreador) && (subActu.getUsuario().getId() ==idUsuario)) {
                return subActu.getSubscripcion_id();
            }
        }
        return null;
    }

    public void deleteSubscripcion(Long id) {
        subscripcionRepository.deleteById(id);
    }

    public boolean modificarNotificaciones(Long idSubscripcion) {

        Subscripcion sub = subscripcionRepository.findById(idSubscripcion).orElse(null);

        if (sub == null) {
            return false;
        }

        sub.setNotificar(!sub.isNotificar());

        subscripcionRepository.save(sub);
        return true;
    }
}
