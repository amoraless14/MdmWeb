package repository;

import Entidad.GpsTracking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GpsTrackingRepository extends JpaRepository<GpsTracking, Long> {

   Optional<GpsTracking> findByTabletId(Long tabletId);

}