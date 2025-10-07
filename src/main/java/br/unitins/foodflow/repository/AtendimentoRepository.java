package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Atendimento;
import br.unitins.foodflow.model.TipoAtendimento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AtendimentoRepository implements PanacheRepository<Atendimento> {
    
    public List<Atendimento> findByTipo(TipoAtendimento tipo) {
        return find("tipo", tipo).list();
    }

    public long countByTipo(TipoAtendimento tipo) {
        return count("tipo", tipo);
    }
}