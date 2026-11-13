package proyecto.dto;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import proyecto.entity.Rol;

public record RegistroRequest(
    @NotBlank String username,
    @NotBlank @Size(min = 10, max = 72) String password,
    @NotBlank String nombre,
    @NotBlank String apellido1,
    String apellido2,
    @NotBlank @Email String correo,
    @Pattern(regexp = "^[6-9][0-9]{8}$") String telefono,
    Rol rol,
    BigDecimal precioServicio,
    String codigoPostal,
    List<String> alergias
) {}
