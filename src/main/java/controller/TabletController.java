package controller;

import Entidad.Tablet;
import service.TabletService;
import service.WebTitleService;
import repository.TabletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import Dto.TabletDashboardDTO;
import Dto.TabletDashboardProjection;
import Dto.WebUrlDTO;
import Dto.DashboardDTO;
import Dto.LocationDTO;
import java.util.stream.Collectors;
import com.example.monitoreo.MdmSocketHandler;
import service.GpsHistoryService;
import Dto.PolicyDTO;

import java.io.IOException;
import java.util.List;
import exception.GpsTrackingAlreadyActiveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/devices") // Cambiamos esto para que coincida con la app
public class TabletController {

    @Autowired
    private TabletService tabletService;

    @Autowired
    private TabletRepository tabletRepository;

    @Autowired
    private GpsHistoryService gpsHistoryService;

    // La app llama a /devices/register
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Tablet tablet) {

        Tablet t = tabletService.procesarHeartbeat(tablet);

        if (t == null) {
            return ResponseEntity.badRequest().body("Activo obligatorio");
        }

        return ResponseEntity.ok(t);
    }

    // La app llama a /devices/{id}/heartbeat
    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<?> heartbeat(@PathVariable Long id, @RequestBody Tablet datos) {

        datos.setId(id);

        Tablet tablet = tabletService.procesarHeartbeat(datos);

        if (tablet == null) {
            return ResponseEntity.badRequest().body("Activo obligatorio");
        }

        return ResponseEntity.ok(tablet);
    }

    @GetMapping("/{id}/historial-cargador")
    public List<Object[]> obtenerHistorialCargador(
            @PathVariable Long id,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {

        return tabletService.obtenerHistorialCargador(
                id,
                fechaDesde,
                fechaHasta);

    }

    @GetMapping("/all")
    public Page<TabletDashboardProjection> obtenerTodas(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "25") int size,

            @RequestParam(defaultValue = "") String buscar,

            @RequestParam(defaultValue = "") String planta,

            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(defaultValue = "") String estado,

            @RequestParam(defaultValue = "") String estadoCargador) {

        Pageable pageable = PageRequest.of(page, size);

        System.out.println("PLANTA = [" + planta + "]");
        System.out.println("CATEGORIA = [" + categoria + "]");
        System.out.println("BUSCAR = [" + buscar + "]");
        System.out.println("ESTADO = [" + estadoCargador + "]");

        return tabletService.obtenerDashboard(
                buscar,
                planta,
                categoria,
                estadoCargador,
                estado,
                pageable);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> actualizarDispositivos() {

        MdmSocketHandler.obtenerTabletsConectadas()
                .forEach(MdmSocketHandler::solicitarActualizacion);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard")
    public DashboardDTO obtenerDashboard() {

        return tabletService.obtenerDashboard();

    }

    @GetMapping("/reporte")
    public ResponseEntity<byte[]> generarReporte(
            @RequestParam(defaultValue = "") String buscar,
            @RequestParam(defaultValue = "") String planta,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(defaultValue = "") String estadoCargador,
            @RequestParam(defaultValue = "") String estado,
            @RequestParam(defaultValue = "") String columnas) throws IOException {

        byte[] excel = tabletService.obtenerReporte(
                buscar,
                planta,
                categoria,
                estadoCargador,
                estado,
                columnas);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Reporte_MDM.xlsx");

        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

    @GetMapping("/plantas")
    public List<String> obtenerPlantas() {

        return tabletService.obtenerPlantas();
    }

    @GetMapping("/categorias")
    public List<String> obtenerCategorias() {

        return tabletService.obtenerCategorias();
    }

    @GetMapping("/lista")
    public List<Tablet> obtenerTablets() {

        return tabletService.obtenerTablets();
    }

    @GetMapping("/lista-paginada")
    public Page<Tablet> obtenerTabletsPaginadas(

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "25") int size,

            @RequestParam(defaultValue = "") String buscar) {

        Pageable pageable = PageRequest.of(page, size);

        return tabletService.obtenerTabletsSelector(
                buscar,
                pageable);
    }

    @PostMapping("/{id}/location")
    public void recibirUbicacion(
            @PathVariable Long id,
            @RequestBody LocationDTO location) {

        tabletRepository.findById(id).ifPresent(tablet -> {

            tablet.setLatitude(location.getLatitude());
            tablet.setLongitude(location.getLongitude());
            tablet.setGpsAccuracy(location.getAccuracy());
            tablet.setGpsSource(location.getSource());

            if (location.getTimestamp() != null) {
                tablet.setGpsTimestamp(
                        java.time.Instant.ofEpochMilli(location.getTimestamp())
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
            }

            tabletRepository.save(tablet);

            gpsHistoryService.guardarPunto(
                    id,
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAccuracy());

            System.out.println("GPS RECIBIDO TABLET " + id);
            System.out.println("LAT = " + location.getLatitude());
            System.out.println("LON = " + location.getLongitude());
            System.out.println("ACC = " + location.getAccuracy());
            System.out.println("SRC = " + location.getSource());

        });
    }

    @GetMapping("/{id}/location")
    public LocationDTO obtenerUbicacion(
            @PathVariable Long id) {

        Tablet tablet = tabletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));

        LocationDTO dto = new LocationDTO();

        dto.setLatitude(tablet.getLatitude());
        dto.setLongitude(tablet.getLongitude());
        dto.setAccuracy(tablet.getGpsAccuracy());
        dto.setSource(tablet.getGpsSource());

        if (tablet.getGpsTimestamp() != null) {
            dto.setTimestamp(
                    tablet.getGpsTimestamp()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
        }

        return dto;
    }

    @PostMapping("/{id}/apps")
    public void recibirApps(@PathVariable Long id, @RequestBody String body) {

        System.out.println("================================");
        System.out.println("RECIBI APPS");
        System.out.println("ID = " + id);
        System.out.println("TAMAÑO = " + body.length());
        System.out.println(body.substring(0, Math.min(300, body.length())));
        System.out.println("================================");

        tabletRepository.findById(id).ifPresent(t -> {
            t.setAppsReportadas(body);
            tabletRepository.save(t);
        });
    }

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Autowired
    private WebTitleService webTitleService;

    @PutMapping("/{id}/config")
    public Tablet actualizarConfig(@PathVariable Long id, @RequestBody Tablet config) {

        try {

            // COMANDO TEMPORAL (NO SE GUARDA EN BD)
            if ("sync_apps".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"sync_apps\"}");

                System.out.println("SYNC_APPS ENVIADO POR WEBSOCKET A TABLET " + id);

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("reboot".equals(config.getPendingCommand())) {

                boolean ack = MdmSocketHandler.enviarOrdenConAck(
                        id.toString(),
                        "{\"pending_command\":\"reboot\"}",
                        "reboot");

                if (ack) {
                    System.out.println("REBOOT CONFIRMADO POR TABLET " + id);
                } else {
                    System.out.println("REBOOT NO CONFIRMADO POR TABLET " + id);
                }

                Tablet tablet = tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));

                tablet.setPendingCommand(ack ? "ACK_REBOOT_OK" : "ACK_REBOOT_FAIL");

                return tablet;
            }

            if ("lock_device".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"lock_device\"}");

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("unlock_device".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"unlock_device\"}");

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("alarm_on".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"alarm_on\"}");

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("alarm_off".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"alarm_off\"}");

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("location_request".equals(config.getPendingCommand())) {

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"location_request\"}");

                return tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
            }

            if ("location_track_start".equals(config.getPendingCommand())) {

                Tablet tabletActualizada = tabletService.actualizarConfiguracionRemota(id, config);

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"location_track_start\"}");

                return tabletActualizada;
            }

            if ("location_track_stop".equals(config.getPendingCommand())) {

                Tablet tabletActualizada = tabletService.actualizarConfiguracionRemota(id, config);

                MdmSocketHandler.enviarOrden(
                        id.toString(),
                        "{\"pending_command\":\"location_track_stop\"}");

                return tabletActualizada;
            }

            if ("factory_reset".equals(config.getPendingCommand())) {

                boolean ack = MdmSocketHandler.enviarOrdenConAck(
                        id.toString(),
                        "{\"command\":\"factory_reset\"}",
                        "factory_reset");

                if (ack) {
                    System.out.println("FACTORY RESET CONFIRMADO POR TABLET " + id);
                } else {
                    System.out.println("FACTORY RESET NO CONFIRMADO POR TABLET " + id);
                }

                Tablet tablet = tabletRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));

                tablet.setPendingCommand(
                        ack ? "ACK_FACTORY_RESET_OK"
                                : "ACK_FACTORY_RESET_FAIL");

                return tablet;
            }

            System.out.println("============== CONTROLLER ==============");
            System.out.println("APPS = " + config.getAppsBloqueadas());
            System.out.println("REINICIO = " + config.getConfigReinicio());
            System.out.println("RESTRICCIONES = " + config.getRestricciones());
            System.out.println("URLS = " + config.getUrlsPermitidas());
            System.out.println("========================================");

            // =============================================
            // CONFIGURACIÓN NORMAL / ACCIONES MASIVAS APPS
            // =============================================

            String accionMasivaApps = config.getAccionMasivaApps();
            String appsBloqueadasOriginal = config.getAppsBloqueadas();

            boolean esAccionMasiva = "PERMITIR_TODAS".equals(accionMasivaApps) ||
                    "BLOQUEAR_TODAS".equals(accionMasivaApps);

            // Si es PERMITIR TODAS o BLOQUEAR TODAS,
            // NO guardamos ese estado temporal como política permanente.
            if (esAccionMasiva) {
                config.setAppsBloqueadas(null);
            }

            // Guardar las demás configuraciones normalmente
            Tablet tabletActualizada = tabletService.actualizarConfiguracionRemota(id, config);

            tabletActualizada.setPendingCommand(null);

            // Para el WebSocket sí mandamos la acción masiva
            if (esAccionMasiva) {

                tabletActualizada.setAppsBloqueadas(
                        appsBloqueadasOriginal);

                tabletActualizada.setAccionMasivaApps(
                        accionMasivaApps);

            } else {

                tabletActualizada.setAccionMasivaApps(null);
            }

            String jsonResponse = objectMapper.writeValueAsString(tabletActualizada);

            MdmSocketHandler.enviarOrden(
                    id.toString(),
                    jsonResponse);

            System.out.println(
                    "WEBSOCKET ENVIADO A TABLET " + id +
                            " | ACCION MASIVA = " + accionMasivaApps);

            return tabletActualizada;

        } catch (GpsTrackingAlreadyActiveException e) {

            throw e;

        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/web-title")
    public WebUrlDTO obtenerTitulo(@RequestParam String url) {

        WebUrlDTO dto = new WebUrlDTO();

        dto.setUrl(url);
        dto.setNombre(webTitleService.obtenerTitulo(url));

        return dto;
    }

    // Añade esto a tu TabletController.java
    @GetMapping("/{id}/config")
    public PolicyDTO obtenerConfiguracionLigera(@PathVariable Long id) {

        Tablet t = tabletRepository.findById(id)
                .orElseThrow();

        PolicyDTO dto = new PolicyDTO();

        dto.setAppsBloqueadas(t.getAppsBloqueadas());
        dto.setRestricciones(t.getRestricciones());
        dto.setConfigReinicio(t.getConfigReinicio());
        dto.setUrlsPermitidas(t.getUrlsPermitidas());

        return dto;
    }

    @GetMapping("/{id}")
    public Tablet obtenerPorId(@PathVariable Long id) {

        Tablet t = tabletRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));

        return t;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTablet(@PathVariable Long id) {

        System.out.println("ELIMINANDO TABLET: " + id);
        tabletService.eliminarTablet(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activo/{activo}")
    public Tablet obtenerPorActivo(@PathVariable String activo) {

        return tabletRepository.findByActivo(activo)
                .orElseThrow(() -> new RuntimeException("Tablet no encontrada"));
    }

    @ExceptionHandler(GpsTrackingAlreadyActiveException.class)
    public ResponseEntity<?> gpsTrackingActivo(
            GpsTrackingAlreadyActiveException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "GPS_TRACKING_ALREADY_ACTIVE",
                        "message", e.getMessage()));
    }

}