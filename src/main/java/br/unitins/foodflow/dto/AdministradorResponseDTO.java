package br.unitins.foodflow.dto;

import java.util.Date;

import br.unitins.foodflow.model.Administrador;

public record AdministradorResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        Date dataNascimento) {
    public static AdministradorResponseDTO valueOf(Administrador adm) {
        return new AdministradorResponseDTO(
                adm.getId(),
                adm.getNome(),
                adm.getEmail(),
                adm.getCpf(),
                adm.getDataNascimento());
    }
}
