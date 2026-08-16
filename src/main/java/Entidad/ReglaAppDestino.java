package Entidad;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reglas_apps_destinos", schema = "monitoreo tablet")
@Data
public class ReglaAppDestino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "regla_id", nullable = false)
    private Long reglaId;

    @Column(name = "tipo_destino", nullable = false, length = 20)
    private String tipoDestino;

    @Column(name = "valor_destino", length = 255)
    private String valorDestino;
}