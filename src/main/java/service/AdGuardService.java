package service;

import Dto.AdGuardClienteMdmDTO;
import Dto.AdGuardConfigDTO;
import Dto.AdGuardStatsDTO;
import Dto.AdGuardStatusDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Dto.AdGuardConfigDTO;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import Entidad.Tablet;
import repository.AdGuardPoliticaTabletRepository;
import repository.TabletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import Entidad.AdGuardPoliticaTablet;
import repository.AdGuardPoliticaTabletRepository;
import java.util.HashSet;
import java.util.Set;

@Service
public class AdGuardService {

        private static final String URL = "http://172.16.2.82/control/status";

        private static final String USER = "amorales";

        private static final String PASSWORD = "@Rg3nTina11$";

        private static final String ADGUARD_BASE = "http://172.16.2.82";

        @Autowired
        private AdGuardPoliticaTabletRepository politicaTabletRepository;

        @Autowired
        private TabletRepository tabletRepository;

        public AdGuardStatusDTO obtenerEstado() {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<AdGuardStatusDTO> response = restTemplate.exchange(
                                URL,
                                HttpMethod.GET,
                                entity,
                                AdGuardStatusDTO.class);

                return response.getBody();
        }

        public AdGuardStatsDTO obtenerStats() {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<AdGuardStatsDTO> response = restTemplate.exchange(
                                "http://172.16.2.82/control/stats",
                                HttpMethod.GET,
                                entity,
                                AdGuardStatsDTO.class);

                return response.getBody();
        }

        public AdGuardConfigDTO obtenerReglas() {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<AdGuardConfigDTO> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/status",
                                HttpMethod.GET,
                                entity,
                                AdGuardConfigDTO.class);

