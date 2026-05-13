package com.example.demo.proyectoAddons.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.proyectoAddons.model.Addon;
import com.example.demo.proyectoAddons.model.Archivo;
import com.example.demo.proyectoAddons.model.Creador;
import com.example.demo.proyectoAddons.repository.AddonRepository;
import com.example.demo.proyectoAddons.repository.ArchivoRepository;
import com.example.demo.proyectoAddons.repository.CreadorrRepository;

@Service
public class AddonService {

    @Autowired
    private AddonRepository addonRepository;

    @Autowired
    private CreadorrRepository creadorRepository;

    @Autowired
    private UsuarioLikeService usuarioLikeService;

    @Autowired
    private ArchivoRepository archivoRepository;

    public Addon createAddon(Addon addonContent) {
        addonContent.setLikes(0);
        return addonRepository.save(addonContent);
    }

    public Addon devolverAddon(Long id) {
        Addon addon = addonRepository.findById(id).orElse(null);
        if (addon != null) {
            this.rellenarCreadoresAddon(addon);
        }
        return addon;
    }

    public Addon updateAddon(Long id, Addon addonContent) {
        Addon addonExistente = addonRepository.findById(id).orElse(null);
        if (addonExistente != null) {
            addonExistente.setNombre(addonContent.getNombre());
            addonExistente.setDescripcion(addonContent.getDescripcion());
            addonExistente.setTipo(addonContent.getTipo());
            addonExistente.setTag(addonContent.getTag());
            addonExistente.setUrlMiniatura(addonContent.getUrlMiniatura());
            addonExistente.setTextoAddon(addonContent.getTextoAddon());
            addonExistente.setDeprecado(addonContent.getDeprecado());
            return addonRepository.save(addonExistente);
        }
        return null;
    }

    @Transactional
    public void eliminarAddonCompleto(Long idAddon) {
        archivoRepository.deleteByAddonId(idAddon);
        List<Long> creadoresIds = addonRepository.getRelacionesPorAddon(idAddon);
        for (Long cId : creadoresIds) {
            addonRepository.deleteDatosDeCreadorAddon(cId, idAddon);
        }
        usuarioLikeService.eliminarLikesDeAddon(idAddon);
        addonRepository.deleteById(idAddon);
    }

    public boolean esColaborador(Long userId, Long idAddon) {
        String status = addonRepository.getStatusCreadorAddon(userId, idAddon);
        return "colaborador".equalsIgnoreCase(status);
    }

    public List<Addon> getAlLAddons() {
        List<Addon> addons = addonRepository.findAll();
        List<Addon> filtrados = new ArrayList<>();
        
        for (Addon addon : addons) {
            // Solo incluimos el addon si NO está deprecado
            if (addon.getDeprecado() != null && addon.getDeprecado()) {
                continue;
            }

            // Solo incluimos el addon si tiene al menos un archivo
            List<?> archivos = archivoRepository.findByAddonId(addon.getId());
            if (archivos != null && !archivos.isEmpty()) {
                this.rellenarCreadoresAddon(addon);
                filtrados.add(addon);
            }
        }
        return filtrados;
    }

    private void rellenarCreadoresAddon(Addon addon) {
        List<Long> idsCreadores = this.getCreadorIdsDeUnAddon(addon.getId());
        addon.setIdsCreadores(idsCreadores);
        addon.setNombresCreadores(this.getCreadorNombreDeUnAddon(idsCreadores));
    }

