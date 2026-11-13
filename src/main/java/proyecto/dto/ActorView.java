package proyecto.dto;

import proyecto.entity.Actor;
import proyecto.entity.Rol;
public record ActorView(int id, String username, String nombre, Rol rol) {
    public static ActorView of(Actor a) { return new ActorView(a.getId(), a.getUsername(), a.getNombre(), a.getRol()); }
}
