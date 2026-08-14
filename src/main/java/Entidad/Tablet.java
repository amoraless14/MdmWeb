package Entidad;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "dispositivos", schema = "monitoreo tablet")
@Data
@DynamicUpdate
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tablet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("activo")
    private String activo;

    @Column(name = "device_name")
    @JsonProperty("device_name")
    private String deviceName;

    @JsonProperty("model")
    private String model;

    @Column(name = "codigo_emp")
    @JsonProperty("codigo_emp")
    private String codigoEmp;

    @Column(name = "nombre_emp")
    @JsonProperty("nombre_emp")
    private String nombreEmp;

    @Column(name = "battery_level")
    @JsonProperty("battery_level")
    private Integer batteryLevel;

    @JsonProperty("temperatura")
    private String temperatura;

    @Column(name = "estado_cargador")
    @JsonProperty("estado_cargador")
    private String estadoCargador;

    @Column(name = "estado_wifi")
    @JsonProperty("estado_wifi")
    private String estadoWifi;

    @Column(name = "estado_red")
    @JsonProperty("estado") // <--- ESTO ARREGLA EL "DESCONOCIDO"
    private String estado;

    @Column(name = "ip_address")
    @JsonProperty("ip_address")
    private String ipAddress;

    @Column(name = "ram_usage")
    @JsonProperty("ram_usage")
    private String ramUsage;

    @Column(name = "storage_usage")
    @JsonProperty("storage_usage")
    private String storageUsage;

    @JsonProperty("uptime")
    private String uptime;

    // Agrega la anotación @JsonProperty para que coincida con lo que espera el HTML
    @Column(name = "last_connection")
    @JsonProperty("last_connection")
    private LocalDateTime lastConnection;

    @Column(name = "pending_command")
    @JsonProperty("pending_command")
    private String pendingCommand;

    @Column(name = "android_id")
    @JsonProperty("android_id")
    private String androidId;

    @Column(name = "os_version")
    @JsonProperty("os_version")
    private String osVersion;

    // --- CAMPOS PARA GESTIÓN MDM ---

    @Column(name = "apps_reportadas", columnDefinition = "TEXT")
    @JsonProperty("apps_reportadas")
    private String appsReportadas;

    @Transient
    @JsonProperty("accion_masiva_apps")
    private String accionMasivaApps;

    @Column(name = "apps_bloqueadas", columnDefinition = "TEXT")
    @JsonProperty("apps_bloqueadas")
    private String appsBloqueadas;

    @Column(name = "config_reinicio", length = 500) // Ej: "0,2,4|03:00"
    @JsonProperty("config_reinicio")
    private String configReinicio;

    @Column(name = "restricciones", length = 1000) // Ej: "camera:false,usb:true"
    @JsonProperty("restricciones")
    private String restricciones;

    @Column(name = "urls_permitidas", length = 2000)
    @JsonProperty("urls_permitidas")
    private String urlsPermitidas;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "gps_timestamp")
    private LocalDateTime gpsTimestamp;

    @Column(name = "gps_accuracy")
    private Double gpsAccuracy;

    @Column(name = "gps_source")
    private String gpsSource;

    @Column(name = "categoria")
    @JsonProperty("categoria")
    private String categoria;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastConnection = LocalDateTime.now();
    }
}