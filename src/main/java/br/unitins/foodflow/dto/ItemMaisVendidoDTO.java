package br.unitins.foodflow.dto;

import java.math.BigDecimal;

public record ItemMaisVendidoDTO(
        String nomeItem,
        Long quantidadeVendida,
        BigDecimal faturamentoTotal
) {}