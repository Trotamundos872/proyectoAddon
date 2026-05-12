package com.example.demo.proyectoAddons.configuration;

import com.example.demo.proyectoAddons.model.Administrador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AdministradorRepository;
import com.example.demo.proyectoAddons.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@admin.com}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:12345678}")
    private String adminPassword;

    @Value("${ADMIN_NAME:admin}")
    private String adminName;

    @Override
    public void run(String... args) throws Exception {
        if (administradorRepository.count() == 0) {
            System.out.println("No se detectó ningún administrador. Creando administrador por defecto...");

            Usuario adminUsuario = usuarioRepository.findByEmail(adminEmail).orElse(null);

            if (adminUsuario == null) {
                adminUsuario = new Usuario();
                adminUsuario.setNombre(adminName);
                adminUsuario.setEmail(adminEmail);
                adminUsuario.setPassword(passwordEncoder.encode(adminPassword));
                adminUsuario.setEsDePago(true);
                adminUsuario = usuarioRepository.save(adminUsuario);
                System.out.println("Usuario admin creado: " + adminEmail);
            } else {
                System.out.println("El usuario para el administrador ya existe.");
            }

            Administrador admin = new Administrador();
            admin.setUsuario(adminUsuario);
            admin.setId(adminUsuario.getId());
            administradorRepository.save(admin);
            System.out.println("Rol de Administrador asignado al usuario: " + adminEmail);
        } else {
            System.out.println("Ya existen administradores en la base de datos.");
        }
    }
}
