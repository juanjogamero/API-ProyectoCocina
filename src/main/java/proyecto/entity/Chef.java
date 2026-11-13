package proyecto.entity;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Chef extends Actor {
    @NotNull @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    private BigDecimal precioServicio = BigDecimal.ZERO;

    @Min(0) @Max(5)
    private Integer valoracion = 0;

    @NotNull private Boolean activo = true;

    public BigDecimal getPrecioServicio() { return precioServicio; }
    public void setPrecioServicio(BigDecimal precioServicio) { this.precioServicio = precioServicio; }
    public Integer getValoracion() { return valoracion; }
    public void setValoracion(Integer valoracion) { this.valoracion = valoracion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
