package proyecto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Chef extends Actor {
	@NotNull
	private Double precioServicio;
	
	@NotNull
	@Min(0)
	@Max(5)
	private Integer valoracion;
	
	@NotNull
	private Boolean activo;
	
	@ManyToOne
	private Contrato contrato;

	public Chef() {
		super();
	}

	public Chef(@NotNull Double precioServicio, @NotNull @Min(0) @Max(5) Integer valoracion, @NotNull Boolean activo) {
		super();
		this.precioServicio = precioServicio;
		this.valoracion = valoracion;
		this.activo = activo;
	}

	public Double getPrecioServicio() {
		return precioServicio;
	}

	public void setPrecioServicio(Double precioServicio) {
		this.precioServicio = precioServicio;
	}

	public Integer getValoracion() {
		return valoracion;
	}

	public void setValoracion(Integer valoracion) {
		this.valoracion = valoracion;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
