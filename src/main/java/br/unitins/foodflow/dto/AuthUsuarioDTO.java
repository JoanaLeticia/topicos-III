package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Perfil;

public record AuthUsuarioDTO(
    String login,
    String senha,
    Perfil perfil
) {
}
