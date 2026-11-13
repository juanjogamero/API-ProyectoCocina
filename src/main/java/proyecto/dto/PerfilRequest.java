package proyecto.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record PerfilRequest(
    String nombre,
    String apellido1,
    String apellido2,
    @Email String correo,
    @Pattern(regexp = "^[6-9][0-9]{8}$") String telefono,
    @DecimalMin("0.0") BigDecimal precioServicio,
    Boolean activo,
    @Pattern(regexp = "^\\d{5}$") String codigoPostal,
    List<String> alergias
) {}
