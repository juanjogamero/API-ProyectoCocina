package proyecto.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.PerfilRequest;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Cliente;
import proyecto.entity.Rol;
import proyecto.respository.ClienteRepository;
import proyecto.respository.ContratoRepository;

@Service
public class ClienteService {
    private final ClienteRepository repo;
    private final ContratoRepository contratos;
    private final ActorService actors;
    public ClienteService(ClienteRepository repo,ContratoRepository contratos,ActorService actors) {
        this.repo=repo; this.contratos=contratos; this.actors=actors;
    }
    public List<Cliente> findAll() { return repo.findAll(); }
    public Cliente findById(int id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Cliente no encontrado"));
    }
    public Cliente requireSelfOrAdmin(int id,String username) {
        Cliente cliente = findById(id);
        if (!cliente.getUsername().equals(username) &&
            actors.requireActor(username).getRol() != Rol.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"No puedes consultar otro cliente");
        }
        return cliente;
    }
    public Cliente create(RegistroRequest request) { return (Cliente) actors.registerAs(request,Rol.CLIENTE); }

    @Transactional
    public Cliente update(int id, PerfilRequest data,String username) {
        Cliente cliente=requireSelfOrAdmin(id,username);
        if (data.nombre()!=null) cliente.setNombre(data.nombre());
        if (data.apellido1()!=null) cliente.setApellido1(data.apellido1());
        if (data.apellido2()!=null) cliente.setApellido2(data.apellido2());
        if (data.correo()!=null) cliente.setCorreo(data.correo());
        if (data.telefono()!=null) cliente.setTelefono(data.telefono());
        if (data.alergias()!=null) cliente.setAlergias(data.alergias());
        if (data.codigoPostal()!=null) cliente.setCodigoPostal(data.codigoPostal());
        return repo.save(cliente);
    }

    @Transactional
    public void delete(int id) {
        Cliente cliente=findById(id);
        if (contratos.existsByClienteId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El cliente tiene un historial de contrataciones");
        }
        repo.delete(cliente);
    }
}