    public List<Addon> buscarPorCoincidencia(String texto, String orden, String categoria) {
        List<Addon> todosLosAddons = this.getAlLAddons();
        List<Addon> addonsSeleccionados = new ArrayList<>();

        for (Addon addonAct : todosLosAddons) {
            // Filtro por categoría, puede no pasarlo
            if (categoria != null && !categoria.isEmpty() && !"all".equalsIgnoreCase(categoria)) {
                boolean matchesCategory = false;
                if (addonAct.getTipo() != null && addonAct.getTipo().equalsIgnoreCase(categoria)) {
                    matchesCategory = true;
                }
                if (!matchesCategory) continue; // Si no coincide con la categoría, saltar al siguiente
            }

            // Filtro por texto de búsqueda
            String searchLower = (texto != null) ? texto.toLowerCase() : "";
            if (searchLower.isEmpty() || 
                (addonAct.getNombre() != null && addonAct.getNombre().toLowerCase().contains(searchLower))
                || (addonAct.getDescripcion() != null && addonAct.getDescripcion().toLowerCase().contains(searchLower))
                || (addonAct.getTag() != null && addonAct.getTag().toLowerCase().contains(searchLower))
                || (addonAct.getTipo() != null && addonAct.getTipo().toLowerCase().contains(searchLower))
                || (addonAct.getTextoAddon() != null && addonAct.getTextoAddon().toLowerCase().contains(searchLower))) {
                addonsSeleccionados.add(addonAct);
            }
        }


        if (orden != null) {
            if ("likes".equalsIgnoreCase(orden)) {
                addonsSeleccionados.sort((a, b) -> {
                    int likesA = a.getLikes() != null ? a.getLikes() : 0;
                    int likesB = b.getLikes() != null ? b.getLikes() : 0;
                    return Integer.compare(likesB, likesA); // Descendente
                });
            } else if ("alfabetico".equalsIgnoreCase(orden)) {
                addonsSeleccionados.sort((a, b) -> {
                    String nomA = a.getNombre() != null ? a.getNombre() : "";
                    String nomB = b.getNombre() != null ? b.getNombre() : "";
                    return nomA.compareToIgnoreCase(nomB); // A-Z
                });
            } else if ("reciente".equalsIgnoreCase(orden)) {
                addonsSeleccionados.sort((a, b) -> {
                    LocalDateTime fechaA = obtenerFechaMasReciente(a);
                    LocalDateTime fechaB = obtenerFechaMasReciente(b);
                    return fechaB.compareTo(fechaA); // Descendente (más reciente primero)
                });
            }
        }

        return addonsSeleccionados;
    }

    private LocalDateTime obtenerFechaMasReciente(Addon addon) {
        List<Archivo> archivos = archivoRepository.findByAddonId(addon.getId());
        if (archivos == null || archivos.isEmpty()) {
            return LocalDateTime.MIN;
        }
        
        LocalDateTime masReciente = LocalDateTime.MIN;
        for (Archivo archivo : archivos) {
            if (archivo.getFecha() != null && archivo.getFecha().isAfter(masReciente)) {
                masReciente = archivo.getFecha();
            }
        }
        return masReciente;
    }

    public boolean addonExiste(Long id) {
        return addonRepository.findById(id).isPresent();
    }

    public String darLike(Long idAddon, Long idUsuario) {
        Optional<Addon> addonAct = addonRepository.findById(idAddon);

        if (addonAct.isPresent()) {
            Addon addon = addonAct.get();
            boolean likeAñadido = usuarioLikeService.toggleLikeLink(idUsuario, addon);

            if (likeAñadido) {
                if (addon.getLikes() == null) {
                    addon.setLikes(1);
                } else {
                    addon.setLikes(addon.getLikes() + 1);
                }
            } else {
                if (addon.getLikes() == null || addon.getLikes() <= 0) {
                    addon.setLikes(0);
                } else {
                    addon.setLikes(addon.getLikes() - 1);
                }
            }
            addonRepository.save(addon);
            return likeAñadido ? "Añadido" : "Quitado";
        }
        return "NoExiste";
    }

    public boolean haDadoLike(Long idAddon, Long idUsuario) {
        return usuarioLikeService.haDadoLike(idUsuario, idAddon);
    }

    @Transactional
    public void linkAddonCreador(Long idCreador, Long idAddon, String status) {
        // RESET POR SI ES RECHAZADO
        addonRepository.deleteDatosDeCreadorAddon(idCreador, idAddon);

        addonRepository.insertarCreadorAddon(idCreador, idAddon, status);
    }

