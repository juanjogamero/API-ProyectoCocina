package proyecto.dto;
import jakarta.validation.constraints.NotNull;
import proyecto.entity.EstadoContrato;
public record EstadoRequest(@NotNull EstadoContrato estado) {}
