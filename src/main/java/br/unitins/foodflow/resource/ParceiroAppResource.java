package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.ParceiroAppDTO;
import br.unitins.foodflow.dto.ParceiroAppResponseDTO;
import br.unitins.foodflow.service.ParceiroAppService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/parceiros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ParceiroAppResource {

    @Inject
    ParceiroAppService parceiroService;

    @POST
    @RolesAllowed({"Admin"})
    public Response create(@Valid ParceiroAppDTO dto) {
        ParceiroAppResponseDTO response = parceiroService.create(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response update(@PathParam("id") Long id, @Valid ParceiroAppDTO dto) {
        ParceiroAppResponseDTO response = parceiroService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response delete(@PathParam("id") Long id) {
        parceiroService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        ParceiroAppResponseDTO response = parceiroService.findById(id);
        return Response.ok(response).build();
    }

    @GET
    public Response findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("sort") String sort) {
        List<ParceiroAppResponseDTO> response = parceiroService.findAll(page, pageSize, sort);
        return Response.ok(response).build();
    }

    @GET
    @Path("/search")
    public Response findByNome(
            @QueryParam("nome") String nome,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize,
            @QueryParam("sort") String sort) {
        List<ParceiroAppResponseDTO> response = parceiroService.findByNome(nome, page, pageSize, sort);
        return Response.ok(response).build();
    }

    @GET
    @Path("/count")
    public Response count(@QueryParam("nome") String nome) {
        long count = nome != null && !nome.isEmpty() 
            ? parceiroService.count(nome) 
            : parceiroService.count();
        return Response.ok(count).build();
    }
}
