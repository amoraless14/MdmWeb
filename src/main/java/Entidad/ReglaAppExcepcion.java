package Entidad;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "reglas_apps_excepciones",
    schema = "monitoreo tablet",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_regla_app_excepcion_activo",
            columnNames = {"activo", "package_name"}
        )
    }
)
@Data
public class ReglaAppExcepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "activo", nullable = false, length = 255)
    private String activo;

    @Column(name = "package_name", nullable = false, length = 255)
    private String packageName;

    @Column(name = "app_name", length = 255)
    private String appName;

    @Column(nullable = false, length = 10)
    private String accion;
}