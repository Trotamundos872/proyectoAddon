package com.example.demo.proyectoAddons.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;
import com.example.demo.proyectoAddons.repository.UsuarioRepository;
import com.example.demo.proyectoAddons.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pruebas de Integración para AddonController
 * Prueba la integración completa entre el controlador, servicio y la base de datos
 * Utiliza una base de datos H2 en memoria para las pruebas
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Pruebas de Integración de AddonController")
class AddonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CreadorrRepository creadorRepository;

    @Autowired
    private JWTService jwtService;

    private Usuario testUsuario;
    private Creador testCreador;
    private Addon testAddon;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Limpiar datos anteriores
        addonRepository.deleteAll();
        creadorRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario de prueba
        testUsuario = new Usuario();
        testUsuario.setNombre("TestCreador");
        testUsuario.setEmail("testcreador@example.com");
        testUsuario.setPassword("password123");
        testUsuario.setEsDePago(false);
        testUsuario = usuarioRepository.save(testUsuario);

        // Crear creador asociado
        testCreador = new Creador();
        testCreador.setId(testUsuario.getId());
        testCreador.setEspecialidad("Desarrollo Web");
        testCreador.setUsuario(testUsuario);
        testCreador = creadorRepository.save(testCreador);

        // Crear addon de prueba
        testAddon = new Addon();
        testAddon.setNombre("Addon de Prueba Integración");
        testAddon.setTipo("Plugin");
        testAddon.setUrlMiniatura("https://example.com/imagen.png");
        testAddon.setDescripcion("Este es un addon creado para pruebas de integración con una descripción lo suficientemente larga");
        testAddon.setLikes(0);
        testAddon = addonRepository.save(testAddon);

        // Generar token JWT válido para el usuario creador
        validToken = "Bearer " + jwtService.generateToken(testUsuario.getId(), testUsuario.getEmail());
    }

    @Test
    @DisplayName("Obtener todos los addons - GET /api/addon")
    void testGetAllAddons() throws Exception {
        mockMvc.perform(get("/api/addon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nombre", notNullValue()))
                .andExpect(jsonPath("$[0].tipo", notNullValue()));
    }

    @Test
    @DisplayName("Obtener addon específico - GET /api/addon")
    void testGetSpecificAddon() throws Exception {
        mockMvc.perform(get("/api/addon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testAddon.getId().intValue())))
                .andExpect(jsonPath("$[0].nombre", is("Addon de Prueba Integración")))
                .andExpect(jsonPath("$[0].tipo", is("Plugin")));
    }

    @Test
    @DisplayName("Crear nuevo addon exitosamente - POST /api/addon")
    void testCreateAddonSuccess() throws Exception {
        Addon nuevoAddon = new Addon();
        nuevoAddon.setNombre("Nuevo Addon Integración");
        nuevoAddon.setTipo("Extensión");
        nuevoAddon.setUrlMiniatura("https://example.com/nueva.png");
        nuevoAddon.setDescripcion("Descripción del nuevo addon para pruebas que cumple con la longitud mínima requerida");

        mockMvc.perform(post("/api/addon")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoAddon)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Nuevo Addon Integración")))
                .andExpect(jsonPath("$.tipo", is("Extensión")))
                .andExpect(jsonPath("$.likes", is(0)));
    }

    @Test
    @DisplayName("Crear addon sin token retorna UNAUTHORIZED")
    void testCreateAddonWithoutToken() throws Exception {
        Addon nuevoAddon = new Addon();
        nuevoAddon.setNombre("Addon sin autorización");
        nuevoAddon.setTipo("Plugin");
        nuevoAddon.setUrlMiniatura("https://example.com/imagen.png");
        nuevoAddon.setDescripcion("Descripción suficientemente larga para cumplir con validaciones de longitud");

        mockMvc.perform(post("/api/addon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoAddon)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("Token")));
    }

    @Test
    @DisplayName("Crear addon sin ser creador retorna error")
    void testCreateAddonNotCreator() throws Exception {
        // Crear usuario que no es creador
        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setNombre("UsuarioNormal");
        usuarioNormal.setEmail("normal@example.com");
        usuarioNormal.setPassword("password123");
        usuarioNormal.setEsDePago(false);
        usuarioNormal = usuarioRepository.save(usuarioNormal);

        String tokenNoCreador = "Bearer " + jwtService.generateToken(usuarioNormal.getId(), usuarioNormal.getEmail());

        Addon nuevoAddon = new Addon();
        nuevoAddon.setNombre("Addon sin creador");
        nuevoAddon.setTipo("Plugin");
        nuevoAddon.setUrlMiniatura("https://example.com/imagen.png");
        nuevoAddon.setDescripcion("Descripción con longitud suficiente para pasar las validaciones requeridas");

        mockMvc.perform(post("/api/addon")
                .header("Authorization", tokenNoCreador)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoAddon)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("Creador")));
    }

    @Test
    @DisplayName("Dar like a un addon - PUT /api/addon/darlike/{idAddon}")
    void testDarLikeAddon() throws Exception {
        long likesAntes = testAddon.getLikes();

        mockMvc.perform(put("/api/addon/darlike/{idAddon}", testAddon.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito", notNullValue()));

        // Verificar que los likes se incrementaron en la base de datos
        Addon addonActualizado = addonRepository.findById(testAddon.getId()).orElseThrow();
        assert addonActualizado.getLikes() == likesAntes + 1;
    }

    @Test
    @DisplayName("Dar like sin token retorna UNAUTHORIZED")
    void testDarLikeWithoutToken() throws Exception {
        mockMvc.perform(put("/api/addon/darlike/{idAddon}", testAddon.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("Token")));
    }

    @Test
    @DisplayName("Dar like a addon inexistente retorna error")
    void testDarLikeNonExistentAddon() throws Exception {
        mockMvc.perform(put("/api/addon/darlike/{idAddon}", 999L)
                .header("Authorization", validToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", containsString("No existe")));
    }

    @Test
    @DisplayName("Obtener nombres de creadores de un addon - GET /api/addon/creadores/{idAddon}")
    void testGetCreadorNombres() throws Exception {
        mockMvc.perform(get("/api/addon/creadores/{idAddon}", testAddon.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Validar creación y persistencia de addon en BD")
    void testAddonPersistenceInDatabase() throws Exception {
        Addon addonNuevo = new Addon();
        addonNuevo.setNombre("Addon Persistencia BD");
        addonNuevo.setTipo("Tema");
        addonNuevo.setUrlMiniatura("https://example.com/tema.png");
        addonNuevo.setDescripcion("Addon creado para validar que se persiste correctamente en la base de datos relacional");

        mockMvc.perform(post("/api/addon")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addonNuevo)))
                .andExpect(status().isOk());

        // Verificar que el addon fue guardado en la base de datos
        assert addonRepository.findAll().size() >= 2;
        
        // Verificar que el último addon creado tiene el nombre correcto
        Addon addonGuardado = addonRepository.findAll().stream()
                .filter(a -> "Addon Persistencia BD".equals(a.getNombre()))
                .findFirst()
                .orElseThrow();
        
        assert addonGuardado.getNombre().equals("Addon Persistencia BD");
        assert addonGuardado.getTipo().equals("Tema");
        assert addonGuardado.getLikes() == 0;
    }

    @Test
    @DisplayName("Flujo completo: Crear addon, obtener y dar like")
    void testCompleteAddonWorkflow() throws Exception {
        // 1. Crear nuevo addon
        Addon addonWorkflow = new Addon();
        addonWorkflow.setNombre("Addon Workflow");
        addonWorkflow.setTipo("Plugin");
        addonWorkflow.setUrlMiniatura("https://example.com/workflow.png");
        addonWorkflow.setDescripcion("Addon creado para probar el flujo completo de creación obtención y like");

        String responseCrear = mockMvc.perform(post("/api/addon")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addonWorkflow)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraer ID del addon creado
        Addon addonCreado = objectMapper.readValue(responseCrear, Addon.class);
        assert addonCreado.getId() != null;

        // 2. Obtener todos los addons y verificar que está en la lista
        mockMvc.perform(get("/api/addon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + addonCreado.getId() + ")].nombre", 
                        hasItem("Addon Workflow")));

        // 3. Dar like al addon creado
        mockMvc.perform(put("/api/addon/darlike/{idAddon}", addonCreado.getId())
                .header("Authorization", validToken))
                .andExpect(status().isOk());

        // 4. Verificar que el like se registró
        Addon addonConLike = addonRepository.findById(addonCreado.getId()).orElseThrow();
        assert addonConLike.getLikes() == 1;
    }

    @Test
    @DisplayName("Validar validaciones del addon - nombre muy corto")
    void testAddonValidationShortName() throws Exception {
        Addon addonInvalido = new Addon();
        addonInvalido.setNombre("A"); // Demasiado corto
        addonInvalido.setTipo("Plugin");
        addonInvalido.setUrlMiniatura("https://example.com/imagen.png");
        addonInvalido.setDescripcion("Descripción válida con longitud suficiente para cumplir con los requisitos");

        mockMvc.perform(post("/api/addon")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addonInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Validar validaciones del addon - descripción muy corta")
    void testAddonValidationShortDescription() throws Exception {
        Addon addonInvalido = new Addon();
        addonInvalido.setNombre("Addon Válido");
        addonInvalido.setTipo("Plugin");
        addonInvalido.setUrlMiniatura("https://example.com/imagen.png");
        addonInvalido.setDescripcion("Corta"); // Demasiado corta

        mockMvc.perform(post("/api/addon")
                .header("Authorization", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addonInvalido)))
                .andExpect(status().isBadRequest());
    }
}
