package repository;

import Entidad.ReglaAppDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReglaAppDestinoRepository extends JpaRepository<ReglaAppDestino, Long> {

    List<ReglaAppDestino> findByReglaId(Long reglaId);

    List<ReglaAppDestino> findByTipoDestino(String tipoDestino);

    List<ReglaAppDestino> findByTipoDestinoAndValorDestino(
            String tipoDestino,
            String valorDestino
    );

    void deleteByReglaId(Long reglaId);
}