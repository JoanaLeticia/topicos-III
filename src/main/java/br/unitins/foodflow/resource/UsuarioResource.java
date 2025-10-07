package br.unitins.foodflow.resource;

import java.util.List;

import br.unitins.foodflow.dto.PaginacaoResponse;
import br.unitins.foodflow.dto.UsuarioResponseDTO;
import br.unitins.foodflow.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/usuarios")
@RolesAllowed({ "Admin" })
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @Inject
    UsuarioService service;

    @GET
    public PaginacaoResponse<UsuarioResponseDTO> buscarTodos(
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("page_size") @DefaultValue("10") int pageSize,
        @QueryParam("sort") @DefaultValue("id") String sort) {
        
        List<UsuarioResponseDTO> usuarios = service.findAll(page, pageSize, sort);
        long total = service.count();
        return new PaginacaoResponse<>(usuarios, page, pageSize, total);
    }

    @GET
    @Path("/nome/{nome}")
    public PaginacaoResponse<UsuarioResponseDTO> buscarPorNome(
        @PathParam("nome") String nome,
        @QueryParam("page") @DefaultValue("0") int page,
        @QueryParam("page_size") @DefaultValue("10") int pageSize,
        @QueryParam("sort") @DefaultValue("id") String sort) {
        
        List<UsuarioResponseDTO> usuarios = service.findByNome(nome, page, pageSize, sort);
        long total = service.count(nome);
        return new PaginacaoResponse<>(usuarios, page, pageSize, total);
    }

    @GET
    @Path("/nome/{nome}/count")
    public long totalPorNome(@PathParam("nome") String nome) {
        return service.count(nome);
    }

    @GET
    @Path("/count")
    public long total() {
        return service.count();
    }
}