package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Administrador;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AdministradorRepository implements PanacheRepository<Administrador> {
    public PanacheQuery<Administrador> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public long countByNome(String nome) {
        return count("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public Administrador findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Administrador findByEmailAndSenha(String email, String senha) {
        return find("email = ?1 AND senha = ?2", email, senha).firstResult();
    }
}
