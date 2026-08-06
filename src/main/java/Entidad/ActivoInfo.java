package Entidad;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "activo_info", schema = "public")
@Data
public class ActivoInfo {

    @Id
    @Column(name = "activo")
    private String activo;

    @Column(name = "codigo_emp")
    private String codigoEmp;

    @Column(name = "empleado_asig")
    private String empleadoAsig;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "area")
    private String area;

    @Column(name = "planta")
    private String planta;

}