package repository;

import Entidad.ReglaAppDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaAppDetalleRepository extends JpaRepository<ReglaAppDetalle, Long> {

    List<ReglaAppDetalle> findByReglaId(Long reglaId);

    void deleteByReglaId(Long reglaId);
}