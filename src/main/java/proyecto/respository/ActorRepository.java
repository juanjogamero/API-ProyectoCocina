package proyecto.respository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor,Integer> {
    Optional<Actor> findByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
}
