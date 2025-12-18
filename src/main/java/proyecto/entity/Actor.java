package proyecto.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Actor extends DomainEntity{
	private String username;
	private String password;
	private Rol rol;
	
	@NotBlank
	private String nombre;
	@NotBlank
	private String apellido1;
	@NotBlank
	private String apellido2;
	@NotBlank
	@Email
	private String correo;
	
	@Pattern(regexp = "^[6-9][0-9]{8}$")
	private String telefono;
	
	

	public Actor() {
		super();
	}



	public Actor(String username, String password, Rol rol, @NotBlank String nombre, @NotBlank String apellido1,
			@NotBlank String apellido2, @NotBlank @Email String correo,
			@Pattern(regexp = "^[6-9][0-9]{8}$") String telefono) {
		super();
		this.username = username;
		this.password = password;
		this.rol = rol;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.correo = correo;
		this.telefono = telefono;
	}





	public String getNombre() {
		return nombre;
	}



	public void setNombre(String nombre) {
		this.nombre = nombre;
	}



	public String getApellido1() {
		return apellido1;
	}



	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}



	public String getApellido2() {
		return apellido2;
	}



	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}



	public String getCorreo() {
		return correo;
	}



	public void setCorreo(String correo) {
		this.correo = correo;
	}



	public String getTelefono() {
		return telefono;
	}



	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}



	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Rol getRol() {
		return rol;
	}
	public void setRol(Rol rol) {
		this.rol = rol;
	}
}
