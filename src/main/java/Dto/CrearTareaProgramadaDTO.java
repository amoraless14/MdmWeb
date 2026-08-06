package Dto;

import Enums.DestinoTarea;
import Enums.TipoTarea;
import Enums.TipoProgramacion;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CrearTareaProgramadaDTO {

    private String nombre;

    private String descripcion;

    private TipoTarea tipoTarea;

    private DestinoTarea destinoTarea;

    private String valorDestino;

    private List<Long> dispositivos;

    private LocalDate fechaProgramada;

    private LocalTime horaProgramada;

    private String parametros;

    private TipoProgramacion tipoProgramacion = TipoProgramacion.UNA_VEZ;

    private String diasSemana;

    private Integer diaMes;

}