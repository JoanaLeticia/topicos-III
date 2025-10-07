package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ParceiroAppDTO;
import br.unitins.foodflow.dto.ParceiroAppResponseDTO;

import java.util.List;

public interface ParceiroAppService {
    ParceiroAppResponseDTO create(ParceiroAppDTO dto);
    ParceiroAppResponseDTO update(ParceiroAppDTO dto, Long id);
    void delete(Long id);
    ParceiroAppResponseDTO findById(Long id);
    List<ParceiroAppResponseDTO> findAll(int page, int pageSize, String sort);
    List<ParceiroAppResponseDTO> findByNome(String nome, int page, int pageSize, String sort);
    long count();
    long count(String nome);
}