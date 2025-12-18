package proyecto.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import proyecto.entity.Chef;
import proyecto.service.ChefService;

@RestController
@RequestMapping("/chef")
@Tag(name = "Chefs", description = "Controlador para la gestión de chefs")
public class ChefController {

    @Autowired
    private ChefService chefService;

    @GetMapping
    @Operation(summary = "Obtener todos los chefs", description = "Devuelve una lista de todos los chefs registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de chefs obtenida correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay chefs registrados")
    })
    public ResponseEntity<?> findAll() {
        List<Chef> chefs = chefService.findAll();
        if (chefs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No hay chefs registrados en el sistema");
        }
        return ResponseEntity.ok(chefs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar chef por ID", description = "Busca un chef específico utilizando su ID")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Chef encontrado"),
        @ApiResponse(responseCode = "404", description = "Chef no encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido")
    })
    public ResponseEntity<?> findById(@PathVariable int id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de chef inválido");
        }
        
        Optional<Chef> oChef = chefService.findById(id); // Asumiendo que tu servicio devuelve Optional, si devuelve objeto directo, ajusta aquí.
        
        if (oChef.isPresent()) {
            return ResponseEntity.ok(oChef.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chef con ID " + id + " no encontrado");
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo chef", description = "Registra un nuevo chef en el sistema")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Chef creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos del chef inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> save(@RequestBody Chef chef) {
        try {
            // Validaciones básicas (Ajusta los getters según los atributos reales de tu clase Chef)
            if (chef.getNombre() == null || chef.getNombre().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El nombre del chef es obligatorio");
            }
            // Ejemplo: si Chef tiene especialidad en lugar de apellido/correo, ajusta esto:
            // if (chef.getEspecialidad() == null ... )
            
            Chef savedChef = chefService.save(chef);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Chef creado correctamente con ID: " + savedChef.getId());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el chef: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un chef", description = "Actualiza la información de un chef existente")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Chef actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Chef no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Chef chef) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de chef inválido");
            }
            
            Chef updatedChef = chefService.update(id, chef);
            if (updatedChef == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chef con ID " + id + " no encontrado");
            }
            
            return ResponseEntity.ok("Chef actualizado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el chef: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un chef", description = "Elimina un chef existente del sistema")
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Chef eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Chef no encontrado"),
        @ApiResponse(responseCode = "400", description = "ID inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            if (id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID de chef inválido");
            }
            
   
            
            chefService.delete(id);
            return ResponseEntity.ok("Chef eliminado correctamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el chef: " + e.getMessage());
        }
    }
    

}