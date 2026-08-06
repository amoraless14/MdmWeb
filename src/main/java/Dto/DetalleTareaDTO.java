package Dto;

import Enums.EstadoTareaDispositivo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DetalleTareaDTO {

    private Long totalDispositivos;

    private Long completados;

    private Long pendientes;

    private Long errores;

    private List<Dispositivo> dispositivos;

    @Data
    public static class Dispositivo {

        private String activo;

        private String equipo;

        private EstadoTareaDispositivo estado;

        private Boolean confirmado;

        private LocalDateTime fechaEjecucion;

    }

}