package proyecto.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto.entity.Admin;

@Repository
public interface AdminService extends JpaRepository<Admin, Integer> {

}
