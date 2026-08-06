package controller;

import Entidad.LayoutEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import service.LayoutEmpresaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/layouts")
@CrossOrigin("*")
public class LayoutEmpresaController {

    @Autowired
    private LayoutEmpresaService layoutEmpresaService;

    @GetMapping
    public List<LayoutEmpresa> obtenerTodos() {
        return layoutEmpresaService.obtenerTodos();
    }

    @PostMapping("/{id}/archivo")
    public ResponseEntity<?> subirArchivo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {

            LayoutEmpresa layout = layoutEmpresaService.obtenerPorId(id);

            if (layout == null) {
                return ResponseEntity.notFound().build();
            }

            layout.setArchivo(file.getBytes());
            layout.setTipoArchivo(file.getContentType());

            layoutEmpresaService.guardar(layout);

            return ResponseEntity.ok().build();

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> obtenerArchivo(@PathVariable Long id) {

        LayoutEmpresa layout = layoutEmpresaService.obtenerPorId(id);

        if (layout == null || layout.getArchivo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        layout.getTipoArchivo())
                .body(layout.getArchivo());
    }

    @GetMapping("/{id}")
    public LayoutEmpresa obtenerPorId(@PathVariable Long id) {
        return layoutEmpresaService.obtenerPorId(id);
    }

    @PostMapping
    public LayoutEmpresa guardar(@RequestBody LayoutEmpresa layout) {
        return layoutEmpresaService.guardar(layout);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        layoutEmpresaService.eliminar(id);
    }

    @GetMapping("/activo")
    public LayoutEmpresa obtenerLayoutActivo() {

        return layoutEmpresaService.obtenerTodos()
                .stream()
                .findFirst()
                .orElse(null);
    }
}