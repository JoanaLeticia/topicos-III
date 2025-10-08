package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.form.ItemCardapioImageForm;
import br.unitins.foodflow.model.TipoPeriodo;
import br.unitins.foodflow.repository.ItemCardapioRepository;
import br.unitins.foodflow.service.FileService;
import br.unitins.foodflow.service.ItemCardapioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
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

import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/itens-cardapio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemCardapioResource {

    @Inject
    ItemCardapioService itemService;

    @Inject
    FileService fileService;

    @Inject
    ItemCardapioRepository itemCardapioRepository;

    private static final Logger LOG = Logger.getLogger(ItemCardapioResource.class);

    @POST
    @RolesAllowed({ "Admin" }) // Ajuste conforme suas roles
    public Response create(@Valid ItemCardapioDTO dto) {
        ItemCardapioResponseDTO response = itemService.create(dto);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
    public Response update(@PathParam("id") Long id, @Valid ItemCardapioDTO dto) {
        ItemCardapioResponseDTO response = itemService.update(dto, id);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "Admin" })
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
    @Path("/periodo/id/{idPeriodo}")
    public Response findByPeriodo(@PathParam("idPeriodo") Integer idPeriodo) {
        List<ItemCardapioResponseDTO> response = itemService.findByPeriodo(idPeriodo);
        return Response.ok(response).build();
    }

    @GET
    @Path("/count")
    public long count() {
        return itemService.count();
    }

    @GET
    @Path("/nome/{nome}/count")
    public long totalPorNome(@PathParam("nome") String nome) {
        return itemService.count(nome);
    }

    @GET
    @Path("/periodo/{nomePeriodo}/count")
    public long totalPorPeriodo(
            @PathParam("nomePeriodo") String nomePeriodo,
            @QueryParam("precoMax") Double precoMax) {

        return itemService.countPorPeriodo(nomePeriodo, precoMax);
    }

    @GET
    @Path("/image/download/{nomeImagem}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("nomeImagem") String nomeImagem) {
        ResponseBuilder response = Response.ok(fileService.download(nomeImagem));
        response.header("Content-Disposition", "attachment;filename=" + nomeImagem);
        return response.build();
    }

    @PATCH
    @RolesAllowed({ "Admin" })
    @Path("/image/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response salvarImagem(@MultipartForm ItemCardapioImageForm form) {
        try {
            fileService.salvar(form.getId(), form.getNomeImagem(), form.getImagem());
            return Response.noContent().build();
        } catch (IOException e) {
            return Response.status(Status.CONFLICT).build();
        }
    }

    @GET
    @Path("/periodos")
    public Response getPeriodos() {
        return Response.ok(TipoPeriodo.values()).build();
    }

    @GET
    @Path("/periodo/{nomePeriodo}")
    public Response buscarPorPeriodo(
            @PathParam("nomePeriodo") String nomePeriodo,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("0") int size,
            @QueryParam("sort") String sort,
            @QueryParam("precoMax") Double precoMax) {

        try {
            List<ItemCardapioResponseDTO> itens = itemService.buscarPorPeriodo(
                    nomePeriodo, page, size, sort, precoMax);
            return Response.ok(itens).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/filtros/{periodo}")
    public Response getFiltrosPorPeriodo(@PathParam("periodo") String nomePeriodo) {
        try {
            LOG.infof("Buscando filtros para período: %s", nomePeriodo);

            Map<String, Object> filtros = itemService.getFiltrosPorPeriodo(nomePeriodo);

            if (filtros.isEmpty()) {
                LOG.warnf("Nenhum filtro encontrado para periodo: %s", nomePeriodo);
                return Response.status(Status.NOT_FOUND)
                        .entity("Nenhum produto encontrado para a periodo especificada")
                        .build();
            }

            return Response.ok(filtros).build();

        } catch (RuntimeException e) {
            LOG.errorf("Erro ao buscar filtros para periodo %s: %s", nomePeriodo, e.getMessage());
            return Response.status(Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}
