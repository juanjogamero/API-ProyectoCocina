package proyecto.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import proyecto.dto.ActorView;
import proyecto.dto.ChefView;
import proyecto.dto.PerfilRequest;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Chef;
import proyecto.service.ChefService;

@RestController
@RequestMapping("/chef")
public class ChefController {
    private final ChefService chefs;
    public ChefController(ChefService chefs) { this.chefs = chefs; }

    @GetMapping
    public List<ChefView> findAll() {
        return chefs.findAll().stream().filter(c -> Boolean.TRUE.equals(c.getActivo()))
            .map(ChefView::of).toList();
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<ChefView> allForAdmin() { return chefs.findAll().stream().map(ChefView::of).toList(); }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CHEF')")
    public Chef me(Authentication auth) {
        return chefs.findAll().stream().filter(c -> c.getUsername().equals(auth.getName())).findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Chef no encontrado"));
    }

    @GetMapping("/{id}")
    public ChefView findById(@PathVariable int id) {
        Chef chef = chefs.findById(id);
        if (!Boolean.TRUE.equals(chef.getActivo())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Chef no disponible");
        }
        return ChefView.of(chef);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ActorView> create(@Valid @RequestBody RegistroRequest request) {
        Chef chef = chefs.create(request);
        return ResponseEntity.created(URI.create("/chef/" + chef.getId())).body(ActorView.of(chef));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHEF','ADMINISTRADOR')")
    public ChefView update(@PathVariable int id, @Valid @RequestBody PerfilRequest data, Authentication auth) {
        return ChefView.of(chefs.update(id,data,auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        chefs.delete(id);
        return ResponseEntity.noContent().build();
    }
}
