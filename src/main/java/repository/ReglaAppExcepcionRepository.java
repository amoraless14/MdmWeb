package repository;

import Entidad.ReglaAppExcepcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReglaAppExcepcionRepository
        extends JpaRepository<ReglaAppExcepcion, Long> {

    List<ReglaAppExcepcion> findByActivo(String activo);

    Optional<ReglaAppExcepcion> findByActivoAndPackageName(
            String activo,
            String packageName
    );

    List<ReglaAppExcepcion> findByActivoAndAccion(
            String activo,
            String accion
    );

    void deleteByActivoAndPackageName(
            String activo,
            String packageName
    );
}