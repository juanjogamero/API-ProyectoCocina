package proyecto.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Contrato extends DomainEntity {
    @Column(nullable = false)
    private LocalDate fechaContratacion;
    @Column(nullable = false)
    private LocalDate fechaTrabajo;
    @Column(length = 1000)
    private String comentario;
    private Integer valoracionCliente;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoContrato estado = EstadoContrato.PENDIENTE;
    @ManyToOne(optional = false)
    @JoinColumn(name = "chef_id", nullable = false)
    private Chef chef;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    public LocalDate getFechaTrabajo() { return fechaTrabajo; }
    public void setFechaTrabajo(LocalDate fechaTrabajo) { this.fechaTrabajo = fechaTrabajo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Integer getValoracionCliente() { return valoracionCliente; }
    public void setValoracionCliente(Integer valoracionCliente) { this.valoracionCliente = valoracionCliente; }
    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }
    public Chef getChef() { return chef; }
    public void setChef(Chef chef) { this.chef = chef; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
