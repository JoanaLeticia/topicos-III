package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.TipoPeriodo;
import java.math.BigDecimal;

public record ItemCardapioResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal precoBase,
        BigDecimal precoComDesconto, // Este campo será null se não houver desconto
        TipoPeriodo periodo,
        Boolean isSugestaoChefe
) {
    // Este método é um atalho para quando não sabemos se é sugestão
    public static ItemCardapioResponseDTO valueOf(ItemCardapio item) {
        return valueOf(item, false);
    }

    // Este é o método principal que usaremos
    public static ItemCardapioResponseDTO valueOf(ItemCardapio item, Boolean isSugestaoChefe) {
        if (item == null) {
            return null;
        }

        // LÓGICA CORRIGIDA: Só calcula o preço com desconto se for a sugestão
        BigDecimal precoFinalComDesconto = isSugestaoChefe 
            ? item.calcularPrecoComDesconto() 
            : null;

        return new ItemCardapioResponseDTO(
                item.id,
                item.getNome(),
                item.getDescricao(),
                item.getPrecoBase(),
                precoFinalComDesconto, // Usa o valor condicional
                item.getPeriodo(),
                isSugestaoChefe
        );
    }
}