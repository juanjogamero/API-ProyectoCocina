package proyecto.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import proyecto.dto.*;
import proyecto.service.ContratoService;

@RestController
@RequestMapping("/contrato")
@PreAuthorize("isAuthenticated()")
public class ContratoController {
    private final ContratoService contratos;
    public ContratoController(ContratoService contratos) { this.contratos=contratos; }

    @GetMapping
    public List<ContratoView> findAll(Authentication auth) {
        return contratos.findAll(auth.getName());
    }
    @GetMapping("/{id}")
    public ContratoView findById(@PathVariable int id,Authentication auth) {
        return contratos.findById(id,auth.getName());
    }
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ContratoView> save(@Valid @RequestBody ContratoRequest request,Authentication auth) {
        ContratoView result=contratos.create(request,auth.getName());
        return ResponseEntity.created(URI.create("/contrato/"+result.id())).body(result);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContratoView update(@PathVariable int id,@Valid @RequestBody ContratoUpdateRequest request,Authentication auth) {
        return contratos.update(id,request,auth.getName());
    }
    @PutMapping("/{id}/estado")
    public ContratoView estado(@PathVariable int id,@Valid @RequestBody EstadoRequest request,Authentication auth) {
        return contratos.changeEstado(id,request.estado(),auth.getName());
    }
    @PutMapping("/{id}/valoracion")
    @PreAuthorize("hasRole('CLIENTE')")
    public ContratoView valorar(@PathVariable int id,@Valid @RequestBody ValoracionRequest request,Authentication auth) {
        return contratos.rate(id,request.valoracion(),auth.getName());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        contratos.delete(id);
        return ResponseEntity.noContent().build();
    }
}
