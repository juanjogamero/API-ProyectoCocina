package proyecto.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto.entity.Chef;

@Repository
public interface ChefRepository extends JpaRepository<Chef, Integer> {

}
