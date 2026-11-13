package proyecto.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
public record ContratoRequest(
    @NotNull @Positive Integer chefId,
    @NotNull @Future LocalDate fechaTrabajo,
    @Size(max = 1000) String comentario
) {}
