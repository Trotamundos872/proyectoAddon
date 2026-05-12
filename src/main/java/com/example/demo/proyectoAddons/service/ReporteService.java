package com.example.demo.proyectoAddons.service;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Archivo;
import com.example.demo.proyectoAddons.model.Reporte;
import com.example.demo.proyectoAddons.model.Usuario;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.repository.ArchivoRepository;
import com.example.demo.proyectoAddons.repository.ReporteRepository;
import com.example.demo.proyectoAddons.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ReporteService {

    private static final Set<String> TIPOS_VALIDOS = Set.of("addon", "usuario", "archivo");

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private ArchivoRepository archivoRepository;

    public Reporte crearReporte(Long usuarioId, String tipo, Long referenciaId, String razon) {
        if (!TIPOS_VALIDOS.contains(tipo)) {
            throw new IllegalArgumentException("Tipo de reporte inválido. Debe ser: addon, usuario o archivo");
        }

        Usuario usuario = usuarioService.devolverUsuario(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuario);
        reporte.setTipo(tipo);
        reporte.setReferenciaId(referenciaId);
        reporte.setRazon(razon);
        reporte.setFecha(LocalDateTime.now());

        return reporteRepository.save(reporte);
    }

    public List<Reporte> getTodos() {
        return reporteRepository.findAll();
    }

    public List<Reporte> getPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    public List<Reporte> getPorReferencia(String tipo, Long referenciaId) {
        return reporteRepository.findByTipoAndReferenciaId(tipo, referenciaId);
    }

    public List<Reporte> getPorUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioId(usuarioId);
    }

    public void eliminarReporte(Long id) {
        reporteRepository.deleteById(id);
    }

    @Transactional
    public void deshabilitarReferencia(Long reporteId) {
        Reporte reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        String tipo = reporte.getTipo().toLowerCase();
        Long refId = reporte.getReferenciaId();

        switch (tipo) {
            case "usuario":
                Usuario u = usuarioRepository.findById(refId).orElse(null);
                if (u != null) {
                    u.setDeprecado(true);
                    usuarioRepository.save(u);
                }
                break;
            case "addon":
                Addon a = addonRepository.findById(refId).orElse(null);
                if (a != null) {
                    a.setDeprecado(true);
                    addonRepository.save(a);
                }
                break;
            case "archivo":
                Archivo arc = archivoRepository.findById(refId).orElse(null);
                if (arc != null) {
                    arc.setDisponible(false);
                    arc.setMotivoRechazo("Bloqueado por administración");
                    archivoRepository.save(arc);
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado para deshabilitar");
        }
    }
}
