package repository;

import Entidad.TareaProgramadaDispositivo;
import Enums.EstadoTareaDispositivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaProgramadaDispositivoRepository extends JpaRepository<TareaProgramadaDispositivo, Long> {

    List<TareaProgramadaDispositivo> findByTareaProgramadaId(Long tareaProgramadaId);

    void deleteByTareaProgramadaId(Long tareaProgramadaId);

    void deleteByTabletId(Long tabletId);

    Page<TareaProgramadaDispositivo> findByTareaProgramadaId(
            Long tareaProgramadaId,
            Pageable pageable);

    Page<TareaProgramadaDispositivo> findByTareaProgramadaIdAndTabletActivoContainingIgnoreCase(
            Long tareaProgramadaId,
            String activo,
            Pageable pageable);

    Page<TareaProgramadaDispositivo> findByTareaProgramadaIdAndEstado(
            Long tareaProgramadaId,
            EstadoTareaDispositivo estado,
            Pageable pageable);

    Page<TareaProgramadaDispositivo> findByTareaProgramadaIdAndEstadoAndTabletActivoContainingIgnoreCase(
            Long tareaProgramadaId,
            EstadoTareaDispositivo estado,
            String activo,
            Pageable pageable);

}