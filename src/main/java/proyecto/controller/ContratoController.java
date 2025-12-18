package proyecto.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proyecto.entity.Contrato;
import proyecto.service.ContratoService;

@RestController
@RequestMapping("/contrato")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Contrato> contratos = contratoService.findAll();
        if (contratos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay contratos registrados");
        }
        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de contrato inválido");
        }

        Optional<Contrato> oContrato = contratoService.findById(id);

        if (oContrato.isPresent()) {
            return ResponseEntity.ok(oContrato.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato con ID " + id + " no encontrado");
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Contrato contrato) {
        try {
            if (contrato.getFechaContratacion() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha de contratación es obligatoria");
            }
            if (contrato.getFechaTrabajo() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La fecha del trabajo es obligatoria");
            }

            Contrato savedContrato = contratoService.save(contrato);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Contrato creado correctamente con ID: " + savedContrato.getId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el contrato: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Contrato contrato) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de contrato inválido");
            }

            Contrato updatedContrato = contratoService.update(id, contrato);
            if (updatedContrato == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato con ID " + id + " no encontrado");
            }

            return ResponseEntity.ok("Contrato actualizado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el contrato: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de contrato inválido");
            }

            if (!contratoService.findById(id).isPresent()) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Contrato con ID " + id + " no encontrado");
            }

            contratoService.delete(id);
            return ResponseEntity.ok("Contrato eliminado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el contrato: " + e.getMessage());
        }
    }
}