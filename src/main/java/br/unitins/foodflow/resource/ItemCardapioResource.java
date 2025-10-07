package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.service.ItemCardapioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
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
import jakarta.ws.rs.core.Response.ResponseBuilder;

import java.util.List;

@Path("/itens-cardapio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemCardapioResource {

    @Inject
    ItemCardapioService itemService;

    @POST
    @RolesAllowed({"Admin"}) // Ajuste conforme suas roles
    public Response create(@Valid ItemCardapioDTO dto) {
        ItemCardapioResponseDTO response = itemService.create(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response update(@PathParam("id") Long id, @Valid ItemCardapioDTO dto) {
        ItemCardapioResponseDTO response = itemService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response delete(@PathParam("id") Long id) {
        itemService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        ItemCardapioResponseDTO response = itemService.findById(id);
        return Response.ok(response).build();
    }

    @GET
    public PaginacaoResponse<ItemCardapioResponseDTO> buscarTodos(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        List<ItemCardapioResponseDTO> produtos = itemService.findAll(page, size);
        long total = itemService.count();
        return new PaginacaoResponse<>(produtos, page, size, total);
    }

    @GET
    @Path("/search")
    public Response findByNome(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("sort") String sort) {
        List<ItemCardapioResponseDTO> response = itemService.findByNome(nome, page, pageSize, sort);
        return Response.ok(response).build();
    }

    @GET
    @Path("/periodo/{idPeriodo}")
    public Response findByPeriodo(@PathParam("idPeriodo") Integer idPeriodo) {
        List<ItemCardapioResponseDTO> response = itemService.findByPeriodo(idPeriodo);
        return Response.ok(response).build();
    }

    @GET
    @Path("/count")
    public Response count(@QueryParam("nome") String nome) {
        long count = nome != null && !nome.isEmpty() 
            ? itemService.count(nome) 
            : itemService.count();
        return Response.ok(count).build();
    }
}
