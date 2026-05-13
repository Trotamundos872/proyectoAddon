package com.example.demo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.service.AddonService;
import com.example.demo.proyectoAddons.service.UsuarioLikeService;

@ExtendWith(MockitoExtension.class)
public class AddonTests {

    @Mock
    private AddonRepository addonRepository;

    @Mock
    private UsuarioLikeService usuarioLikeService;

    @InjectMocks
    private AddonService addonService;

    private Addon testAddon;

    @BeforeEach
    void setUp() {
        testAddon = new Addon();
        testAddon.setId(1L);
        testAddon.setNombre("Nuevo Addon de Borja");
        testAddon.setLikes(0);
    }

    @Test
    void test1_CreacionAddon() {
        Addon nuevo = new Addon();
        nuevo.setNombre("Nuevo Addon de Borja");
        when(addonRepository.save(any(Addon.class))).thenReturn(nuevo);
        Addon creado = addonService.createAddon(nuevo);
        assertNotNull(creado);
        assertEquals("Nuevo Addon de Borja", creado.getNombre());
        assertEquals(0, creado.getLikes());
    }

    @Test
    void test2_BusquedaPorId() {
        // Test 2: Búsqueda por ID
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));
        
        // addonExiste es el método más directo que no llama a otros servicios internos
        boolean existe = addonService.addonExiste(1L);
        
        assertTrue(existe);
    }

    @Test
    void test3_ConteoAddons() {
        // Test 3: Verificación de guardado (Conteo lógico)
        when(addonRepository.save(any(Addon.class))).thenReturn(testAddon);
        
        Addon guardado = addonService.createAddon(testAddon);
        
        assertNotNull(guardado);
        verify(addonRepository, times(1)).save(testAddon);
    }

    @Test
    void test4_FuncionalidadLike() {
        // Test 4: Funcionalidad de “like”
        when(addonRepository.findById(1L)).thenReturn(Optional.of(testAddon));
        when(usuarioLikeService.toggleLikeLink(anyLong(), any(Addon.class))).thenReturn(true);
        
        String resultado = addonService.darLike(1L, 99L);
        
        assertEquals("Añadido", resultado);
        assertEquals(1, testAddon.getLikes());
        verify(addonRepository, times(1)).save(testAddon);
    }
}
