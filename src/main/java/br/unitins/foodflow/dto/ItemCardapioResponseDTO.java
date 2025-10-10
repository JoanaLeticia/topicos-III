package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.TipoPeriodo;
import java.math.BigDecimal;

public record ItemCardapioResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal precoBase,
        BigDecimal precoComDesconto,
        String nomeImagem,
        TipoPeriodo periodo,
        Boolean isSugestaoChefe
) {
    public static ItemCardapioResponseDTO valueOf(ItemCardapio item) {
        return valueOf(item, false);
    }

    public static ItemCardapioResponseDTO valueOf(ItemCardapio item, Boolean isSugestaoChefe) {
        if (item == null) {
            return null;
        }

        BigDecimal precoFinalComDesconto = isSugestaoChefe 
            ? item.calcularPrecoComDesconto() 
            : null;

        return new ItemCardapioResponseDTO(
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                item.getPrecoBase(),
                precoFinalComDesconto,
                item.getNomeImagem(),
                item.getPeriodo(),
                isSugestaoChefe
        );
    }
}