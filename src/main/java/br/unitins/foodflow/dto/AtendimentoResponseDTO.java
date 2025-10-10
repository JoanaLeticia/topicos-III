package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.*;
import java.math.BigDecimal;

public record AtendimentoResponseDTO(
        Long id,
        TipoAtendimento tipo,
        BigDecimal taxa,
        Integer numeroMesa,
        EnderecoResponseDTO enderecoEntrega,
        ParceiroAppResponseDTO parceiro
) {
    public static AtendimentoResponseDTO valueOf(Atendimento atendimento) {
        if (atendimento == null) {
            return null;
        }

        Integer numeroMesa = null;
        EnderecoResponseDTO endereco = null;
        ParceiroAppResponseDTO parceiro = null;

        if (atendimento instanceof AtendimentoPresencial presencial) {
            numeroMesa = presencial.getNumeroMesa();
        } else if (atendimento instanceof AtendimentoDeliveryProprio deliveryProprio) {
            endereco = EnderecoResponseDTO.valueOf(deliveryProprio.getEnderecoEntrega());
        } else if (atendimento instanceof AtendimentoDeliveryAplicativo deliveryApp) {
            endereco = EnderecoResponseDTO.valueOf(deliveryApp.getEnderecoEntrega());
            parceiro = ParceiroAppResponseDTO.valueOf(deliveryApp.getParceiro());
        }

        return new AtendimentoResponseDTO(
                atendimento.getId(),
                atendimento.getTipo(),
                atendimento.calcularTaxa(),
                numeroMesa,
                endereco,
                parceiro
        );
    }
}
