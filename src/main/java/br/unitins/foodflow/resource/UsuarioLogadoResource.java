package br.unitins.foodflow.resource;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import br.unitins.foodflow.dto.PedidoResponseDTO;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.repository.ClienteRepository;
import br.unitins.foodflow.repository.UsuarioRepository;
import br.unitins.foodflow.service.ClienteService;
import br.unitins.foodflow.service.PedidoService;
import br.unitins.foodflow.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuariologado")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioLogadoResource {
    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioRepository repository;

    @Inject
    PedidoService pedidoService;

    @Inject
    ClienteService clienteService;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    UsuarioService usuarioService;

    private static final Logger LOG = Logger.getLogger(UsuarioLogadoResource.class);

    @GET
    @RolesAllowed({ "Cliente", "Admin" })
    public Response getUsuario() {
        String login = jwt.getSubject();
        LOG.infof("login: %s", login);
        try {
            LOG.info("obtendo o login pelo token jwt");
            LOG.info("Retornando login");
            return Response.ok(usuarioService.findByEmail(login)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao retornar informações do usuário logado: " + e.getMessage())
                    .build();

        }
    }

    @PATCH
    @Transactional
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/updateNome/{nome}")
    public Response updateNome(@PathParam("nome") String nome) {
        String login = jwt.getSubject();
        try {
            usuarioService.updateNome(login, nome);
            LOG.info("Nome atualizado!");
            return Response.ok("Informações do usuário atualizadas com sucesso").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Erro ao atualizar informações do usuário: " + e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Transactional
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/updateSenha/{novaSenha}")
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

    @GET
    @RolesAllowed({ "Cliente", "Admin" })
    @Path("/PedidosDoUsuario")
    public Response getPedidosUsuario() {
        String login = jwt.getSubject();
        LOG.infof("Jwt: %s", login);
        Cliente usuarioLogado = clienteRepository.findByEmail(login);

        if (usuarioLogado != null) {
            List<PedidoResponseDTO> pedidos = pedidoService.pedidosUsuarioLogado(usuarioLogado);
            LOG.info("Retornando pedidos do usuário: " + login);
            return Response.ok(pedidos).build();
        } else {
            LOG.error("Usuário não encontrado: " + login);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuário não é cliente ou não foi encontrado.")
                    .build();
        }
    }
}
