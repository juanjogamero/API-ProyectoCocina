package proyecto.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.PerfilRequest;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Chef;
import proyecto.entity.Rol;
import proyecto.respository.ChefRepository;
import proyecto.respository.ContratoRepository;

@Service
public class ChefService {
    private final ChefRepository repo;
    private final ContratoRepository contratos;
    private final ActorService actors;
    public ChefService(ChefRepository repo, ContratoRepository contratos, ActorService actors) {
        this.repo = repo;
        this.contratos = contratos;
        this.actors = actors;
    }
    public Chef findById(int id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Chef no encontrado"));
    }
    public List<Chef> findAll() { return repo.findAll(); }
    public Chef create(RegistroRequest request) { return (Chef) actors.registerAs(request,Rol.CHEF); }

    @Transactional
    public Chef update(int id, PerfilRequest data, String username) {
        Chef chef = findById(id);
        if (!chef.getUsername().equals(username) &&
            actors.requireActor(username).getRol() != Rol.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"No puedes modificar otro chef");
        }
        if (data.nombre() != null) chef.setNombre(data.nombre());
        if (data.apellido1() != null) chef.setApellido1(data.apellido1());
        if (data.apellido2() != null) chef.setApellido2(data.apellido2());
        if (data.correo() != null) chef.setCorreo(data.correo());
        if (data.telefono() != null) chef.setTelefono(data.telefono());
        if (data.precioServicio() != null) chef.setPrecioServicio(data.precioServicio());
        if (data.activo() != null) chef.setActivo(data.activo());
        return repo.save(chef);
    }

    @Transactional
    public void delete(int id) {
        Chef chef = findById(id);
        if (contratos.existsByChefId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El chef tiene un historial de contrataciones");
        }
        repo.delete(chef);
    }
}
