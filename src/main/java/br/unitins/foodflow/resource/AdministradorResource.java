package br.unitins.foodflow.resource;

import java.util.List;

import org.jboss.logging.Logger;

import br.unitins.foodflow.application.Result;
import br.unitins.foodflow.dto.AdministradorDTO;
import br.unitins.foodflow.dto.AdministradorResponseDTO;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.service.AdministradorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/administradores")
@RolesAllowed({ "Admin" })
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdministradorResource {
    @Inject
    AdministradorService service;

    private static final Logger LOG = Logger.getLogger(AdministradorResource.class);

    @POST
    @Transactional
    public Response incluir(AdministradorDTO dto) {
        try {
            AdministradorResponseDTO retorno = service.create(dto);
            return Response.status(201).entity(retorno).build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @PUT
    @Transactional
    @Path("/{id}")
    public Response alterar(AdministradorDTO dto, @PathParam("id") Long id) {
        try {
            service.update(dto, id);
        return Response.noContent().build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response apagar(@PathParam("id") Long id) {
        try {
            service.delete(id);
        return Response.noContent().build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @GET
    @Path("/count")
    public long total() {
        return service.count();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            AdministradorResponseDTO administrador = service.findById(id);
            return Response.ok(administrador).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/nome/{nome}/count")
    public long totalPorNome(@PathParam("nome") String nome) {
        return service.count(nome);
    }

    @GET
    public PaginacaoResponse<AdministradorResponseDTO> buscarTodos(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        List<AdministradorResponseDTO> admins = service.findAll(page, size);
        long total = service.count();
        return new PaginacaoResponse<>(admins, page, size, total);
    }

    @GET
    @Path("search/nome/{nome}")
    public List<AdministradorResponseDTO> buscarPorNome(
            @PathParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        LOG.infof("Buscando admin pelo nome %s", nome);
        LOG.debug("Debug de busca pelo nome.");
        return service.findByNome(nome, page, size, sort);
    }
}

