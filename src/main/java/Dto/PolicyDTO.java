package Dto;

import lombok.Data;

@Data
public class PolicyDTO {

    private String appsBloqueadas;
    private String restricciones;
    private String configReinicio;
    private String urlsPermitidas;

}