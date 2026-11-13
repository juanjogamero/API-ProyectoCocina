package proyecto.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
class JWTUtilsTest {
    @Test void generaYValidaTokenConClaveConfigurada() {
        JWTUtils jwt=new JWTUtils(new MockEnvironment().withProperty(
            "app.jwt.secret","Una-Clave-De-Pruebas-Larga-Y-Segura-2026-1234567890"));
        var auth=new UsernamePasswordAuthenticationToken("chef1",null,
            List.of(new SimpleGrantedAuthority("ROLE_CHEF")));
        String token=jwt.generateToken(auth);
        assertEquals("chef1",jwt.getUsernameOfToken(token));
    }
    @Test void rechazaClaveCorta() {
        assertThrows(IllegalStateException.class, ()->new JWTUtils(
            new MockEnvironment().withProperty("app.jwt.secret","muy-corta")));
    }
}
