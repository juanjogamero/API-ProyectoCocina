package proyecto.entity;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Contrato extends DomainEntity {

	@NotNull
	private LocalDate fechaContratacion;
	
	@NotNull
	private Date fechaTrabajo;
	
	private String comentario;
	
	private Integer valoracionCliente;
	
	@ManyToOne
	private Chef chef;

	public Contrato() {
		super();
	}

	public Contrato(@NotNull LocalDate fechaContratacion, @NotNull Date fechaTrabajo, String comentario,
			Integer valoracionCliente) {
		super();
		this.fechaContratacion = fechaContratacion;
		this.fechaTrabajo = fechaTrabajo;
		this.comentario = comentario;
		this.valoracionCliente = valoracionCliente;
	}

	public LocalDate getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(LocalDate fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	public Date getFechaTrabajo() {
		return fechaTrabajo;
	}

	public void setFechaTrabajo(Date fechaTrabajo) {
		this.fechaTrabajo = fechaTrabajo;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Integer getValoracionCliente() {
		return valoracionCliente;
	}

	public void setValoracionCliente(Integer valoracionCliente) {
		this.valoracionCliente = valoracionCliente;
	}
	
	
}
