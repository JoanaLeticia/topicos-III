package br.unitins.foodflow.dto;

import jakarta.validation.constraints.NotBlank;

public record TelefoneDTO(
        Long id,
        @NotBlank(message = "O campo Codigo de Area não pode ser nulo.")
        String codArea,
        @NotBlank(message = "O campo numero não pode ser nulo.")
        String numero,
        Long idCliente
) {
}