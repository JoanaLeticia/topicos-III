package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.Pedido;
import br.unitins.foodflow.model.StatusPedido;
import br.unitins.foodflow.model.SugestaoChefe; // Importe a SugestaoChefe
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
    // Método valueOf antigo para manter a compatibilidade onde não há sugestão
    public static PedidoResponseDTO valueOf(Pedido pedido) {
        return valueOf(pedido, null);
    }

    // Método principal e corrigido
    public static PedidoResponseDTO valueOf(Pedido pedido, SugestaoChefe sugestaoDoDia) {
        if (pedido == null) {
            return null;
        }

        BigDecimal subtotalCorreto = BigDecimal.ZERO;
        List<ItemCardapioResponseDTO> itensDTO = new ArrayList<>();

        // Itera sobre os itens para calcular o subtotal E criar a lista de DTOs
        for (ItemCardapio item : pedido.getItens()) {
            boolean isSugestao = sugestaoDoDia != null && sugestaoDoDia.isItemSugestao(item, pedido.getPeriodo());

            if (isSugestao) {
                subtotalCorreto = subtotalCorreto.add(item.calcularPrecoComDesconto());
            } else {
                subtotalCorreto = subtotalCorreto.add(item.getPrecoBase());
            }
            // Cria o DTO do item já com a flag e o preço com desconto corretos
            itensDTO.add(ItemCardapioResponseDTO.valueOf(item, isSugestao));
        }

        BigDecimal taxaEntrega = pedido.getAtendimento() != null 
                ? pedido.getAtendimento().calcularTaxa() 
                : BigDecimal.ZERO;

        BigDecimal valorTotalCorreto = subtotalCorreto.add(taxaEntrega);

        // O resto da lógica de busca de dados do cliente permanece a mesma
        Long idCliente = (pedido.getCliente() != null) ? pedido.getCliente().getId() : null;
        String nomeCliente = (pedido.getCliente() != null) ? pedido.getCliente().getNome() : null;
        String emailCliente = (pedido.getCliente() != null) ? pedido.getCliente().getEmail() : null;

        return new PedidoResponseDTO(
                pedido.id,
                idCliente,
                nomeCliente,
                emailCliente,
                itensDTO, // Usa a nova lista de DTOs
                AtendimentoResponseDTO.valueOf(pedido.getAtendimento()),
                pedido.getStatus(),
                pedido.getDataPedido(),
                pedido.getPeriodo(),
                subtotalCorreto,    // Usa o subtotal correto
                taxaEntrega,
                valorTotalCorreto   // Usa o valor total correto
        );
    }
}