package br.unitins.foodflow.service;

import java.util.List;

import br.unitins.foodflow.dto.AdministradorDTO;
import br.unitins.foodflow.dto.AdministradorResponseDTO;
import br.unitins.foodflow.dto.UsuarioResponseDTO;

public interface AdministradorService {
    AdministradorResponseDTO create(AdministradorDTO administrador);
    AdministradorResponseDTO update(AdministradorDTO administradorDTO, Long id);
    void delete(long id);
    AdministradorResponseDTO findById(long id);
    List<AdministradorResponseDTO> findAll(int page, int pageSize);
    List<AdministradorResponseDTO> findByNome(String nome, int page, int size, String sort);
    long count();
    long count(String nome);
    public UsuarioResponseDTO login(String email, String senha);
}