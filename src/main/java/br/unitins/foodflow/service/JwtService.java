package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.UsuarioResponseDTO;

public interface JwtService {

    public String generateJwt(UsuarioResponseDTO dto);
    
}
