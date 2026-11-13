package proyecto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.ContratoRequest;
import proyecto.entity.*;
import proyecto.respository.*;

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {
    @Mock ContratoRepository contratos;
    @Mock ChefRepository chefs;
    @Mock ClienteRepository clientes;
    @Mock ActorService actores;
    ContratoService service;

    @BeforeEach void init(){service=new ContratoService(contratos,chefs,clientes,actores);}

    @Test void rechazaFechasYaReservadasSinGuardarContrato() {
        Cliente cliente = new Cliente();
        Chef chef = new Chef();
        chef.setId(2);
        chef.setActivo(true);
        chef.setPrecioServicio(BigDecimal.valueOf(90));
        LocalDate fecha = LocalDate.now().plusDays(4);
        when(clientes.findByUsername("maria")).thenReturn(Optional.of(cliente));
        when(chefs.findByIdForUpdate(2)).thenReturn(Optional.of(chef));
        when(contratos.existsByChefIdAndFechaTrabajoAndEstadoIn(eq(2),eq(fecha),anyCollection()))
            .thenReturn(true);

        ResponseStatusException error=assertThrows(ResponseStatusException.class,
            ()->service.create(new ContratoRequest(2,fecha,"Cena para cuatro"),"maria"));
        assertEquals(HttpStatus.CONFLICT,error.getStatusCode());
        verify(contratos,never()).save(any());
    }

    @Test void noPermiteQueUnClienteConfirmeElServicio() {
        Contrato c=new Contrato();
        Chef chef=new Chef();chef.setUsername("cocinero");
        Cliente cliente=new Cliente();cliente.setUsername("maria");
        c.setChef(chef);c.setCliente(cliente);c.setEstado(EstadoContrato.PENDIENTE);
        when(contratos.findByIdForUpdate(5)).thenReturn(Optional.of(c));
        when(actores.requireActor("maria")).thenReturn(cliente);
        cliente.setRol(Rol.CLIENTE);

        ResponseStatusException error=assertThrows(ResponseStatusException.class,
            ()->service.changeEstado(5,EstadoContrato.CONFIRMADO,"maria"));
        assertEquals(HttpStatus.FORBIDDEN,error.getStatusCode());
        verify(contratos,never()).save(any());
    }

    @Test void rechazaValoracionesDeOtroCliente() {
        Contrato c=new Contrato();
        Chef chef=new Chef();chef.setUsername("cocinero");
        Cliente cliente=new Cliente();cliente.setUsername("maria");
        c.setChef(chef);c.setCliente(cliente);c.setEstado(EstadoContrato.COMPLETADO);
        when(contratos.findByIdForUpdate(3)).thenReturn(Optional.of(c));
        assertEquals(HttpStatus.FORBIDDEN,
            assertThrows(ResponseStatusException.class,()->service.rate(3,5,"otro")).getStatusCode());
    }
}
