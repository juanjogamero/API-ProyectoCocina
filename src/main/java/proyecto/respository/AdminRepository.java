package proyecto.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.entity.Admin;
public interface AdminRepository extends JpaRepository<Admin,Integer> {}
