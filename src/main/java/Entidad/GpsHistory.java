package Entidad;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "gps_history", schema = "monitoreo tablet")
@Data
public class GpsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tablet_id")
    private Long tabletId;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "gps_timestamp")
    private LocalDateTime gpsTimestamp;
}