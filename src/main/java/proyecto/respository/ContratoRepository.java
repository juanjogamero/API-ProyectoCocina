package proyecto.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyecto.entity.Contrato;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

}
