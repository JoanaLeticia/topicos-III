package br.unitins.foodflow.service;

import java.util.List;

import br.unitins.foodflow.dto.TelefoneDTO;
import br.unitins.foodflow.dto.TelefoneResponseDTO;

public interface TelefoneService {
    TelefoneResponseDTO create(TelefoneDTO telefone);
    TelefoneResponseDTO update(TelefoneDTO dto, Long id);
    void delete(long id);
    TelefoneResponseDTO findById(long id);
    List<TelefoneResponseDTO> findAll(int page, int pageSize, String sort);
    List<TelefoneResponseDTO> findByNumero(String numero, int page, int pageSize, String sort);
    long count();
    long count(String numero);
}
