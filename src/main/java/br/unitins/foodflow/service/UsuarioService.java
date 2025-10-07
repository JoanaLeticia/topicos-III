package br.unitins.foodflow.service;

import java.util.List;

import br.unitins.foodflow.dto.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO updateNome(String email, String nome);
    void updateSenha(String login, String novaSenha, String senhaAtual);
    UsuarioResponseDTO findById(long id);
    UsuarioResponseDTO findByEmail(String email);
    UsuarioResponseDTO findByEmailAndSenha(String email, String senha);
    List<UsuarioResponseDTO> findAll(int page, int pageSize, String sort);
    List<UsuarioResponseDTO> findByNome(String nome, int page, int pageSize, String sort);
    long count();
    long count(String nome);
}
