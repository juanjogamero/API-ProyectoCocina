package proyecto.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import proyecto.dto.ActorView;
import proyecto.dto.RegistroRequest;
import proyecto.entity.Actor;
import proyecto.entity.ActorLogin;
import proyecto.security.JWTUtils;
import proyecto.service.ActorService;

@RestController
public class ActorController {
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwt;
    private final ActorService actors;

    public ActorController(AuthenticationManager authenticationManager, JWTUtils jwt, ActorService actors) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.actors = actors;
    }

    public record LoginResponse(String token, String tipo) {}

    @PostMapping({"/login","/auth/login"})
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody ActorLogin login) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.getUsername().toLowerCase(),login.getPassword()));
        return ResponseEntity.ok(new LoginResponse(jwt.generateToken(authentication),"Bearer"));
    }

    @PostMapping("/auth/registro")
    public ResponseEntity<ActorView> register(@Valid @RequestBody RegistroRequest request) {
        Actor actor = actors.register(request);
        return ResponseEntity.created(URI.create("/auth/usuarios/" + actor.getId())).body(ActorView.of(actor));
    }
}
