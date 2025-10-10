package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.PedidoDTO;
import br.unitins.foodflow.dto.PedidoResponseDTO;
import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.model.StatusPedido;

import java.util.List;

public interface PedidoService {
    PedidoResponseDTO create(PedidoDTO dto, String email);
    PedidoResponseDTO update(PedidoDTO dto, Long id);
    PedidoResponseDTO updateStatus(Long id, StatusPedido novoStatus);
    void delete(Long id);
    PedidoResponseDTO findById(Long id);
    List<PedidoResponseDTO> findByClienteId(Long clienteId, int page, int pageSize);
    public List<PedidoResponseDTO> pedidosUsuarioLogado(Cliente cliente);
    List<PedidoResponseDTO> findByStatus(StatusPedido status, int page, int size, String sort);
    List<PedidoResponseDTO> findAll(int page, int pageSize, String sort);
    long count();
    long countByClienteId(Long clienteId);
    long countByStatus(StatusPedido status);
    PedidoResponseDTO findLastByUser(String email);
    List<PedidoResponseDTO> findByClienteEmail(String email);
}