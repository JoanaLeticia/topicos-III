package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Perfil;
import br.unitins.foodflow.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Perfil perfil) {

    public static UsuarioResponseDTO valueOf(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil());
    }

}
