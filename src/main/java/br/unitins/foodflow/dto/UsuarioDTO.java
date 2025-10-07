package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.unitins.foodflow.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioDTO(
        @JsonProperty(required = true) @NotBlank(message = "O campo email não pode ser nulo.") String email,
        @NotBlank(message = "O campo senha não pode ser nulo.") String senha,
        @NotNull(message = "O campo perfil não pode ser nulo.") Integer idPerfil,
        String nome,
        Usuario usuario) {
}
