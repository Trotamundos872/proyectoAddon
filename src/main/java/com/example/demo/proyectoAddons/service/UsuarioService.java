package com.example.demo.proyectoAddons.service;

import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AdministradorRepository;
import com.example.demo.proyectoAddons.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository; 

    @Autowired
    private AdministradorRepository administradorRepository;

    public UsuarioService(PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }



    public Usuario createUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        String contrseniaPlana = usuario.getPassword();
        usuario.setPassword(passwordEncoder.encode(contrseniaPlana));
        return usuarioRepository.save(usuario); 
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll(); 
    }

    public void borrarUsuarios() {
        usuarioRepository.deleteAll(); // Borra todos los usuarios
    }

    public boolean usuarioExiste(Long id) {
    return usuarioRepository.findById(id).isPresent();
    }

    public Usuario devolverUsuario(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    public Long devolverUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByEmail(correo)
                .map(Usuario::getId)
                .orElse(null);
    }

    public boolean verificarUsuario(Usuario user) {
        return usuarioRepository.findByEmail(user.getEmail())
                .map(u -> passwordEncoder.matches(user.getPassword(), u.getPassword()))
                .orElse(false);
    }

    public Integer totalUsuarios() {
        return Math.toIntExact(usuarioRepository.count());
    }

    public boolean esAdmin(Long id) {
        return administradorRepository.existsById(id);
    }

}
