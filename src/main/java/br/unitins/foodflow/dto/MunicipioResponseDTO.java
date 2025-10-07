package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Municipio;

public record MunicipioResponseDTO(
        Long id,
        String nome,
        EstadoResponseDTO estado ) {
    public static MunicipioResponseDTO valueOf(Municipio municipio) {
        return new MunicipioResponseDTO(
            municipio.getId(), municipio.getNome(),
            EstadoResponseDTO.valueOf(municipio.getEstado()));
    }
}
