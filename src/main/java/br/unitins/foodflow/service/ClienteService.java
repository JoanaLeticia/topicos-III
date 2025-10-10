package br.unitins.foodflow.service;

import java.util.List;

import br.unitins.foodflow.dto.ClienteDTO;
import br.unitins.foodflow.dto.ClienteResponseDTO;
import br.unitins.foodflow.dto.ClienteUpdateDTO;
import br.unitins.foodflow.dto.EnderecoResponseDTO;
import br.unitins.foodflow.dto.UsuarioResponseDTO;

public interface ClienteService {
    ClienteResponseDTO create(ClienteDTO cliente);
    ClienteResponseDTO update(ClienteDTO clienteDTO, Long id);
    void updatePartial(ClienteUpdateDTO dto, Long id);
    void delete(long id);
    public UsuarioResponseDTO registrar(ClienteDTO clienteDTO);
    ClienteResponseDTO findById(long id);
    List<ClienteResponseDTO> findAll(int page, int size);
    List<ClienteResponseDTO> findByNome(String nome, int page, int size, String sort);
    long count();
    long count(String nome);
    public UsuarioResponseDTO login(String email, String senha);
    ClienteResponseDTO findByEmail(String email);
    List<EnderecoResponseDTO> findEnderecosByEmail(String email);
}
