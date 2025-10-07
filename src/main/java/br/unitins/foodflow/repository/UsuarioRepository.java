package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    public PanacheQuery<Usuario> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public long countByNome(String nome) {
        return count("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public Usuario findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Usuario findByEmailAndSenha(String email, String senha) {
        try {
            return find("email = ?1 AND senha = ?2 ", email, senha).singleResult();
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        }
    }
}
