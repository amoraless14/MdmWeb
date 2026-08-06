package controller;

import Dto.AdGuardStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AdGuardService;
import Dto.AdGuardConfigDTO;

import java.util.Map;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/adguard")
public class AdGuardController {

        @Autowired
        private AdGuardService adGuardService;

        @GetMapping("/status")
        public ResponseEntity<AdGuardStatusDTO> status() {

                return ResponseEntity.ok(
                                adGuardService.obtenerEstado());
        }

        @GetMapping("/stats")
        public ResponseEntity<?> stats() {

                return ResponseEntity.ok(
                                adGuardService.obtenerStats());
        }

        @PostMapping("/rules")
        public ResponseEntity<?> agregarDominio(
                        @RequestBody Map<String, String> body) {

                return ResponseEntity.ok(
                                adGuardService.agregarDominio(
                                                body.get("dominio")));
        }

        @GetMapping("/rules")
        public ResponseEntity<?> rules() {

                return ResponseEntity.ok(
                                adGuardService.obtenerReglas());
        }

        @DeleteMapping("/rules/{dominio}")
        public ResponseEntity<?> eliminarDominio(
                        @PathVariable String dominio) {

                return ResponseEntity.ok(
                                adGuardService.eliminarDominio(dominio));
        }

        @PostMapping("/whitelist")
        public ResponseEntity<?> agregarWhitelist(@RequestBody Map<String, String> body) {
                return ResponseEntity.ok(
                                adGuardService.agregarDominioListaBlanca(body.get("dominio")));
        }

        @DeleteMapping("/whitelist/{dominio}")
        public ResponseEntity<?> eliminarWhitelist(@PathVariable String dominio) {
                return ResponseEntity.ok(
                                adGuardService.eliminarDominioListaBlanca(dominio));
        }

        @DeleteMapping("/rules/blacklist/all")
        public ResponseEntity<?> vaciarBlacklist() {
                return ResponseEntity.ok(
                                adGuardService.vaciarListaNegra());
        }

        @DeleteMapping("/rules/whitelist/all")
        public ResponseEntity<?> vaciarWhitelist() {
                return ResponseEntity.ok(
                                adGuardService.vaciarListaBlanca());
        }

        @PostMapping("/sync")
        public ResponseEntity<?> sincronizarAdGuard() {
                return ResponseEntity.ok(
                                adGuardService.obtenerReglas());
        }

        @GetMapping("/clients")
        public ResponseEntity<?> clients() {

                return ResponseEntity.ok(
                                adGuardService.obtenerClientes());
        }

        @GetMapping("/clients/mdm")
        public ResponseEntity<?> clientsMdm() {

                return ResponseEntity.ok(
                                adGuardService.obtenerClientesMdm());
        }

        @GetMapping("/clients/detected")
        public ResponseEntity<?> detectedClients() {

                return ResponseEntity.ok(
                                adGuardService.obtenerClientesDetectados());
        }

        @GetMapping("/clients/ip/{ip}")
        public ResponseEntity<?> clientByIp(
                        @PathVariable String ip) {

                return ResponseEntity.ok(
                                adGuardService.obtenerClientePorIp(ip));
        }

        @GetMapping("/tablets/{tabletId}/politicas")
        public ResponseEntity<?> obtenerPoliticasTablet(
                        @PathVariable Long tabletId) {

                return ResponseEntity.ok(
                                adGuardService.obtenerPoliticasTablet(tabletId));
        }

        @GetMapping("/tablets/{tabletId}/blacklist")
        public ResponseEntity<?> obtenerBlacklistTablet(
                        @PathVariable Long tabletId) {

                return ResponseEntity.ok(
                                adGuardService.obtenerListaNegraTablet(tabletId));
        }

        @GetMapping("/tablets/{tabletId}/whitelist")
        public ResponseEntity<?> obtenerWhitelistTablet(
                        @PathVariable Long tabletId) {

                return ResponseEntity.ok(
                                adGuardService.obtenerListaBlancaTablet(tabletId));
        }

        @PostMapping("/tablets/{tabletId}/blacklist")
        public ResponseEntity<?> agregarBlacklistTablet(
                        @PathVariable Long tabletId,
                        @RequestBody Map<String, String> body) {

                adGuardService.agregarPoliticaTablet(
                                tabletId,
                                body.get("dominio"),
                                "NEGRA");

                return ResponseEntity.ok("Dominio agregado a lista negra de la tablet");
        }

        @PostMapping("/tablets/{tabletId}/whitelist")
        public ResponseEntity<?> agregarWhitelistTablet(
                        @PathVariable Long tabletId,
                        @RequestBody Map<String, String> body) {

                adGuardService.agregarPoliticaTablet(
                                tabletId,
                                body.get("dominio"),
                                "BLANCA");

                return ResponseEntity.ok("Dominio agregado a lista blanca de la tablet");
        }

        @DeleteMapping("/tablets/{tabletId}/blacklist/{dominio}")
        public ResponseEntity<?> eliminarBlacklistTablet(
                        @PathVariable Long tabletId,
                        @PathVariable String dominio) {

                adGuardService.eliminarPoliticaTablet(
                                tabletId,
                                dominio,
                                "NEGRA");

                return ResponseEntity.ok("Dominio eliminado de lista negra de la tablet");
        }

        @DeleteMapping("/tablets/{tabletId}/whitelist/{dominio}")
        public ResponseEntity<?> eliminarWhitelistTablet(
                        @PathVariable Long tabletId,
                        @PathVariable String dominio) {

                adGuardService.eliminarPoliticaTablet(
                                tabletId,
                                dominio,
                                "BLANCA");

                return ResponseEntity.ok("Dominio eliminado de lista blanca de la tablet");
        }

        @GetMapping("/filters")
        public ResponseEntity<?> filters() {

                return ResponseEntity.ok(
                                adGuardService.obtenerFiltros());
        }

        @PostMapping("/filters/{id}/enable")
        public ResponseEntity<?> enableFilter(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                adGuardService.activarFiltro(id));
        }

        @PostMapping("/filters/{id}/disable")
        public ResponseEntity<?> disableFilter(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                adGuardService.desactivarFiltro(id));
        }

        @GetMapping("/config")
        public ResponseEntity<AdGuardConfigDTO> config() {

                return ResponseEntity.ok(
                                adGuardService.obtenerConfiguracion());
        }

}