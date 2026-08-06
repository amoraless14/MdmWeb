package Entidad;

import Enums.DestinoTarea;
import Enums.EstadoTarea;
import Enums.TipoTarea;
import Enums.TipoProgramacion;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tarea_programada", schema = "monitoreo tablet")
@Data
public class TareaProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarea", nullable = false)
    private TipoTarea tipoTarea;

    @Enumerated(EnumType.STRING)
    @Column(name = "destino_tarea", nullable = false)
    private DestinoTarea destinoTarea;

    @Column(name = "valor_destino", length = 150)
    private String valorDestino;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "hora_programada", nullable = false)
    private LocalTime horaProgramada;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_programacion", nullable = false)
    private TipoProgramacion tipoProgramacion = TipoProgramacion.UNA_VEZ;

    @Column(name = "dias_semana", length = 30)
    private String diasSemana;

    @Column(name = "dia_mes")
    private Integer diaMes;

    @Column(name = "proxima_ejecucion")
    private LocalDateTime proximaEjecucion;

    @Column(columnDefinition = "TEXT")
    private String parametros;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoTarea.PENDIENTE;
        }
    }

}