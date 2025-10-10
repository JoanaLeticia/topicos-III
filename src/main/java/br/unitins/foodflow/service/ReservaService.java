package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.DisponibilidadeMesaResponse;
import br.unitins.foodflow.dto.ReservaConvidadoDTO;
import br.unitins.foodflow.dto.ReservaDTO;
import br.unitins.foodflow.dto.ReservaResponseDTO;
import br.unitins.foodflow.model.Mesa;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    List<DisponibilidadeMesaResponse> verificarDisponibilidade(LocalDate data, Integer numeroPessoas);
    Mesa encontrarMesaDisponivel(LocalDateTime dataHora, Integer numeroPessoas);
    ReservaResponseDTO createConvidado(ReservaConvidadoDTO dto);
    List<ReservaResponseDTO> findByClienteEmail(String email);
    void delete(Long id, String login);
}