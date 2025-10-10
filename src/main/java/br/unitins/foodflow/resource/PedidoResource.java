package br.unitins.foodflow.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.foodflow.application.Result;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.dto.PedidoDTO;
import br.unitins.foodflow.dto.PedidoResponseDTO;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.model.StatusPedido;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.service.PedidoService;
import io.smallrye.common.constraint.NotNull;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PedidoResource {
    @Inject
    PedidoService service;

    @Inject
    JsonWebToken jwt;

    @Inject
    ClienteRepository clienteRepository;

    private static final Logger LOG = Logger.getLogger(PedidoResource.class);

    @POST
    @RolesAllowed({ "Cliente", "Admin" })
    public Response incluir(PedidoDTO dto) {
        try {
            String email = jwt.getSubject();
            LOG.info("Recuperando o identificador do usuário do token" + email);
            PedidoResponseDTO retorno = service.create(dto, email);
            return Response.status(201).entity(retorno).build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
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
    @Path("/{id}")
    @RolesAllowed({ "Cliente", "Admin" })
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            if (!jwt.getGroups().contains("Admin") && !isOwner2(id)) {
                return Response.status(Status.FORBIDDEN)
                        .entity("Acesso negado: você só pode visualizar seus próprios pedidos")
                        .build();
            }

            PedidoResponseDTO pedido = service.findById(id);
            return Response.ok(pedido).build();

        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed({ "Admin" })
    public PaginacaoResponse<PedidoResponseDTO> buscarTodos(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize,
            @QueryParam("sort") @DefaultValue("id") String sort) {

        List<PedidoResponseDTO> pedidos = service.findAll(page, pageSize, sort);
        long total = service.count();
        return new PaginacaoResponse<>(pedidos, page, pageSize, total);
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response atualizarStatus(
            @PathParam("id") @Min(1) Long id,
            @QueryParam("status") @NotNull StatusPedido novoStatus) {
        try {
            service.updateStatus(id, novoStatus);
            return Response.ok("Status do pedido atualizado").build();
        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IllegalStateException e) {
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/status/{status}")
    @RolesAllowed({ "Admin" })
    public PaginacaoResponse<PedidoResponseDTO> buscarPorStatus(
            @PathParam("status") StatusPedido status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        List<PedidoResponseDTO> pedidos = service.findByStatus(status, page, size, sort);
        long total = service.countByStatus(status);
        return new PaginacaoResponse<>(pedidos, page, size, total);
    }

    @GET
    @Path("/ultimo-pedido")
    @RolesAllowed({ "Cliente", "Admin" })
    public Response buscarUltimoPedido() {
        try {
            String email = jwt.getSubject();
            LOG.info("Buscando último pedido para o usuário: " + email);
            PedidoResponseDTO ultimoPedido = service.findLastByUser(email);
            return Response.ok(ultimoPedido).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro ao buscar último pedido", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar último pedido")
                    .build();
        }
    }

    @GET
    @Path("/cliente/{clienteId}")
    @RolesAllowed({ "Cliente", "Admin" })
    public Response buscarPedidosPorClienteId(@PathParam("clienteId") Long clienteId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        try {
            if (!jwt.getGroups().contains("Admin") && !isOwner(clienteId)) {
                return Response.status(Status.FORBIDDEN)
                        .entity("Acesso negado: você só pode visualizar seus próprios pedidos")
                        .build();
            }
            List<PedidoResponseDTO> pedidos = service.findByClienteId(clienteId, page, size);
            long total = service.count();
            return Response.ok(pedidos).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Erro ao buscar pedidos do cliente", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar pedidos do cliente").build();
        }
    }

    private boolean isOwner(Long clienteId) {
        if (clienteId == null || jwt == null || jwt.getSubject() == null) {
            return false;
        }

        try {
            Cliente cliente = clienteRepository.findById(clienteId);
            String email = jwt.getSubject();

            return cliente != null && email.equals(cliente.getEmail());
        } catch (Exception e) {
            LOG.error("Erro ao verificar propriedade do cliente", e);
            return false;
        }
    }

    private boolean isOwner2(Long pedidoId) {
        if (pedidoId == null || jwt == null || jwt.getSubject() == null) {
            return false;
        }

        try {
            PedidoResponseDTO pedido = service.findById(pedidoId);

            return pedido != null && jwt.getSubject().equals(pedido.emailCliente());
        } catch (Exception e) {
            LOG.error("Erro ao verificar propriedade do pedido", e);
            return false;
        }
    }

    @GET
    @Path("/meus-pedidos")
    @RolesAllowed({ "Cliente" })
    public Response getMeusPedidos() {
        String login = jwt.getSubject();

        try {
            List<PedidoResponseDTO> pedidos = service.findByClienteEmail(login);
            return Response.ok(pedidos).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
