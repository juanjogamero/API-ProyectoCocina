package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.entity.Chef;
import proyecto.entity.Contrato;
import proyecto.respository.ChefRepository;
import proyecto.respository.ContratoRepository;


@Service
public class ContratoService {
	@Autowired
	private ContratoRepository contratoRepository;
	
	public Optional<Contrato> findById(int id){
		return this.contratoRepository.findById(id);
	}
	
	public List<Contrato> findAll(){
		return this.contratoRepository.findAll();
	}
	
	public Contrato save(Contrato c) {
		return this.contratoRepository.save(c);
	}
	
	public Contrato update(int idContrato, Contrato c) {
		Optional<Contrato> oContrato = findById(idContrato);
		if (oContrato.isPresent()) {
			Contrato cReal = oContrato.get();
			
			cReal.setFechaContratacion(c.getFechaContratacion());
			cReal.setComentario(c.getComentario());
			cReal.setValoracionCliente(c.getValoracionCliente());
			
			
			return save(cReal);
		}
		
		return null;
	}
	
	public void delete(int id) {
		this.contratoRepository.deleteById(id);
	}
}
