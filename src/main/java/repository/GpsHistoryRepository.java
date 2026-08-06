package repository;

import Entidad.GpsHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GpsHistoryRepository extends JpaRepository<GpsHistory, Long> {

    List<GpsHistory> findTop500ByTabletIdOrderByGpsTimestampDesc(Long tabletId);

}