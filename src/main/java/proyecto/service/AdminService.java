package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.entity.Admin;


@Service
public class AdminService {

	@Autowired
	private AdminService adminRepository;
	
	public Optional<Admin> findById(int id){
		return this.adminRepository.findById(id);
	}
	
	public List<Admin> findAll(){
		return this.adminRepository.findAll();
	}
	
	public Admin save(Admin admin) {
		return this.adminRepository.save(admin);
	}
	
	public Admin update(int idAdmin, Admin admin) {
		Optional<Admin> oAdmin = findById(idAdmin);
		if (oAdmin.isPresent()) {
			Admin a = oAdmin.get();
			
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
		this.adminRepository.delete(id);	
	}


}
