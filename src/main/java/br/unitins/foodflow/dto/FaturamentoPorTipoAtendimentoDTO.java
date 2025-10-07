package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.TipoAtendimento;
import java.math.BigDecimal;

public record FaturamentoPorTipoAtendimentoDTO(
        TipoAtendimento tipoAtendimento,
        BigDecimal faturamentoTotal,
        Long quantidadePedidos
) {}