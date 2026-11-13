package proyecto.dto;
import java.time.LocalDate;
import proyecto.entity.Contrato;
import proyecto.entity.EstadoContrato;
public record ContratoView(int id, LocalDate fechaContratacion, LocalDate fechaTrabajo,
                           String comentario, EstadoContrato estado, Integer valoracionCliente,
                           ActorView chef, ActorView cliente) {
    public static ContratoView of(Contrato c) {
        return new ContratoView(c.getId(),c.getFechaContratacion(),c.getFechaTrabajo(),
            c.getComentario(),c.getEstado(),c.getValoracionCliente(),
            ActorView.of(c.getChef()),ActorView.of(c.getCliente()));
    }
}
