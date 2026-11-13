package proyecto.respository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import proyecto.entity.Chef;

public interface ChefRepository extends JpaRepository<Chef,Integer> {
    Optional<Chef> findByUsername(String username);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Chef c WHERE c.id = :id")
    Optional<Chef> findByIdForUpdate(@Param("id") int id);
}
