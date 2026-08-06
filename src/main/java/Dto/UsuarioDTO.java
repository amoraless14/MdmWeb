package Dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UsuarioDTO {

    private Long id;

    private String usuario;

    private String password;

    private String nombre;

    private String rol;

    private Boolean activo;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

}