package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.entity.Admin;
import proyecto.entity.Chef;
import proyecto.entity.Cliente;
import proyecto.respository.AdminService;
import proyecto.respository.ChefRepository;
import proyecto.respository.ClienteRepository;


@Service
public class ChefService {

	@Autowired
	private ChefRepository chefRepository;
	
	public Optional<Chef> findById(int id){
		return this.chefRepository.findById(id);
	}
	
	public List<Chef> findAll(){
		return this.chefRepository.findAll();
	}
	
	public Chef save(Chef c) {
		return this.chefRepository.save(c);
	}
	
	public Chef update(int idChef, Chef c) {
		Optional<Chef> oChef = findById(idChef);
		if (oChef.isPresent()) {
			Chef cReal = oChef.get();
			
			cReal.setNombre(c.getNombre());
			cReal.setApellido1(c.getApellido1());
			cReal.setApellido2(c.getApellido2());
			cReal.setCorreo(c.getCorreo());
			cReal.setTelefono(c.getTelefono());
			cReal.setPrecioServicio(c.getPrecioServicio());
			cReal.setValoracion(c.getValoracion());
			cReal.setActivo(c.getActivo());
			
			return save(cReal);
		}
		
		return null;
	}
	
	public void delete(int id) {
		this.chefRepository.deleteById(id);
	}
}
