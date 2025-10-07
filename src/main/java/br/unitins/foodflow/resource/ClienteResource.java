package br.unitins.foodflow.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.foodflow.application.Result;
import br.unitins.foodflow.dto.ClienteDTO;
import br.unitins.foodflow.dto.ClienteResponseDTO;
import br.unitins.foodflow.dto.ClienteUpdateDTO;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.service.ClienteService;
import br.unitins.foodflow.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    private static final Logger LOG = Logger.getLogger(PedidoResource.class);

    @POST
    public Response incluir(ClienteDTO dto) {
        try {
            return Response.status(Status.CREATED).entity(service.create(dto)).build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @PUT
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/{id}")
    public Response alterar(ClienteDTO dto, @PathParam("id") Long id) {
        try {
            service.update(dto, id);
            return Response.noContent().build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @DELETE
    @RolesAllowed({ "Cliente", "Admin" })
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
    @RolesAllowed({ "Admin" })
    @Path("/count")
    public long total() {
        return service.count();
    }

    @GET
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            ClienteResponseDTO a = service.findById(id);
            return Response.ok(a).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    //@RolesAllowed({ "Admin" })
    public PaginacaoResponse<ClienteResponseDTO> buscarTodos(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize) {

        List<ClienteResponseDTO> clientes = service.findAll(page, pageSize);
        long total = service.count();
        return new PaginacaoResponse<>(clientes, page, pageSize, total);
    }

    @GET
    @Path("search/nome/{nome}")
    public List<ClienteResponseDTO> buscarPorNome(
            @PathParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        LOG.infof("Buscando admin pelo nome %s", nome);
        LOG.debug("Debug de busca pelo nome.");
        return service.findByNome(nome, page, size, sort);
    }

    @PUT
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("parcial/{id}")
    public Response alterarParcial(ClienteUpdateDTO dto, @PathParam("id") Long id) {
        LOG.infof("Atualizando parcialmente usuario de id:", id);
        try {
            service.updatePartial(dto, id);
            return Response.noContent().build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.BAD_REQUEST).entity(result).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PATCH
    @Transactional
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/updateSenha/{novaSenha}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSenha(
            @PathParam("novaSenha") String novaSenha,
            @HeaderParam("Senha-Atual") String senhaAtual) {

        String login = jwt.getSubject();
        try {
            usuarioService.updateSenha(login, novaSenha, senhaAtual);
            LOG.info("Senha atualizada!");
            return Response.ok("Senha atualizada com sucesso").build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao atualizar senha: " + e.getMessage())
                    .build();
        }
    }

    private boolean isOwner(Long clienteId) {
        if (clienteId == null || jwt == null || jwt.getSubject() == null) {
            return false;
        }

        try {
            String email = jwt.getSubject();
            Cliente cliente = clienteRepository.findByEmail(email);
            return cliente != null && clienteId.equals(cliente.getId());
        } catch (Exception e) {
            LOG.error("Erro ao verificar propriedade do recurso", e);
            return false;
        }
    }

}