package proyecto.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Pattern;

@Entity
public class Cliente extends Actor {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cliente_alergias", joinColumns = @JoinColumn(name = "cliente_id"))
    private List<String> alergias = new ArrayList<>();

    @Pattern(regexp = "^\\d{5}$")
    private String codigoPostal;

    public List<String> getAlergias() { return alergias; }
    public void setAlergias(List<String> alergias) {
        this.alergias = alergias == null ? new ArrayList<>() : new ArrayList<>(alergias);
    }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}
