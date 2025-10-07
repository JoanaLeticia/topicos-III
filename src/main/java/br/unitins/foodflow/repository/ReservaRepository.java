package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Reserva;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReservaRepository implements PanacheRepository<Reserva> {
    
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        return find("usuario.id = ?1 ORDER BY dataHora DESC", usuarioId).list();
    }

    public PanacheQuery<Reserva> findByUsuarioIdWithPagination(Long usuarioId) {
        return find("usuario.id = ?1 ORDER BY dataHora DESC", usuarioId);
    }

    public List<Reserva> findByDataHora(LocalDateTime dataHora) {
        return find("dataHora", dataHora).list();
    }

    public List<Reserva> findByMesaAndData(Long mesaId, LocalDateTime inicio, LocalDateTime fim) {
        return find("mesa.id = ?1 AND dataHora BETWEEN ?2 AND ?3", mesaId, inicio, fim).list();
    }

    public boolean existsReservaConflito(Long mesaId, LocalDateTime dataHora) {
        // Verifica se já existe reserva na mesma mesa em um intervalo de 2 horas
        LocalDateTime inicio = dataHora.minusHours(2);
        LocalDateTime fim = dataHora.plusHours(2);
        
        return count("mesa.id = ?1 AND dataHora BETWEEN ?2 AND ?3", mesaId, inicio, fim) > 0;
    }

    public Reserva findByCodigoConfirmacao(String codigo) {
        return find("codigoConfirmacao", codigo).firstResult();
    }

    public List<Reserva> findReservasFuturas(Long usuarioId) {
        return find("usuario.id = ?1 AND dataHora > ?2 ORDER BY dataHora ASC", 
                    usuarioId, LocalDateTime.now()).list();
    }
}