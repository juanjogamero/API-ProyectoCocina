package proyecto.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import proyecto.entity.Admin;
import proyecto.entity.Rol;
import proyecto.respository.ActorRepository;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner bootstrapAdmin(ActorRepository repo, PasswordEncoder encoder, Environment env) {
        return args -> {
            String user = env.getProperty("APP_ADMIN_USER");
            String pass = env.getProperty("APP_ADMIN_PASSWORD");
            if (user == null || user.isBlank() || pass == null || pass.isBlank()) return;
            if (pass.length() < 12) throw new IllegalStateException("APP_ADMIN_PASSWORD debe tener al menos 12 caracteres");
            String username = user.trim().toLowerCase();
            if (repo.existsByUsernameIgnoreCase(username)) return;
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(encoder.encode(pass));
            admin.setRol(Rol.ADMINISTRADOR);
            admin.setNombre("Administrador");
            admin.setApellido1("Cocina");
            admin.setCorreo("admin@localhost.invalid");
            repo.save(admin);
        };
    }
}
