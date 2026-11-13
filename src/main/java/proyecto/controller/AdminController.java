package proyecto.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import proyecto.dto.ActorView;
import proyecto.dto.PerfilRequest;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Admin;
import proyecto.service.AdminService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {
    private final AdminService admins;
    public AdminController(AdminService admins) { this.admins=admins; }

    @GetMapping
    public List<ActorView> findAll() { return admins.findAll().stream().map(ActorView::of).toList(); }

    @GetMapping("/{id}")
    public ActorView findById(@PathVariable int id) { return ActorView.of(admins.findById(id)); }

    @PostMapping
    public ResponseEntity<ActorView> create(@Valid @RequestBody RegistroRequest request) {
        Admin admin=admins.create(request);
        return ResponseEntity.created(URI.create("/admin/"+admin.getId())).body(ActorView.of(admin));
    }

    @PutMapping("/{id}")
    public ActorView update(@PathVariable int id,@Valid @RequestBody PerfilRequest data) {
        return ActorView.of(admins.update(id,data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        admins.delete(id);
        return ResponseEntity.noContent().build();
    }
}
