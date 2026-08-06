package repository;

import Entidad.AdGuardPoliticaTablet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdGuardPoliticaTabletRepository
        extends JpaRepository<AdGuardPoliticaTablet, Long> {

    List<AdGuardPoliticaTablet> findByTabletId(Long tabletId);

    List<AdGuardPoliticaTablet> findByTabletIdAndTipo(Long tabletId, String tipo);

}