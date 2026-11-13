package proyecto.service;

import java.time.LocalDate;
import java.util.List;
import java.util.EnumSet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import proyecto.dto.*;
import proyecto.entity.*;
import proyecto.respository.*;

@Service
public class ContratoService {
    private static final EnumSet<EstadoContrato> RESERVADOS =
        EnumSet.of(EstadoContrato.PENDIENTE, EstadoContrato.CONFIRMADO);
    private final ContratoRepository repo;
    private final ChefRepository chefs;
    private final ClienteRepository clientes;
    private final ActorService actors;

    public ContratoService(ContratoRepository repo, ChefRepository chefs,
                           ClienteRepository clientes, ActorService actors) {
        this.repo = repo;
        this.chefs = chefs;
        this.clientes = clientes;
        this.actors = actors;
    }

    public List<ContratoView> findAll(String username) {
        Actor actor = actors.requireActor(username);
        List<Contrato> contratos = switch (actor.getRol()) {
            case ADMINISTRADOR -> repo.findAll();
            case CHEF -> repo.findByChefUsernameOrderByFechaTrabajoDesc(username);
            case CLIENTE -> repo.findByClienteUsernameOrderByFechaTrabajoDesc(username);
        };
        return contratos.stream().map(ContratoView::of).toList();
    }

    public ContratoView findById(int id, String username) {
        Contrato contrato = findAccessible(id, username);
        return ContratoView.of(contrato);
    }

    private Contrato findAccessible(int id, String username) {
        Contrato contrato = repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Contratacion no encontrada"));
        Actor actor = actors.requireActor(username);
        if (actor.getRol() != Rol.ADMINISTRADOR &&
            !contrato.getChef().getUsername().equals(username) &&
            !contrato.getCliente().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"No puedes acceder a esta contratacion");
        }
        return contrato;
    }

    @Transactional
    public ContratoView create(ContratoRequest request, String username) {
        Cliente cliente = clientes.findByUsername(username).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo los clientes pueden contratar"));
        if (!request.fechaTrabajo().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La fecha de trabajo debe ser futura");
        }
        // Se bloquea el chef antes de comprobar el calendario, evitando reservas
        // simultaneas para la misma fecha.
        Chef chef = chefs.findByIdForUpdate(request.chefId()).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Chef no encontrado"));
        if (!Boolean.TRUE.equals(chef.getActivo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Este chef no acepta contrataciones");
        }
        if (repo.existsByChefIdAndFechaTrabajoAndEstadoIn(chef.getId(),request.fechaTrabajo(),RESERVADOS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El chef ya tiene una contratacion en esa fecha");
        }
        Contrato contrato = new Contrato();
        contrato.setChef(chef);
        contrato.setCliente(cliente);
        contrato.setFechaContratacion(LocalDate.now());
        contrato.setFechaTrabajo(request.fechaTrabajo());
        contrato.setComentario(request.comentario());
        contrato.setEstado(EstadoContrato.PENDIENTE);
        return ContratoView.of(repo.save(contrato));
    }

    @Transactional
    public ContratoView update(int id, ContratoUpdateRequest request, String username) {
        if (actors.requireActor(username).getRol() != Rol.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo un administrador puede editar una contratacion");
        }
        Contrato contrato = repo.findByIdForUpdate(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Contratacion no encontrada"));
        if (contrato.getEstado() != EstadoContrato.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Solo se pueden editar contrataciones pendientes");
        }
        if (!request.fechaTrabajo().equals(contrato.getFechaTrabajo()) &&
            repo.existsByChefIdAndFechaTrabajoAndEstadoIn(
                contrato.getChef().getId(),request.fechaTrabajo(),RESERVADOS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Fecha no disponible");
        }
        contrato.setFechaTrabajo(request.fechaTrabajo());
        contrato.setComentario(request.comentario());
        return ContratoView.of(repo.save(contrato));
    }

    @Transactional
    public ContratoView changeEstado(int id, EstadoContrato nuevo, String username) {
        Contrato contrato = repo.findByIdForUpdate(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Contratacion no encontrada"));
        Actor actor = actors.requireActor(username);
        Rol rol = actor.getRol();
        boolean esChef = contrato.getChef().getUsername().equals(username);
        boolean esCliente = contrato.getCliente().getUsername().equals(username);
        if (rol == Rol.CLIENTE && (!esCliente || nuevo != EstadoContrato.CANCELADO)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Un cliente solo puede cancelar su contrato");
        }
        if (rol == Rol.CHEF && !esChef) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"No eres el chef contratado");
        }
        if (rol != Rol.ADMINISTRADOR && rol != Rol.CHEF && rol != Rol.CLIENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Acceso denegado");
        }

        EstadoContrato anterior = contrato.getEstado();
        boolean transicionValida =
            (anterior == EstadoContrato.PENDIENTE &&
                (nuevo == EstadoContrato.CONFIRMADO || nuevo == EstadoContrato.CANCELADO)) ||
            (anterior == EstadoContrato.CONFIRMADO &&
                (nuevo == EstadoContrato.COMPLETADO || nuevo == EstadoContrato.CANCELADO));
        if (!transicionValida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Cambio de estado no permitido");
        }
        if (nuevo == EstadoContrato.COMPLETADO && contrato.getFechaTrabajo().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"El servicio aun no se ha realizado");
        }
        contrato.setEstado(nuevo);
        return ContratoView.of(repo.save(contrato));
    }

    @Transactional
    public ContratoView rate(int id, int stars, String username) {
        Contrato contrato = repo.findByIdForUpdate(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Contratacion no encontrada"));
        if (!contrato.getCliente().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el cliente puede valorar");
        }
        if (contrato.getEstado() != EstadoContrato.COMPLETADO || contrato.getValoracionCliente() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Solo se puede valorar una vez un servicio completado");
        }
        contrato.setValoracionCliente(stars);
        repo.saveAndFlush(contrato);
        Double media = repo.puntuacionMedia(contrato.getChef().getId());
        if (media != null) {
            Chef chef = contrato.getChef();
            chef.setValoracion((int) Math.round(media));
            chefs.save(chef);
        }
        return ContratoView.of(contrato);
    }

    @Transactional
    public void delete(int id) {
        Contrato contrato = repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"Contratacion no encontrada"));
        repo.delete(contrato);
    }
}
