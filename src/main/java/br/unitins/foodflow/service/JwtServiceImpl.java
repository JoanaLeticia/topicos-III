package br.unitins.foodflow.service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import br.unitins.foodflow.dto.UsuarioResponseDTO;
import br.unitins.foodflow.model.Perfil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtServiceImpl implements JwtService {

    private static final Duration EXPIRATION_TIME = Duration.ofHours(24);

    @Override
    public String generateJwt(UsuarioResponseDTO dto) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(EXPIRATION_TIME);

        Set<String> roles = new HashSet<String>();
        if (dto.perfil() == Perfil.ADMIN) {
            roles.add("Admin");
        } else if (dto.perfil() == Perfil.CLIENTE) {
            roles.add("Cliente");
        }

        return Jwt.issuer("unitins-jwt")
            .subject(dto.email())
            .groups(roles)
            .expiresAt(expiryDate)
            .sign();

    }
    
}
