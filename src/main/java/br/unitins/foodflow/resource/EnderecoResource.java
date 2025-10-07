package br.unitins.foodflow.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.foodflow.application.Result;
import br.unitins.foodflow.dto.EnderecoDTO;
import br.unitins.foodflow.dto.EnderecoResponseDTO;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.service.EnderecoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/enderecos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnderecoResource {
    @Inject
    EnderecoService service;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    JsonWebToken jwt;

    private static final Logger LOG = Logger.getLogger(EnderecoResource.class);

    @GET
    @RolesAllowed({ "Admin" })
    public PaginacaoResponse<EnderecoResponseDTO> buscarTodos(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        List<EnderecoResponseDTO> enderecos = service.findAll(page, pageSize, sort);
        long total = service.count();
        return new PaginacaoResponse<>(enderecos, page, pageSize, total);
    }

    @GET
    @RolesAllowed({ "Admin" })
    @Path("search/logradouro/{logradouro}")
    public PaginacaoResponse<EnderecoResponseDTO> buscarPorLogradouro(
            @PathParam("logradouro") String logradouro,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize,
            @QueryParam("sort") @DefaultValue("id") String sort) {

        List<EnderecoResponseDTO> enderecos = service.findByLogradouro(logradouro, page, pageSize, sort);
        long total = service.count(logradouro);
        return new PaginacaoResponse<>(enderecos, page, pageSize, total);
    }

    @GET
    @RolesAllowed({ "Admin" })
    @Path("/municipio/{idMunicipio}")
    public PaginacaoResponse<EnderecoResponseDTO> buscarPorMunicipio(
            @PathParam("idMunicipio") Long idMunicipio,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        List<EnderecoResponseDTO> enderecos = service.findByMunicipio(idMunicipio, page, pageSize, sort);
        long total = enderecos.size();
        return new PaginacaoResponse<>(enderecos, page, pageSize, total);
    }

    @GET
    @RolesAllowed({ "Admin" })
    @Path("/bairro/{bairro}")
    public PaginacaoResponse<EnderecoResponseDTO> buscarPorBairro(
            @PathParam("bairro") String bairro,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("page_size") @DefaultValue("10") int pageSize,
            @QueryParam("sort") @DefaultValue("id") String sort) {
        List<EnderecoResponseDTO> enderecos = service.findByBairro(bairro, page, pageSize, sort);
        long total = service.countByBairro(bairro);
        return new PaginacaoResponse<>(enderecos, page, pageSize, total);
    }

    @GET
    @Path("/logradouro/{logradouro}/count")
    public Response totalPorLogradouro(@PathParam("logradouro") String logradouro) {
        try {
            return Response.ok(service.findByLogradouro(logradouro, 0, 0, logradouro)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            EnderecoResponseDTO a = service.findById(id);
            return Response.ok(a).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({ "Cliente", "Admin" })
    public Response incluir(EnderecoDTO dto) {
        LOG.info("DTO recebido: " + dto.toString());
        try {
            if (!jwt.getGroups().contains("Admin") && !isOwner(dto.idCliente())) {
                throw new NotAuthorizedException(
                        "Acesso negado: você só pode adicionar endereços ao seu próprio cadastro");
            }
            return Response.status(Status.CREATED).entity(service.create(dto)).build();
        } catch (ConstraintViolationException e) {
            Result result = new Result(e.getConstraintViolations());
            return Response.status(Status.NOT_FOUND).entity(result).build();
        }
    }

    @PUT
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/{id}")
    public Response alterar(EnderecoDTO dto, @PathParam("id") Long id) {
        LOG.info("DTO recebido: " + dto.toString());
        try {
            EnderecoResponseDTO endereco = service.findById(id);
            if (!jwt.getGroups().contains("Admin") && !isOwner(endereco.clienteId())) {
                throw new NotAuthorizedException("Acesso negado: você só pode editar seus próprios endereços");
            }
            EnderecoResponseDTO enderecoAtualizado = service.update(dto, id); // Captura o retorno do service
            return Response.ok(enderecoAtualizado).build(); // Retorna 200 com o endereço atualizado
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
            EnderecoResponseDTO endereco = service.findById(id);
            if (!jwt.getGroups().contains("Admin") && !isOwner(endereco.clienteId())) {
                throw new NotAuthorizedException("Acesso negado: você só pode excluir seus próprios endereços");
            }
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
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/cliente/{clienteId}")
    public Response buscarPorClienteId(@PathParam("clienteId") Long clienteId) {
        String email = jwt.getSubject();
        Cliente cliente = clienteRepository.findByEmail(email);

        if (!jwt.getGroups().contains("Admin") && !cliente.getId().equals(clienteId)) {
            throw new NotAuthorizedException("Acesso negado");
        }

        try {
            List<EnderecoResponseDTO> enderecos = service.findByClienteId(clienteId);
            return Response.ok(enderecos).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
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
