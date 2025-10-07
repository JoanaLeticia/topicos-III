package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.SugestaoChefDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;
import br.unitins.foodflow.service.SugestaoChefService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;

@Path("/sugestoes-chef")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SugestaoChefResource {

    @Inject
    SugestaoChefService sugestaoService;

    @POST
    @RolesAllowed({"Admin"})
    public Response create(@Valid SugestaoChefDTO dto) {
        SugestaoChefResponseDTO response = sugestaoService.create(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response update(@PathParam("id") Long id, @Valid SugestaoChefDTO dto) {
        SugestaoChefResponseDTO response = sugestaoService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Admin"})
    public Response delete(@PathParam("id") Long id) {
        sugestaoService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        SugestaoChefResponseDTO response = sugestaoService.findById(id);
        return Response.ok(response).build();
    }

    @GET
    @Path("/ativa")
    public Response findSugestaoAtiva() {
        SugestaoChefResponseDTO response = sugestaoService.findSugestaoAtiva();
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Não há sugestão do chef para hoje.")
                    .build();
        }
        return Response.ok(response).build();
    }

    @GET
    @Path("/data/{data}")
    public Response findByData(@PathParam("data") String data) {
        LocalDate localDate = LocalDate.parse(data);
        SugestaoChefResponseDTO response = sugestaoService.findByData(localDate);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Não há sugestão para a data " + data)
                    .build();
        }
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/antigas/{diasAtras}")
    @RolesAllowed({"Admin"})
    public Response deletarSugestoesAntigas(@PathParam("diasAtras") int diasAtras) {
        sugestaoService.deletarSugestoesAntigas(diasAtras);
        return Response.noContent().build();
    }
}
