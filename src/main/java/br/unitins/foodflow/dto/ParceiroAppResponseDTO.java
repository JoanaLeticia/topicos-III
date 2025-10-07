package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.ParceiroApp;
import java.math.BigDecimal;

public record ParceiroAppResponseDTO(
        Long id,
        String nome,
        BigDecimal percentualComissao,
        BigDecimal taxaFixa
) {
    public static ParceiroAppResponseDTO valueOf(ParceiroApp parceiro) {
        if (parceiro == null) {
            return null;
        }

        return new ParceiroAppResponseDTO(
                parceiro.id,
                parceiro.getNome(),
                parceiro.getPercentualComissao(),
                parceiro.getTaxaFixa()
        );
    }
}
