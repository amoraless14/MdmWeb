package repository;

import Entidad.LayoutEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LayoutEmpresaRepository extends JpaRepository<LayoutEmpresa, Long> {
}