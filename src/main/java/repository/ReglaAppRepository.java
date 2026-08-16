package repository;

import Entidad.ReglaApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaAppRepository extends JpaRepository<ReglaApp, Long> {

    List<ReglaApp> findByActivaTrue();
}