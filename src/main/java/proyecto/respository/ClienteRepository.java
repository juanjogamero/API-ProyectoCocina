package proyecto.respository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.entity.Cliente;
public interface ClienteRepository extends JpaRepository<Cliente,Integer> {
    Optional<Cliente> findByUsername(String username);
}
