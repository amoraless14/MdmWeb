package Entidad;

import Enums.EstadoTareaDispositivo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarea_programada_dispositivo", schema = "monitoreo tablet")
@Data
public class TareaProgramadaDispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_programada_id", nullable = false)
    private TareaProgramada tareaProgramada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Tablet tablet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTareaDispositivo estado;

    @Column(nullable = false)
    private Boolean confirmado = false;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

}