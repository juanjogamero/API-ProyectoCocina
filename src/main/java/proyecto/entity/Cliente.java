package proyecto.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

@Entity
public class Cliente extends Actor{
	
	private List<String> alergias;
	private int codigoPostal;
	
	@OneToMany
	private List<Contrato> contrataciones;

	public Cliente() {
		super();
	}

	public Cliente(List<String> alergias, int codigoPostal, List<Contrato> contrataciones) {
		super();
		this.alergias = alergias;
		this.codigoPostal = codigoPostal;
		this.contrataciones = contrataciones;
	}

	public List<String> getAlergias() {
		return alergias;
	}

	public void setAlergias(List<String> alergias) {
		this.alergias = alergias;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public List<Contrato> getContrataciones() {
		return contrataciones;
	}

	public void setContrataciones(List<Contrato> contrataciones) {
		this.contrataciones = contrataciones;
	}
	
	
	
}
