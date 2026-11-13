package proyecto.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import proyecto.dto.*;
import proyecto.entity.Cliente;
import proyecto.service.ClienteService;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
    private final ClienteService clientes;
    public ClienteController(ClienteService clientes) { this.clientes=clientes; }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ActorView> findAll() { return clientes.findAll().stream().map(ActorView::of).toList(); }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public Cliente me(Authentication auth) {
        return clientes.findAll().stream().filter(c -> c.getUsername().equals(auth.getName())).findFirst()
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND,"Cliente no encontrado"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Cliente findById(@PathVariable int id,Authentication auth) {
        return clientes.requireSelfOrAdmin(id,auth.getName());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ActorView> create(@Valid @RequestBody RegistroRequest request) {
        Cliente cliente=clientes.create(request);
        return ResponseEntity.created(URI.create("/cliente/"+cliente.getId())).body(ActorView.of(cliente));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Cliente update(@PathVariable int id,@Valid @RequestBody PerfilRequest data,Authentication auth) {
        return clientes.update(id,data,auth.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        clientes.delete(id);
        return ResponseEntity.noContent().build();
    }
}
