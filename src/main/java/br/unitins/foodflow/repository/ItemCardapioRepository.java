package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.ItemCardapio;
import br.unitins.foodflow.model.TipoPeriodo;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ItemCardapioRepository implements PanacheRepository<ItemCardapio> {
    
    public PanacheQuery<ItemCardapio> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public long countByNome(String nome) {
        return count("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%");
    }

    public List<ItemCardapio> findByPeriodo(TipoPeriodo periodo) {
        return find("periodo", periodo).list();
    }

    public PanacheQuery<ItemCardapio> findByPeriodoWithPagination(TipoPeriodo periodo) {
        return find("periodo", periodo);
    }

    public List<ItemCardapio> findAllOrderByNome() {
        return find("ORDER BY nome ASC").list();
    }

    public List<ItemCardapio> findByPeriodoOrderByNome(TipoPeriodo periodo) {
        return find("periodo = ?1 ORDER BY nome ASC", periodo).list();
    }

    public List<Double> findItemPrecoByPeriodo(TipoPeriodo periodo) {
        return List.of(20.0, 30.0, 50.0, 75.0, 100.0);
    }

    public Object countByPeriodo(TipoPeriodo periodo) {
        return count("periodo = ?1", periodo);
    }
}