package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ItemCardapioDTO;
import br.unitins.foodflow.dto.ItemCardapioResponseDTO;
import br.unitins.foodflow.model.TipoPeriodo;

import java.util.List;

public interface ItemCardapioService {
    ItemCardapioResponseDTO create(ItemCardapioDTO dto);
    ItemCardapioResponseDTO update(ItemCardapioDTO dto, Long id);
    void delete(Long id);
    ItemCardapioResponseDTO findById(Long id);
    List<ItemCardapioResponseDTO> findAll(int page, int pageSize, String sort);
    List<ItemCardapioResponseDTO> findAll(int page, int pageSize);
    List<ItemCardapioResponseDTO> findByNome(String nome, int page, int pageSize, String sort);
    List<ItemCardapioResponseDTO> findByPeriodo(TipoPeriodo periodo);
    List<ItemCardapioResponseDTO> findByPeriodo(Integer idPeriodo);
    long count();
    long count(String nome);
}
