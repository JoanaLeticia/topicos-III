package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Mesa;

public record MesaResponseDTO(
        Long id,
        Integer numero,
        Integer capacidade
) {
    public static MesaResponseDTO valueOf(Mesa mesa) {
        if (mesa == null) {
            return null;
        }

        return new MesaResponseDTO(
                mesa.id,
                mesa.getNumero(),
                mesa.getCapacidade()
        );
    }
}
