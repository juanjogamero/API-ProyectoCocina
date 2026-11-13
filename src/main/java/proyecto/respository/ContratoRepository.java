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
    boolean existsByChefId(int chefId);
    boolean existsByClienteId(int clienteId);
    boolean existsByChefIdAndFechaTrabajoAndEstadoIn(int chefId, LocalDate fechaTrabajo,
            Collection<EstadoContrato> estados);
    @Query("SELECT AVG(c.valoracionCliente) FROM Contrato c WHERE c.chef.id = :chefId AND c.valoracionCliente IS NOT NULL")
    Double puntuacionMedia(@Param("chefId") int chefId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Contrato c WHERE c.id = :id")
    java.util.Optional<Contrato> findByIdForUpdate(@Param("id") int id);
}
