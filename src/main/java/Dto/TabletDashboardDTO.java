package Dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TabletDashboardDTO {

    private Long id;

    @JsonProperty("activo")
    private String activo;

    @JsonProperty("android_id")
    private String androidId;

    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("model")
    private String model;

    @JsonProperty("codigo_emp")
    private String codigoEmp;

    @JsonProperty("nombre_emp")
    private String nombreEmp;

    @JsonProperty("battery_level")
    private Integer batteryLevel;

    @JsonProperty("temperatura")
    private String temperatura;

    @JsonProperty("estado_cargador")
    private String estadoCargador;

    @JsonProperty("estado_wifi")
    private String estadoWifi;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("ram_usage")
    private String ramUsage;

    @JsonProperty("storage_usage")
    private String storageUsage;

    @JsonProperty("uptime")
    private String uptime;

    @JsonProperty("last_connection")
    private LocalDateTime lastConnection;

    @JsonProperty("os_version")
    private String osVersion;
}