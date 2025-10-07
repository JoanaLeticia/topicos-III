package br.unitins.foodflow.service;

import java.util.List;

import br.unitins.foodflow.dto.EstadoDTO;
import br.unitins.foodflow.dto.EstadoResponseDTO;

public interface EstadoService {
    EstadoResponseDTO create(EstadoDTO estado);
    EstadoResponseDTO update(EstadoDTO dto, Long id);
    void delete(long id);
    EstadoResponseDTO findById(long id);
    EstadoResponseDTO findBySigla(String sigla);
    List<EstadoResponseDTO> findAll(int page, int size);
    List<EstadoResponseDTO> findByNome(String nome, int page, int size, String sort);
    long count();
    long count(String nome);
}
