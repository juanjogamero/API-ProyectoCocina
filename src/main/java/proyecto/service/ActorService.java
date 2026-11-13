package proyecto.service;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.RegistroRequest;
import proyecto.entity.*;
import proyecto.respository.ActorRepository;

@Service
public class ActorService implements UserDetailsService {
    private final ActorRepository actors;
    private final PasswordEncoder encoder;

    public ActorService(ActorRepository actors, PasswordEncoder encoder) {
        this.actors = actors;
        this.encoder = encoder;
    }

    public Optional<Actor> findByUsername(String username) { return actors.findByUsername(username); }

    public Actor requireActor(String username) {
        return actors.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Cuenta no encontrada"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Actor actor = actors.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Cuenta no encontrada"));
        return User.withUsername(actor.getUsername()).password(actor.getPassword())
            .roles(actor.getRol().name()).build();
    }

    @Transactional
    public Actor register(RegistroRequest request) {
        if (request.rol() != Rol.CHEF && request.rol() != Rol.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Solo se permite registrar chefs o clientes");
        }
        return registerAs(request, request.rol());
    }

    @Transactional
    public Actor registerAs(RegistroRequest request, Rol rol) {
        String username = request.username().strip().toLowerCase();
        if (actors.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El nombre de usuario ya existe");
        }
        Actor actor;
        if (rol == Rol.CHEF) {
            if (request.precioServicio() == null || request.precioServicio().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Indica un precio por servicio valido");
            }
            Chef chef = new Chef();
            chef.setPrecioServicio(request.precioServicio());
            chef.setActivo(true);
            chef.setValoracion(0);
            actor = chef;
        } else if (rol == Rol.CLIENTE) {
            Cliente cliente = new Cliente();
            cliente.setCodigoPostal(request.codigoPostal());
            cliente.setAlergias(request.alergias());
            actor = cliente;
        } else if (rol == Rol.ADMINISTRADOR) {
            actor = new Admin();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rol no admitido");
        }
        actor.setUsername(username);
        actor.setPassword(encoder.encode(request.password()));
        actor.setRol(rol);
        actor.setNombre(request.nombre());
        actor.setApellido1(request.apellido1());
        actor.setApellido2(request.apellido2());
        actor.setCorreo(request.correo());
        actor.setTelefono(request.telefono());
        return actors.save(actor);
    }
}
