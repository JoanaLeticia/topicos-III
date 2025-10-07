package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.SugestaoChefe;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class SugestaoChefRepository implements PanacheRepository<SugestaoChefe> {
    
    public SugestaoChefe findByData(LocalDate data) {
        return find("data", data).firstResult();
    }

    public SugestaoChefe findSugestaoAtiva() {
        return findByData(LocalDate.now());
    }

    public boolean existsByData(LocalDate data) {
        return count("data", data) > 0;
    }

    public void deletarSugestoesAntigas(LocalDate dataLimite) {
        delete("data < ?1", dataLimite);
    }
}