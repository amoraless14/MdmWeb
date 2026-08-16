package Entidad;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "reglas_apps_detalle",
    schema = "monitoreo tablet",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_regla_package",
            columnNames = {"regla_id", "package_name"}
        )
    }
)
@Data
public class ReglaAppDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "regla_id", nullable = false)
    private Long reglaId;

    @Column(name = "package_name", nullable = false, length = 255)
    private String packageName;

    @Column(name = "app_name", length = 255)
    private String appName;
}