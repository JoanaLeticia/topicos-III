package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.model.TipoPeriodo;

import java.util.List;
import java.util.Map;

public interface ItemCardapioService {
    ItemCardapioResponseDTO create(ItemCardapioDTO dto);

    ItemCardapioResponseDTO update(ItemCardapioDTO dto, Long id);

    void delete(Long id);

    ItemCardapioResponseDTO findById(Long id);

    List<ItemCardapioResponseDTO> findAll(int page, int pageSize, String sort);

    List<ItemCardapioResponseDTO> findAll(int page, int pageSize);

    ItemCardapioResponseDTO salvarImagem(Long id, String nomeImagem);

    List<ItemCardapioResponseDTO> findByNome(String nome, int page, int pageSize, String sort);

    List<ItemCardapioResponseDTO> buscarPorPeriodo(String nomePeriodo, int page, int pageSize, String sort,
            Double precoMax);

    List<ItemCardapioResponseDTO> findByPeriodo(Integer idPeriodo);

    List<ItemCardapioResponseDTO> findByPeriodo(TipoPeriodo periodo);

    long count();

    long count(String nome);

    long countPorPeriodo(String nomePeriodo, Double precoMax);

    Map<String, Object> getFiltrosPorPeriodo(String nomePeriodo);
}
