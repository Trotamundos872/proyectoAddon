package com.example.demo.proyectoAddons.service;


import java.util.List;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.UsuarioLike;
import com.example.demo.proyectoAddons.repository.UsuarioLikeRepository;

import jakarta.validation.Valid;

@Service
public class UsuarioLikeService {

    @Autowired
    private UsuarioLikeRepository usuarioLikeRepository;

    @Autowired
    private UsuarioService usuarioService;
    
    public Boolean toggleLikeLink(Long idUser, Addon addon) {
        int likeCount = usuarioLikeRepository.getSiDarLike(idUser, addon.getId());
        
        if (likeCount == 0) {
            UsuarioLike instanciaDeLike = new UsuarioLike();
            instanciaDeLike.setAddon(addon);
            instanciaDeLike.setUsuario(usuarioService.devolverUsuario(idUser));
            instanciaDeLike.setId(idUser + " " + addon.getId());
            usuarioLikeRepository.save(instanciaDeLike);
            return true; // Like añadido 
        } else {
            usuarioLikeRepository.deleteById(idUser + " " + addon.getId());
            return false; // Like borrado 
        }
    }

    public boolean haDadoLike(Long idUser, Long idAddon) {
        return usuarioLikeRepository.getSiDarLike(idUser, idAddon) > 0;
    }


}
