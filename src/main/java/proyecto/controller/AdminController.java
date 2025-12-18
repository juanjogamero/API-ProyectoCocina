package proyecto.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import proyecto.entity.Adminastrador;
import proyecto.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Adminastrador> admins = adminService.findAll();
        if (admins.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay administradores registrados en el sistema");
        }
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de administrador inválido");
        }
        
        Optional<Adminastrador> oAdmin = adminService.findById(id);
        
        if (oAdmin.isPresent()) {
            return ResponseEntity.ok(oAdmin.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador con ID " + id + " no encontrado");
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Adminastrador admin) {
        try {
            if (admin.getNombre() == null || admin.getNombre().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre del administrador es obligatorio");
            }
            if (admin.getApellido1() == null || admin.getApellido1().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El apellido del administrador es obligatorio");
            }
            if (admin.getCorreo() == null || admin.getCorreo().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo del administrador es obligatorio");
            }
            
            Adminastrador savedAdmin = adminService.save(admin);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Administrador creado correctamente con ID: " + savedAdmin.getId());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el administrador: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Adminastrador admin) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de administrador inválido");
            }
            
            Adminastrador updatedAdmin = adminService.update(id, admin);
            if (updatedAdmin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Administrador con ID " + id + " no encontrado");
            }
            
            return ResponseEntity.ok("Administrador actualizado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el administrador: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de administrador inválido");

            }
            adminService.delete(id);
            return ResponseEntity.ok("Administrador eliminado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el administrador: " + e.getMessage());
        }
    
}}