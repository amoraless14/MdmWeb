package Dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private Long id;

    private String usuario;

    private String nombre;

    private String rol;

    private Boolean activo;

}