    @Transactional
    public void actualizarStatusCreadorAddon(Long idCreador, Long idAddon, String status) {
        addonRepository.actualizarStatusCreadorAddon(idCreador, idAddon, status);
    }

    @Transactional
    public List<String> getCreadorNombreDeUnAddon(Long idAddon) {
        List<String> listaCreadoresNombre = new ArrayList<>();
        List<Long> idsCreadores = addonRepository.getRelacionesPorAddon(idAddon);

        for (Long idCreador : idsCreadores) {
            Creador creadorAct = creadorRepository.findById(idCreador).orElse(null);
            if (creadorAct != null && creadorAct.getUsuario() != null) {
                listaCreadoresNombre.add(creadorAct.getUsuario().getNombre());
            }
        }
        return listaCreadoresNombre;
    }

    public List<String> getCreadorNombreDeUnAddon(List<Long> idsCreadores) {
        List<String> listaCreadoresNombre = new ArrayList<>();

        for (Long idCreador : idsCreadores) {
            Creador creadorAct = creadorRepository.findById(idCreador).orElse(null);
            if (creadorAct != null && creadorAct.getUsuario() != null) {
                listaCreadoresNombre.add(creadorAct.getUsuario().getNombre());
            }
        }
        return listaCreadoresNombre;
    }

    @Transactional
    public List<Long> getCreadorIdsDeUnAddon(Long idAddon) {
        return addonRepository.getRelacionesPorAddon(idAddon);
    }

    public boolean esCreadorOriginal(Long idCreador, Long idAddon) {
        boolean esCreadorOriginal = false;
        for (String variable : addonRepository.getAddonsCreado(idAddon, idCreador)) {
            if (variable.equals("creador"))
                esCreadorOriginal = true;
        }
        return esCreadorOriginal;
    }

    public boolean esCreadorOColaborador(Long idCreador, Long idAddon) {
        for (String status : addonRepository.getAddonsCreado(idAddon, idCreador)) {
            if ("creador".equals(status) || "colaborador".equals(status)) {
                return true;
            }
        }
        return false;
    }

    public boolean esCreadorInvitado(Long idCreador, Long idAddon) {
        boolean esCreadorInvitado = false;
        for (String variable : addonRepository.getAddonsCreado(idAddon, idCreador)) {
            if (variable.equals("pendiente") || variable.equals("colaborador"))
                esCreadorInvitado = true;
        }
        return esCreadorInvitado;
    }

    public List<Addon> getAddonsDeCreador(Long idCreador) {
        List<Addon> listadEAddonsPropios = new ArrayList<>();
        for (Long addonid : addonRepository.getAddonsDeCreador(idCreador)) {
            addonRepository.findById(addonid).ifPresent(addon -> {
                this.rellenarCreadoresAddon(addon);
                listadEAddonsPropios.add(addon);
            });
        }

        return listadEAddonsPropios;
    }

    public List<Addon> getAddonsDeCreadorPendiente(Long idCreador) {
        List<Addon> listadEAddonsPropios = new ArrayList<>();
        for (Long addonid : addonRepository.getAddonsDeCreadorPendiente(idCreador)) {
            addonRepository.findById(addonid).ifPresent(listadEAddonsPropios::add);
        }

        return listadEAddonsPropios;
    }

    public List<Map<String, Object>> getCreadoresFullDeUnAddon(Long idAddon) {
        List<Map<String, Object>> result = new ArrayList<>();
        Optional<Addon> addonOpt = addonRepository.findById(idAddon);
        if (addonOpt.isPresent()) {
            List<Creador> todosLosCreadores = creadorRepository.findAll();
            for (Creador c : todosLosCreadores) {
                String status = addonRepository.getStatusCreadorAddon(c.getId(), idAddon);
                if (status != null) {
                    Map<String, Object> cData = new HashMap<>();
                    cData.put("id", c.getId());
                    cData.put("nombre", c.getUsuario().getNombre());
                    cData.put("status", status);
                    result.add(cData);
                }
            }
        }
        return result;
    }
}
