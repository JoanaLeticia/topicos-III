package br.unitins.foodflow.dto;

import java.util.Collections;
import java.util.List;

import br.unitins.foodflow.model.Cliente;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String login,
        List<TelefoneResponseDTO> listaTelefone,
        List<EnderecoResponseDTO> listaEndereco) {
    public static ClienteResponseDTO valueOf(Cliente cliente) {
        if (cliente == null) {
            return new ClienteResponseDTO(null, null, null, null, null);
        }

        List<TelefoneResponseDTO> listaTelefones = cliente.getTelefones()
                .stream()
                .map(TelefoneResponseDTO::valueOf)
                .toList();

        List<EnderecoResponseDTO> listaEnderecos = cliente.getEnderecos() != null
                ? cliente.getEnderecos().stream()
                        .map(EnderecoResponseDTO::valueOf)
                        .toList()
                : Collections.emptyList();
                
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                listaTelefones,
                listaEnderecos);
    }
}
