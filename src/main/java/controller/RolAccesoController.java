package controller;

import Entidad.RolAcceso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RolAccesoService;
import Dto.RoleLoginRequestDTO;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/roles")
@CrossOrigin("*")
public class RolAccesoController {

    @Autowired
    private RolAccesoService rolAccesoService;

    @GetMapping
    public List<RolAcceso> obtenerTodos() {
        return rolAccesoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolAcceso> obtenerPorId(@PathVariable Long id) {

        RolAcceso rol = rolAccesoService.obtenerPorId(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody RolAcceso rol) {

        if (rolAccesoService.existePassword(rol.getPassword())) {

            return ResponseEntity.badRequest()
                    .body("La contraseña ya existe.");

        }

        return ResponseEntity.ok(
                rolAccesoService.guardar(rol));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
            @RequestBody RolAcceso datos) {

        RolAcceso rol = rolAccesoService.obtenerPorId(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        if (!rol.getPassword().equals(datos.getPassword())
                && rolAccesoService.existePassword(datos.getPassword())) {

            return ResponseEntity.badRequest()
                    .body("La contraseña ya existe.");

        }

        rol.setPassword(datos.getPassword());
        rol.setRol(datos.getRol());
        rol.setActivo(datos.getActivo());

        return ResponseEntity.ok(
                rolAccesoService.guardar(rol));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        RolAcceso rol = rolAccesoService.obtenerPorId(id);

        if (rol == null) {
            return ResponseEntity.notFound().build();
        }

        rolAccesoService.eliminar(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RoleLoginRequestDTO request) {

        RolAcceso rol = rolAccesoService.validarPassword(request.getPassword());

        if (rol == null) {

            return ResponseEntity.ok().body(Map.of(
                    "success", false));

        }

        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "role", rol.getRol()));

    }
}