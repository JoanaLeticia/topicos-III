package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.DisponibilidadeMesaResponse;
import br.unitins.foodflow.dto.MesaResponseDTO;
import br.unitins.foodflow.dto.ReservaConvidadoDTO;
import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;
import br.unitins.foodflow.model.Mesa;
import br.unitins.foodflow.service.ReservaService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/reservas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservaResource {

    @Inject
    ReservaService reservaService;

    @Inject
    JsonWebToken jwt;

    @POST
    @RolesAllowed({ "Cliente", "Admin" })
    public Response create(@Valid ReservaDTO dto, @Context SecurityContext securityContext) {
        Long usuarioId = getUsuarioIdFromContext(securityContext);
        ReservaResponseDTO response = reservaService.create(dto, usuarioId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/convidado")
    @PermitAll
    public Response createConvidado(@Valid ReservaConvidadoDTO dto) {
        ReservaResponseDTO response = reservaService.createConvidado(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
    public Response update(@PathParam("id") Long id, @Valid ReservaDTO dto) {
        ReservaResponseDTO response = reservaService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Cliente", "Admin" })
    public Response delete(@PathParam("id") Long id) {
        try {
            String login = jwt.getSubject();
            reservaService.delete(id, login);
            return Response.noContent().build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "User", "Admin" })
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
    @Path("/minhas-reservas/futuras")
    @RolesAllowed({ "User", "Admin" })
    public Response findMinhasReservasFuturas(@Context SecurityContext securityContext) {
        Long usuarioId = getUsuarioIdFromContext(securityContext);
        List<ReservaResponseDTO> response = reservaService.findReservasFuturas(usuarioId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/usuario/{usuarioId}")
    @RolesAllowed({ "Admin" })
    public Response findByUsuarioId(
            @PathParam("usuarioId") Long usuarioId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<ReservaResponseDTO> response = reservaService.findByUsuarioId(usuarioId, page, pageSize);
        return Response.ok(response).build();
    }

    @GET
    @Path("/count/usuario/{usuarioId}")
    @RolesAllowed({ "User", "Admin" })
    public Response countByUsuarioId(@PathParam("usuarioId") Long usuarioId) {
        long count = reservaService.countByUsuarioId(usuarioId);
        return Response.ok(count).build();
    }

    private Long getUsuarioIdFromContext(SecurityContext securityContext) {
        // IMPLEMENTAR: igual ao PedidoResource
        return 1L; // PLACEHOLDER
    }

    @GET
    @Path("/disponibilidade")
    public Response verificarDisponibilidade(
            @QueryParam("data") String dataStr,
            @QueryParam("numeroPessoas") Integer numeroPessoas) {

        try {
            LocalDate data = LocalDate.parse(dataStr);
            List<DisponibilidadeMesaResponse> disponibilidade = reservaService.verificarDisponibilidade(data,
                    numeroPessoas);
            return Response.ok(disponibilidade).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Formato de data inválido. Use yyyy-MM-dd"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/encontrar-mesa")
    public Response encontrarMesaDisponivel(
            @QueryParam("data") String dataStr,
            @QueryParam("horario") String horarioStr,
            @QueryParam("numeroPessoas") Integer numeroPessoas) {

        try {
            LocalDate data = LocalDate.parse(dataStr);
            LocalTime horario = LocalTime.parse(horarioStr);
            LocalDateTime dataHora = LocalDateTime.of(data, horario);

            Mesa mesa = reservaService.encontrarMesaDisponivel(dataHora, numeroPessoas);

            if (mesa != null) {
                return Response.ok(MesaResponseDTO.valueOf(mesa)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Nenhuma mesa disponível")
                        .build();
            }

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/minhas-reservas")
    @RolesAllowed({ "Cliente" })
    public Response getMinhasReservas() {
        String login = jwt.getSubject();

        try {
            List<ReservaResponseDTO> reservas = reservaService.findByClienteEmail(login);
            return Response.ok(reservas).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}