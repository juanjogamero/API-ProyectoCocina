package proyecto.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto.entity.Adminastrador;

@Repository
public interface AdminastradorRepository extends JpaRepository<Adminastrador, Integer> {

}
