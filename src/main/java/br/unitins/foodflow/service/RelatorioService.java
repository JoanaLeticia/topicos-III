package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.FaturamentoPorTipoAtendimentoDTO;
import br.unitins.foodflow.dto.ItemMaisVendidoDTO;

import java.time.LocalDate;
import java.util.List;

public interface RelatorioService {
    List<FaturamentoPorTipoAtendimentoDTO> getFaturamentoPorTipoAtendimento(LocalDate inicio, LocalDate fim);
    List<ItemMaisVendidoDTO> getItensMaisVendidos(LocalDate inicio, LocalDate fim, int limit);
    List<ItemMaisVendidoDTO> getItensMaisVendidosComSugestao(LocalDate inicio, LocalDate fim, int limit);
    List<ItemMaisVendidoDTO> getItensMaisVendidosSemSugestao(LocalDate inicio, LocalDate fim, int limit);
}