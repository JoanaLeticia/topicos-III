package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Telefone;

public record TelefoneResponseDTO(
        Long id,
        String codArea,
        String numero,
        Long clienteId) {
    public static TelefoneResponseDTO valueOf(Telefone telefone) {
        return new TelefoneResponseDTO(
                telefone.getId(),
                telefone.getCodArea(),
                telefone.getNumero(),
                telefone.getCliente() != null ? telefone.getCliente().getId() : null);
    }
}

