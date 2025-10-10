package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.Pedido;
import br.unitins.foodflow.model.StatusPedido;
import br.unitins.foodflow.model.SugestaoChefe;
import br.unitins.foodflow.model.TipoPeriodo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long idCliente,
        String nomeCliente,
        String emailCliente,
        List<ItemCardapioResponseDTO> itens,
        AtendimentoResponseDTO atendimento,
        StatusPedido status,
        LocalDateTime dataPedido,
        TipoPeriodo periodo,
        BigDecimal subtotal,
        BigDecimal taxaEntrega,
        BigDecimal valorTotal
) {
    public static PedidoResponseDTO valueOf(Pedido pedido) {
        return valueOf(pedido, null);
    }

    public static PedidoResponseDTO valueOf(Pedido pedido, SugestaoChefe sugestaoDoDia) {
        if (pedido == null) {
            return null;
        }

        BigDecimal subtotalCorreto = BigDecimal.ZERO;
        List<ItemCardapioResponseDTO> itensDTO = new ArrayList<>();

        for (ItemCardapio item : pedido.getItens()) {
            boolean isSugestao = sugestaoDoDia != null && sugestaoDoDia.isItemSugestao(item, pedido.getPeriodo());

            if (isSugestao) {
                subtotalCorreto = subtotalCorreto.add(item.calcularPrecoComDesconto());
            } else {
                subtotalCorreto = subtotalCorreto.add(item.getPrecoBase());
            }
            itensDTO.add(ItemCardapioResponseDTO.valueOf(item, isSugestao));
        }

        BigDecimal taxaEntrega = pedido.getAtendimento() != null 
                ? pedido.getAtendimento().calcularTaxa() 
                : BigDecimal.ZERO;

        BigDecimal valorTotalCorreto = subtotalCorreto.add(taxaEntrega);

        Long idCliente = (pedido.getCliente() != null) ? pedido.getCliente().getId() : null;
        String nomeCliente = (pedido.getCliente() != null) ? pedido.getCliente().getNome() : null;
        String emailCliente = (pedido.getCliente() != null) ? pedido.getCliente().getEmail() : null;

        return new PedidoResponseDTO(
                pedido.getId(),
                idCliente,
                nomeCliente,
                emailCliente,
                itensDTO,
                AtendimentoResponseDTO.valueOf(pedido.getAtendimento()),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getPeriodo(),
                subtotalCorreto,
                taxaEntrega,
                valorTotalCorreto
        );
    }
}