package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;

import java.util.List;

public interface ReservaService {
    ReservaResponseDTO create(ReservaDTO dto, Long usuarioId);
    ReservaResponseDTO update(ReservaDTO dto, Long id);
    void delete(Long id);
    ReservaResponseDTO findById(Long id);
    ReservaResponseDTO findByCodigoConfirmacao(String codigo);
    List<ReservaResponseDTO> findByUsuarioId(Long usuarioId, int page, int pageSize);
    List<ReservaResponseDTO> findReservasFuturas(Long usuarioId);
    long countByUsuarioId(Long usuarioId);
}