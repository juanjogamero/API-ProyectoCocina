package proyecto.dto;
import java.math.BigDecimal;
import proyecto.entity.Chef;
public record ChefView(int id, String nombre, String apellido1, BigDecimal precioServicio,
                       int valoracion, boolean activo) {
    public static ChefView of(Chef chef) {
        return new ChefView(chef.getId(),chef.getNombre(),chef.getApellido1(),
            chef.getPrecioServicio(),chef.getValoracion() == null ? 0 : chef.getValoracion(),
            Boolean.TRUE.equals(chef.getActivo()));
    }
}
