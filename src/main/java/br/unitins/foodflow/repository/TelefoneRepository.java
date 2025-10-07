package br.unitins.foodflow.repository;

import java.util.List;

import br.unitins.foodflow.model.Telefone;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TelefoneRepository implements PanacheRepository<Telefone> {
    public List<Telefone> findByNumero(String numero) {
        return find("UPPER(numero) LIKE UPPER(?1) ", "%" + numero + "%").list();
    }

    public long countByNumero(String numero) {
        return count("UPPER(numero) LIKE UPPER(?1)", "%" + numero + "%");
    }
}
