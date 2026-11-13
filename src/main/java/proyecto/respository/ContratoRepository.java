package proyecto.respository;

import java.util.Collection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import proyecto.entity.Contrato;
import proyecto.entity.EstadoContrato;

public interface ContratoRepository extends JpaRepository<Contrato,Integer> {
    List<Contrato> findByClienteUsernameOrderByFechaTrabajoDesc(String username);
    List<Contrato> findByChefUsernameOrderByFechaTrabajoDesc(String username);
    boolean existsByChefIdAndFechaTrabajoAndEstadoIn(int chefId, LocalDate fechaTrabajo,
            Collection<EstadoContrato> estados);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Contrato c WHERE c.id = :id")
    java.util.Optional<Contrato> findByIdForUpdate(@Param("id") int id);
}
