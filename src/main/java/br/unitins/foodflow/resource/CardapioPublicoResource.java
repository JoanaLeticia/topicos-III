package br.unitins.foodflow.resource;

import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;
import br.unitins.foodflow.service.ItemCardapioService;
import br.unitins.foodflow.service.SugestaoChefService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/cardapio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardapioPublicoResource {

    @Inject
    ItemCardapioService itemService;

    @Inject
    SugestaoChefService sugestaoService;

    @GET
    @Path("/completo")
    public Response getCardapioCompleto() {
        List<ItemCardapioResponseDTO> itensAlmoco = itemService.findByPeriodo(1);
        List<ItemCardapioResponseDTO> itensJantar = itemService.findByPeriodo(2);
        SugestaoChefResponseDTO sugestao = sugestaoService.findSugestaoAtiva();

        Map<String, Object> cardapio = new HashMap<>();
        cardapio.put("almoco", itensAlmoco);
        cardapio.put("jantar", itensJantar);
        cardapio.put("sugestaoChef", sugestao);

        return Response.ok(cardapio).build();
    }

    @GET
    @Path("/almoco")
    public Response getCardapioAlmoco() {
        List<ItemCardapioResponseDTO> itens = itemService.findByPeriodo(1);
        return Response.ok(itens).build();
    }

    @GET
    @Path("/jantar")
    public Response getCardapioJantar() {
        List<ItemCardapioResponseDTO> itens = itemService.findByPeriodo(2);
        return Response.ok(itens).build();
    }

    @GET
    @Path("/sugestao-chef")
    public Response getSugestaoChef() {
        SugestaoChefResponseDTO sugestao = sugestaoService.findSugestaoAtiva();
        if (sugestao == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Não há sugestão do chef para hoje.")
                    .build();
        }
        return Response.ok(sugestao).build();
    }
}