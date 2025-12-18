package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.entity.Admin;
import proyecto.entity.Cliente;
import proyecto.respository.AdminService;
import proyecto.respository.ClienteRepository;


@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	public Optional<Cliente> findById(int id){
		return this.clienteRepository.findById(id);
	}
	
	public List<Cliente> findAll(){
		return this.clienteRepository.findAll();
	}
	
	public Cliente save(Cliente c) {
		return this.clienteRepository.save(c);
	}
	
	public Cliente update(int idCliente, Cliente c) {
		Optional<Cliente> oCliente = findById(idCliente);
		if (oCliente.isPresent()) {
			Cliente cReal = oCliente.get();
			
			cReal.setNombre(c.getNombre());
			cReal.setApellido1(c.getApellido1());
			cReal.setApellido2(c.getApellido2());
			cReal.setCorreo(c.getCorreo());
			cReal.setTelefono(c.getTelefono());
			cReal.setAlergias(c.getAlergias());
			cReal.setCodigoPostal(c.getCodigoPostal());
			
			return save(cReal);
		}
		
		return null;
	}
	
	public void delete(int id) {
		this.clienteRepository.deleteById(id);
	}
}
