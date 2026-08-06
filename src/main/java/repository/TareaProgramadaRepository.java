package repository;

import Entidad.TareaProgramada;
import Enums.EstadoTarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TareaProgramadaRepository extends JpaRepository<TareaProgramada, Long> {

    List<TareaProgramada> findByEstadoAndActivoTrueAndFechaProgramadaLessThanEqualAndHoraProgramadaLessThanEqual(
            EstadoTarea estado,
            LocalDate fechaProgramada,
            LocalTime horaProgramada
    );

    List<TareaProgramada> findByEstadoAndActivoTrue(
            EstadoTarea estado
    );

}