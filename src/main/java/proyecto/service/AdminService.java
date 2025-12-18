package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.entity.Adminastrador;
import proyecto.respository.AdminastradorRepository;


@Service
public class AdminService {

	@Autowired
	private AdminastradorRepository adminRepository;
	
	public Optional<Adminastrador> findById(int id){
		return this.adminRepository.findById(id);
	}
	
	public List<Adminastrador> findAll(){
		return this.adminRepository.findAll();
	}
	
	public Adminastrador save(Adminastrador admin) {
		return this.adminRepository.save(admin);
	}
	
	public Adminastrador update(int idAdmin, Adminastrador admin) {
		Optional<Adminastrador> oAdmin = findById(idAdmin);
		if (oAdmin.isPresent()) {
			Adminastrador a = oAdmin.get();
			
			a.setNombre(admin.getNombre());
			a.setApellido1(admin.getApellido1());
			a.setApellido2(admin.getApellido2());
			a.setCorreo(admin.getCorreo());
			a.setTelefono(admin.getTelefono());
			
			return save(a);
		}
		
		return null;
	}
	
	public void delete(int id) {
		this.adminRepository.deleteById(id);
	}
}
