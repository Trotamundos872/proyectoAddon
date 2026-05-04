package com.example.demo.proyectoAddons.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.proyectoAddons.model.Addon;
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

    public List<Addon> getAlLAddons() {
        List<Addon> addons = addonRepository.findAll();
        List<Addon> filtrados = new ArrayList<>();
        
        for (Addon addon : addons) {
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
            // 1. Filtro por categoría (si se proporciona)
            if (categoria != null && !categoria.isEmpty() && !"all".equalsIgnoreCase(categoria)) {
                boolean matchesCategory = false;
                if (addonAct.getTipo() != null && addonAct.getTipo().equalsIgnoreCase(categoria)) {
                    matchesCategory = true;
                }
                if (!matchesCategory) continue; // Si no coincide con la categoría, saltar al siguiente
            }

            // 2. Filtro por texto de búsqueda
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
            // ... (resto del código de ordenación igual)
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
                    Long idA = a.getId() != null ? a.getId() : 0L;
                    Long idB = b.getId() != null ? b.getId() : 0L;
                    return idB.compareTo(idA); // Descendente
                });
            }
        }

        return addonsSeleccionados;
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
                // Solo incluimos el addon si tiene al menos un archivo
                List<?> archivos = archivoRepository.findByAddonId(addon.getId());
                if (archivos != null && !archivos.isEmpty()) {
                    this.rellenarCreadoresAddon(addon);
                    listadEAddonsPropios.add(addon);
                }
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
}
