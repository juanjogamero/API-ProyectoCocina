package proyecto.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.RegistroRequest;
import proyecto.entity.*;
import proyecto.respository.ActorRepository;

@ExtendWith(MockitoExtension.class)
class ActorServiceTest {
    @Mock ActorRepository repo;
    @Mock PasswordEncoder encoder;

    private RegistroRequest registration(Rol rol) {
        return new RegistroRequest("ana", "MiPasswordSeguro123", "Ana", "Pérez", "",
            "ana@example.com", "612345678", rol, BigDecimal.valueOf(120), "41001", List.of("Frutos secos"));
    }

    @Test void nuncaPermiteRegistrarAdministradoresEnPublico() {
        ActorService service = new ActorService(repo,encoder);
        ResponseStatusException error=assertThrows(ResponseStatusException.class,
            () -> service.register(registration(Rol.ADMINISTRADOR)));
        assertEquals(HttpStatus.BAD_REQUEST,error.getStatusCode());
        verifyNoInteractions(repo);
    }

    @Test void hashDePasswordAntesDePersistirChef() {
        ActorService service=new ActorService(repo,encoder);
        when(encoder.encode("MiPasswordSeguro123")).thenReturn("hash-bcrypt");
        when(repo.save(any(Actor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Actor actor=service.register(registration(Rol.CHEF));
        assertInstanceOf(Chef.class,actor);
        assertEquals("hash-bcrypt",actor.getPassword());
        assertEquals(Rol.CHEF,actor.getRol());
        assertEquals(0,((Chef)actor).getValoracion());
        verify(encoder).encode("MiPasswordSeguro123");
    }

    @Test void noDuplicaNombresDeUsuario() {
        ActorService service=new ActorService(repo,encoder);
        when(repo.existsByUsernameIgnoreCase("ana")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT,
            assertThrows(ResponseStatusException.class,() -> service.register(registration(Rol.CLIENTE)))
                .getStatusCode());
        verify(repo,never()).save(any());
    }
}