                return response.getBody();
        }

        public String agregarDominio(String dominio) {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>();

                if (config.getUser_rules() != null) {
                        reglas.addAll(config.getUser_rules());
                }

                String nuevaRegla = "||" + dominio + "^";

                if (!reglas.contains(nuevaRegla)) {
                        reglas.add(nuevaRegla);
                }

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();

                body.put("rules", reglas);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/set_rules",
                                HttpMethod.POST,
                                entity,
                                String.class);

                return response.getBody();
        }

        public String eliminarDominio(String dominio) {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>();

                if (config.getUser_rules() != null) {
                        reglas.addAll(config.getUser_rules());
                }

                reglas.remove("||" + dominio + "^");

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();

                body.put("rules", reglas);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/set_rules",
                                HttpMethod.POST,
                                entity,
                                String.class);

                return response.getBody();
        }

        private String limpiarDominio(String dominio) {

                return dominio
                                .replace("https://", "")
                                .replace("http://", "")
                                .replace("www.", "")
                                .replace("/", "")
                                .trim();
        }

        private String reglaNegra(String dominio) {
                return "||" + limpiarDominio(dominio) + "^";
        }

        private String reglaBlanca(String dominio) {
                return "@@||" + limpiarDominio(dominio) + "^";
        }

        private String guardarReglas(List<String> reglas) {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();
                body.put("rules", reglas);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/set_rules",
                                HttpMethod.POST,
                                entity,
                                String.class);

                return response.getBody();
        }

        public String agregarDominioListaBlanca(String dominio) {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>(
                                config.getUser_rules() != null
                                                ? config.getUser_rules()
                                                : new ArrayList<>());

                String regla = reglaBlanca(dominio);

                if (!reglas.contains(regla)) {
                        reglas.add(regla);
                }

                return guardarReglas(reglas);
        }

        public String eliminarDominioListaBlanca(String dominio) {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>(
                                config.getUser_rules() != null
                                                ? config.getUser_rules()
                                                : new ArrayList<>());

                reglas.remove(reglaBlanca(dominio));

                return guardarReglas(reglas);
        }

        public String vaciarListaNegra() {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>(
                                config.getUser_rules() != null
                                                ? config.getUser_rules()
                                                : new ArrayList<>());

                reglas.removeIf(r -> r.startsWith("||"));

                return guardarReglas(reglas);
        }

        public String vaciarListaBlanca() {

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>(
                                config.getUser_rules() != null
                                                ? config.getUser_rules()
                                                : new ArrayList<>());

                reglas.removeIf(r -> r.startsWith("@@||"));

                return guardarReglas(reglas);
        }

        public Object obtenerClientes() {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Object> response = restTemplate.exchange(
                                "http://172.16.2.82/control/clients",
                                HttpMethod.GET,
                                entity,
                                Object.class);

                return response.getBody();
        }

        public List<AdGuardPoliticaTablet> obtenerPoliticasTablet(Long tabletId) {

                return politicaTabletRepository.findByTabletId(tabletId);

        }

        public List<AdGuardPoliticaTablet> obtenerListaNegraTablet(Long tabletId) {

                return politicaTabletRepository.findByTabletIdAndTipo(tabletId, "NEGRA");

        }

        public List<AdGuardPoliticaTablet> obtenerListaBlancaTablet(Long tabletId) {

                return politicaTabletRepository.findByTabletIdAndTipo(tabletId, "BLANCA");

        }

        public void agregarPoliticaTablet(Long tabletId,
                        String dominio,
                        String tipo) {

                AdGuardPoliticaTablet politica = new AdGuardPoliticaTablet();

                politica.setTabletId(tabletId);
                politica.setDominio(limpiarDominio(dominio));
                politica.setTipo(tipo);

                politicaTabletRepository.save(politica);

                sincronizarPoliticasTablet(tabletId);
        }

        public void eliminarPoliticaTablet(Long tabletId,
                        String dominio,
                        String tipo) {

                List<AdGuardPoliticaTablet> lista = politicaTabletRepository.findByTabletIdAndTipo(tabletId, tipo);

                for (AdGuardPoliticaTablet politica : lista) {

                        if (politica.getDominio().equalsIgnoreCase(limpiarDominio(dominio))) {

                                politicaTabletRepository.delete(politica);

                                break;
                        }
                }

                sincronizarPoliticasTablet(tabletId);
        }

        @SuppressWarnings("unchecked")
        public List<AdGuardClienteMdmDTO> obtenerClientesMdm() {

                List<Tablet> tablets = tabletRepository.findAll();

                Map<String, Object> respuesta = (Map<String, Object>) obtenerClientes();

                List<Map<String, Object>> clientesAdGuard = (List<Map<String, Object>>) respuesta.get("auto_clients");

                AdGuardStatsDTO stats = obtenerStats();

                Set<String> clientesActivos = new HashSet<>();

                if (stats.getTop_clients() != null) {

                        for (Map<String, Integer> cliente : stats.getTop_clients()) {

                                clientesActivos.addAll(cliente.keySet());

                        }

                }

                List<AdGuardClienteMdmDTO> resultado = new ArrayList<>();

                for (Tablet tablet : tablets) {

                        AdGuardClienteMdmDTO dto = new AdGuardClienteMdmDTO();

                        dto.setId(tablet.getId());
                        dto.setActivo(tablet.getActivo());
                        dto.setNombreDispositivo(tablet.getDeviceName());
                        dto.setModelo(tablet.getModel());
                        dto.setIp(tablet.getIpAddress());
                        dto.setTotalPoliticas(
                                        politicaTabletRepository.findByTabletId(tablet.getId()).size());

                        boolean encontrado = clientesActivos.contains(tablet.getIpAddress());
                        String origen = "No Detectado";

                        if (clientesAdGuard != null) {

                                for (Map<String, Object> cliente : clientesAdGuard) {

                                        Object ip = cliente.get("ip");

                                        if (ip != null &&
                                                        ip.toString().equals(tablet.getIpAddress())) {

                                                Object source = cliente.get("source");

                                                if (source != null) {

                                                        origen = source.toString();

                                                }

                                                break;

                                        }

                                }

                        }

                        dto.setUsandoAdGuard(encontrado);
                        dto.setOrigenAdGuard(origen);

                        resultado.add(dto);
                }

                return resultado;
        }

        public Object obtenerClientesDetectados() {

                Map<String, Object> data = (Map<String, Object>) obtenerClientes();

                return data.get("auto_clients");
        }

        public Object obtenerClientePorIp(String ip) {

                List<Map<String, Object>> clientes = (List<Map<String, Object>>) obtenerClientesDetectados();

                return clientes.stream()
                                .filter(c -> ip.equals(c.get("ip")))
                                .findFirst()
                                .orElse(null);
        }

        private HttpHeaders headersAdGuard() {

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);
                headers.setContentType(MediaType.APPLICATION_JSON);

                return headers;
        }

        @SuppressWarnings("unchecked")
        public void sincronizarClienteAdGuard(Long tabletId) {

                Tablet tablet = tabletRepository.findById(tabletId).orElse(null);

                if (tablet == null) {
                        return;
                }

                if (tablet.getIpAddress() == null || tablet.getIpAddress().isBlank()) {
                        return;
                }

                String clienteNombre = "Tablet-" + tablet.getActivo();

                RestTemplate restTemplate = new RestTemplate();

                Map<String, Object> body = new HashMap<>();

                body.put("name", clienteNombre);
                body.put("ids", List.of(tablet.getIpAddress()));

                body.put("use_global_settings", true);
                body.put("filtering_enabled", true);
                body.put("safebrowsing_enabled", false);
                body.put("parental_enabled", false);
                body.put("safesearch_enabled", false);
                body.put("use_global_blocked_services", true);
                body.put("blocked_services", List.of());

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headersAdGuard());

                try {

                        restTemplate.exchange(
                                        ADGUARD_BASE + "/control/clients/add",
                                        HttpMethod.POST,
                                        entity,
                                        String.class);

                } catch (Exception e) {

                        Map<String, Object> updateBody = new HashMap<>();

                        updateBody.put("name", clienteNombre);
                        updateBody.put("data", body);

                        HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateBody, headersAdGuard());

                        restTemplate.exchange(
                                        ADGUARD_BASE + "/control/clients/update",
                                        HttpMethod.POST,
                                        updateEntity,
                                        String.class);
                }
        }

        public void sincronizarPoliticasTablet(Long tabletId) {

                Tablet tablet = tabletRepository.findById(tabletId).orElse(null);

                if (tablet == null) {
                        return;
                }

                sincronizarClienteAdGuard(tabletId);

                String clienteNombre = "Tablet-" + tablet.getActivo();

                AdGuardConfigDTO config = obtenerReglas();

                List<String> reglas = new ArrayList<>();

                if (config.getUser_rules() != null) {
                        reglas.addAll(config.getUser_rules());
                }

                reglas.removeIf(r -> r.contains("$client='" + clienteNombre + "'"));

                List<AdGuardPoliticaTablet> politicas = politicaTabletRepository.findByTabletId(tabletId);

                for (AdGuardPoliticaTablet politica : politicas) {

                        String dominio = limpiarDominio(politica.getDominio());

                        if ("NEGRA".equalsIgnoreCase(politica.getTipo())) {

                                reglas.add(
                                                "||" + dominio + "^$client='" + clienteNombre + "'");

                        }

                        if ("BLANCA".equalsIgnoreCase(politica.getTipo())) {

                                reglas.add(
                                                "@@||" + dominio + "^$client='" + clienteNombre + "'");

                        }
                }

                guardarReglas(reglas);
        }

        public List<Map<String, Object>> obtenerFiltros() {

                AdGuardConfigDTO config = obtenerReglas();

                return config.getFilters();
        }

        public String activarFiltro(Long id) {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/enable/" + id,
                                HttpMethod.POST,
                                entity,
                                String.class);

                return response.getBody();
        }

        public String desactivarFiltro(Long id) {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<String> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/disable/" + id,
                                HttpMethod.POST,
                                entity,
                                String.class);

                return response.getBody();
        }

        public Map<String, Object> obtenerConfigGeneral() {

                AdGuardConfigDTO config = obtenerReglas();

                Map<String, Object> respuesta = new HashMap<>();

                respuesta.put("enabled", config.getEnabled());
                respuesta.put("interval", config.getInterval());

                return respuesta;
        }

        public AdGuardConfigDTO obtenerConfiguracion() {

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();

                String auth = USER + ":" + PASSWORD;

                String encodedAuth = Base64.getEncoder()
                                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

                headers.set("Authorization", "Basic " + encodedAuth);

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<AdGuardConfigDTO> response = restTemplate.exchange(
                                "http://172.16.2.82/control/filtering/status",
                                HttpMethod.GET,
                                entity,
                                AdGuardConfigDTO.class);

                return response.getBody();
        }
}