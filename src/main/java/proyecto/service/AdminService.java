package proyecto.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.PerfilRequest;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Admin;
import proyecto.entity.Rol;
import proyecto.respository.AdminRepository;

@Service
public class AdminService {
    private final AdminRepository repo;
    private final ActorService actors;
    public AdminService(AdminRepository repo, ActorService actors) { this.repo = repo; this.actors = actors; }

    public List<Admin> findAll() { return repo.findAll(); }

    public Admin findById(int id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Administrador no encontrado"));
    }

    public Admin create(RegistroRequest request) {
        return (Admin) actors.registerAs(request, Rol.ADMINISTRADOR);
    }

    @Transactional
    public Admin update(int id, PerfilRequest data) {
        Admin admin = findById(id);
        if (data.nombre() != null) admin.setNombre(data.nombre());
        if (data.apellido1() != null) admin.setApellido1(data.apellido1());
        if (data.apellido2() != null) admin.setApellido2(data.apellido2());
        if (data.correo() != null) admin.setCorreo(data.correo());
        if (data.telefono() != null) admin.setTelefono(data.telefono());
        return repo.save(admin);
    }

    public void delete(int id) { repo.delete(findById(id)); }
}
