package Dto;

public class AdGuardClienteMdmDTO {

    private Long id;
    private String activo;
    private String nombreDispositivo;
    private String modelo;
    private String ip;
    private Boolean usandoAdGuard;
    private String origenAdGuard;
    private Integer totalPoliticas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getUsandoAdGuard() {
        return usandoAdGuard;
    }

    public void setUsandoAdGuard(Boolean usandoAdGuard) {
        this.usandoAdGuard = usandoAdGuard;
    }

    public String getOrigenAdGuard() {
        return origenAdGuard;
    }

    public void setOrigenAdGuard(String origenAdGuard) {
        this.origenAdGuard = origenAdGuard;
    }

    public Integer getTotalPoliticas() {
        return totalPoliticas;
    }

    public void setTotalPoliticas(Integer totalPoliticas) {
        this.totalPoliticas = totalPoliticas;
    }
}