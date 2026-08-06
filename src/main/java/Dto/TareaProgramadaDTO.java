package Dto;

import Enums.DestinoTarea;
import Enums.EstadoTarea;
import Enums.TipoTarea;
import Enums.TipoProgramacion;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TareaProgramadaDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    private TipoTarea tipoTarea;

    private DestinoTarea destinoTarea;

    private String valorDestino;

    private LocalDate fechaProgramada;

    private LocalTime horaProgramada;

    private String parametros;

    private EstadoTarea estado;

    private Boolean activo;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaEjecucion;

    private Long totalDispositivos;
    private Long completados;
    private Long pendientes;
    private Long errores;

    private TipoProgramacion tipoProgramacion;

    private Integer intervalo;

    private String diasSemana;

    private Integer diaMes;

    private LocalDateTime proximaEjecucion;

}