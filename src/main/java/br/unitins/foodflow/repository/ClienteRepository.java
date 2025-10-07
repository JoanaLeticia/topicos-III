package br.unitins.foodflow.repository;

import java.util.List;

import br.unitins.foodflow.model.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {
    public PanacheQuery<Cliente> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public long countByNome(String nome) {
        return count("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public Cliente findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public boolean existePorEmail(String email) {
        return count("email", email) > 0;
    }

    public Cliente findByEmailAndSenha(String email, String senha) {   
        return find("email = ?1 AND senha = ?2", email, senha).firstResult();
    }

    public List<Cliente> findByTelefoneId(Long telefoneId) {
        return find("SELECT c FROM Cliente c JOIN c.telefones t WHERE t.id = ?1", telefoneId).list();
    }

    public List<Cliente> findByEnderecoId(Long enderecoId) {
        return find("SELECT c FROM Cliente c JOIN c.enderecos e WHERE e.id = ?1", enderecoId).list();
    }
}
