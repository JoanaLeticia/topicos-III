package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;
import br.unitins.foodflow.service.ReservaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaResource {

    @Inject
    ReservaService reservaService;

    @POST
    @RolesAllowed({"User", "Admin"})
    public Response create(@Valid ReservaDTO dto, @Context SecurityContext securityContext) {
        Long usuarioId = getUsuarioIdFromContext(securityContext);
        ReservaResponseDTO response = reservaService.create(dto, usuarioId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"})
    public Response update(@PathParam("id") Long id, @Valid ReservaDTO dto) {
        ReservaResponseDTO response = reservaService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"})
    public Response delete(@PathParam("id") Long id) {
        reservaService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"})
    public Response findById(@PathParam("id") Long id) {
        ReservaResponseDTO response = reservaService.findById(id);
        return Response.ok(response).build();
    }

    @GET
    @Path("/codigo/{codigo}")
    public Response findByCodigoConfirmacao(@PathParam("codigo") String codigo) {
        ReservaResponseDTO response = reservaService.findByCodigoConfirmacao(codigo);
        return Response.ok(response).build();
    }

    @GET
    @Path("/minhas-reservas")
    @RolesAllowed({"User", "Admin"})
    public Response findMinhasReservas(
            @Context SecurityContext securityContext,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        Long usuarioId = getUsuarioIdFromContext(securityContext);
        List<ReservaResponseDTO> response = reservaService.findByUsuarioId(usuarioId, page, pageSize);
        return Response.ok(response).build();
    }

    @GET
    @Path("/minhas-reservas/futuras")
    @RolesAllowed({"User", "Admin"})
    public Response findMinhasReservasFuturas(@Context SecurityContext securityContext) {
        Long usuarioId = getUsuarioIdFromContext(securityContext);
        List<ReservaResponseDTO> response = reservaService.findReservasFuturas(usuarioId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/usuario/{usuarioId}")
    @RolesAllowed({"Admin"})
    public Response findByUsuarioId(
            @PathParam("usuarioId") Long usuarioId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<ReservaResponseDTO> response = reservaService.findByUsuarioId(usuarioId, page, pageSize);
        return Response.ok(response).build();
    }

    @GET
    @Path("/count/usuario/{usuarioId}")
    @RolesAllowed({"User", "Admin"})
    public Response countByUsuarioId(@PathParam("usuarioId") Long usuarioId) {
        long count = reservaService.countByUsuarioId(usuarioId);
        return Response.ok(count).build();
    }

    private Long getUsuarioIdFromContext(SecurityContext securityContext) {
        // IMPLEMENTAR: igual ao PedidoResource
        return 1L; // PLACEHOLDER
    }
}