package br.unitins.foodflow.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(
        @NotEmpty(message = "O campo email não pode ser nulo.")
        String email,
        @NotEmpty(message = "O campo senha não pode ser nulo.")
        String senha) {
}
