package service;

import Entidad.ReglaApp;
import Entidad.ReglaAppDetalle;
import Entidad.ReglaAppDestino;
import Entidad.ReglaAppExcepcion;
import Entidad.Tablet;
import repository.ReglaAppRepository;
import repository.ReglaAppDetalleRepository;
import repository.ReglaAppDestinoRepository;
import repository.ReglaAppExcepcionRepository;
import repository.TabletRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReglaAppsService {

    private final ReglaAppRepository reglaRepository;
    private final ReglaAppDetalleRepository detalleRepository;
    private final ReglaAppDestinoRepository destinoRepository;
    private final ReglaAppExcepcionRepository excepcionRepository;
    private final TabletRepository tabletRepository;

   public ReglaAppsService(
        ReglaAppRepository reglaRepository,
        ReglaAppDetalleRepository detalleRepository,
        ReglaAppDestinoRepository destinoRepository,
        ReglaAppExcepcionRepository excepcionRepository,
        TabletRepository tabletRepository
) {
    this.reglaRepository = reglaRepository;
    this.detalleRepository = detalleRepository;
    this.destinoRepository = destinoRepository;
    this.excepcionRepository = excepcionRepository;
    this.tabletRepository = tabletRepository;
}

    public Set<String> obtenerReglaEfectiva(Tablet tablet) {

        Set<String> paquetes = new LinkedHashSet<>();

        if (tablet == null || tablet.getActivo() == null) {
            return paquetes;
        }

        List<ReglaApp> reglasActivas =
                reglaRepository.findByActivaTrue();

        for (ReglaApp regla : reglasActivas) {

            if (!aplicaRegla(regla, tablet)) {
                continue;
            }

            List<ReglaAppDetalle> detalles =
                    detalleRepository.findByReglaId(regla.getId());

            for (ReglaAppDetalle detalle : detalles) {

                String packageName = detalle.getPackageName();

                if (packageName != null && !packageName.isBlank()) {
                    paquetes.add(packageName.trim());
                }
            }
        }

        aplicarExcepciones(
                tablet.getActivo(),
                paquetes
        );

        return paquetes;
    }

    private boolean aplicaRegla(
            ReglaApp regla,
            Tablet tablet
    ) {

        List<ReglaAppDestino> destinos =
                destinoRepository.findByReglaId(regla.getId());

        for (ReglaAppDestino destino : destinos) {

            String tipo = destino.getTipoDestino();

            if (tipo == null) {
                continue;
            }

            switch (tipo.toUpperCase()) {

                case "TODAS":
                    return true;

                case "DISPOSITIVO":

                    if (iguales(
                            tablet.getActivo(),
                            destino.getValorDestino()
                    )) {
                        return true;
                    }

                    break;

                case "CATEGORIA":

                    if (iguales(
                            tablet.getCategoria(),
                            destino.getValorDestino()
                    )) {
                        return true;
                    }

                    break;

                case "PLANTA":
                    break;
            }
        }

        return false;
    }

    private void aplicarExcepciones(
            String activo,
            Set<String> paquetes
    ) {

        List<ReglaAppExcepcion> excepciones =
                excepcionRepository.findByActivo(activo);

        for (ReglaAppExcepcion excepcion : excepciones) {

            String packageName =
                    excepcion.getPackageName();

            String accion =
                    excepcion.getAccion();

            if (packageName == null || accion == null) {
                continue;
            }

            packageName = packageName.trim();

            if ("PERMITIR".equalsIgnoreCase(accion)) {
                paquetes.add(packageName);
            }

            if ("EXCLUIR".equalsIgnoreCase(accion)) {
                paquetes.remove(packageName);
            }
        }
    }

    private boolean iguales(
            String valor1,
            String valor2
    ) {

        if (valor1 == null || valor2 == null) {
            return false;
        }

        return valor1.trim()
                .equalsIgnoreCase(valor2.trim());
    }

    public List<Tablet> obtenerDispositivosAfectados(Long reglaId) {

    ReglaApp regla = reglaRepository.findById(reglaId)
            .orElseThrow(() ->
                    new RuntimeException("Regla no encontrada: " + reglaId)
            );

    List<Tablet> dispositivos = tabletRepository.findAll();

    return dispositivos.stream()
            .filter(tablet -> aplicaRegla(regla, tablet))
            .toList();
}

}