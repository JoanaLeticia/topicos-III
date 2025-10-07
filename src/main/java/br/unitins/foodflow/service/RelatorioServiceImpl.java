package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.FaturamentoPorTipoAtendimentoDTO;
import br.unitins.foodflow.dto.ItemMaisVendidoDTO;
import br.unitins.foodflow.model.TipoAtendimento;
import br.unitins.foodflow.repository.PedidoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RelatorioServiceImpl implements RelatorioService {

    @Inject
    PedidoRepository pedidoRepository;

    @Override
    public List<FaturamentoPorTipoAtendimentoDTO> getFaturamentoPorTipoAtendimento(LocalDate inicio, LocalDate fim) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        List<Object[]> resultados = pedidoRepository.findFaturamentoPorTipoAtendimento(dataInicio, dataFim);

        return resultados.stream()
                .map(result -> new FaturamentoPorTipoAtendimentoDTO(
                        (TipoAtendimento) result[0],
                        (BigDecimal) result[1],
                        ((Number) result[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemMaisVendidoDTO> getItensMaisVendidos(LocalDate inicio, LocalDate fim, int limit) {
        LocalDateTime dataInicio = inicio.atStartOfDay();
        LocalDateTime dataFim = fim.atTime(LocalTime.MAX);

        List<Object[]> resultados = pedidoRepository.findItensMaisVendidos(dataInicio, dataFim, limit);

        return resultados.stream()
                .map(result -> new ItemMaisVendidoDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue(),
                        (BigDecimal) result[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemMaisVendidoDTO> getItensMaisVendidosComSugestao(LocalDate inicio, LocalDate fim, int limit) {
        // TODO: Implementar query específica para itens vendidos quando eram sugestão
        // Por enquanto, retorna os mais vendidos gerais
        return getItensMaisVendidos(inicio, fim, limit);
    }

    @Override
    public List<ItemMaisVendidoDTO> getItensMaisVendidosSemSugestao(LocalDate inicio, LocalDate fim, int limit) {
        // TODO: Implementar query específica para itens vendidos quando NÃO eram sugestão
        // Por enquanto, retorna os mais vendidos gerais
        return getItensMaisVendidos(inicio, fim, limit);
    }
}