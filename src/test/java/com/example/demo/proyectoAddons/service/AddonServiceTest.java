package com.example.demo.proyectoAddons.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;
import com.example.demo.proyectoAddons.service.AddonService;
import com.example.demo.proyectoAddons.service.UsuarioLikeService;

/**
 * Pruebas Unitarias para AddonService
 * Utiliza Mockito para simular las dependencias de base de datos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de AddonService")
class AddonServiceTest {

    @Mock
    private AddonRepository addonRepository;

    @Mock
    private CreadorrRepository creadorRepository;

    @Mock
    private UsuarioLikeService usuarioLikeService;

    @InjectMocks
    private AddonService addonService;

    private Addon testAddon;
    private Usuario testUsuario;
    private Creador testCreador;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setNombre("Marcos");
        testUsuario.setEmail("jimgar@marck.com");
        testUsuario.setPassword("password123,");
        testUsuario.setEsDePago(false);

        testCreador = new Creador();
        testCreador.setId(1L);
        testCreador.setEspecialidad("Addonguiais");
        testCreador.setUsuario(testUsuario);

        testAddon = new Addon();
        testAddon.setId(1L);
        testAddon.setNombre("Super Man");
        testAddon.setTipo("addon");
        testAddon.setUrlMiniatura("https://example.com/imagen.png");
        testAddon.setDescripcion("bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla ");
        testAddon.setLikes(0);
    }

    @Test
    @DisplayName("Crear un addon exitosamente")
    void testCreateAddonSuccess() {
        when(addonRepository.save(any(Addon.class))).thenReturn(testAddon);

        Addon resultado = addonService.createAddon(testAddon);

        assertNotNull(resultado);
        assertEquals("Mi Addon Increíble", resultado.getNombre());
    }

    @Test
    @DisplayName("Obtener addon por ID")
    void testDevolverAddonSuccess() {
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));

        Addon resultado = addonService.devolverAddon(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }
}
