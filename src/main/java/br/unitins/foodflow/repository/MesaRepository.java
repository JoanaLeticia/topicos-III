package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Mesa;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class MesaRepository implements PanacheRepository<Mesa> {
    
    public Mesa findByNumero(Integer numero) {
        return find("numero", numero).firstResult();
    }

    public List<Mesa> findByCapacidadeMinima(Integer capacidade) {
        return find("capacidade >= ?1 ORDER BY capacidade ASC", capacidade).list();
    }

    public List<Mesa> findMesasDisponiveis(LocalDateTime dataHora, Integer capacidadeMinima) {
        // Busca mesas que não têm reserva no horário solicitado
        return getEntityManager()
            .createQuery(
                "SELECT m FROM Mesa m " +
                "WHERE m.capacidade >= :capacidade " +
                "AND m.id NOT IN (" +
                "   SELECT r.mesa.id FROM Reserva r " +
                "   WHERE r.dataHora BETWEEN :inicio AND :fim" +
                ") ORDER BY m.capacidade ASC",
                Mesa.class)
            .setParameter("capacidade", capacidadeMinima)
            .setParameter("inicio", dataHora.minusHours(2))
            .setParameter("fim", dataHora.plusHours(2))
            .getResultList();
    }

    public List<Mesa> findAllOrderByNumero() {
        return find("ORDER BY numero ASC").list();
    }
}