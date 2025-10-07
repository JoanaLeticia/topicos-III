package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.ParceiroApp;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParceiroAppRepository implements PanacheRepository<ParceiroApp> {
    
    public PanacheQuery<ParceiroApp> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public long countByNome(String nome) {
        return count("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public ParceiroApp findByNomeExato(String nome) {
        return find("UPPER(nome) = UPPER(?1)", nome).firstResult();
    }
}