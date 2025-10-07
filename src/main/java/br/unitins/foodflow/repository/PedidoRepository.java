package br.unitins.foodflow.repository;

import br.unitins.foodflow.model.Cliente;
import br.unitins.foodflow.model.Pedido;
import br.unitins.foodflow.model.StatusPedido;
import br.unitins.foodflow.model.TipoAtendimento;
import br.unitins.foodflow.model.TipoPeriodo;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PedidoRepository implements PanacheRepository<Pedido> {

    public List<Pedido> findByClienteId(Long clienteId) {
        return find("cliente.id = ?1 ORDER BY dataPedido DESC", clienteId).list();
    }

    public PanacheQuery<Pedido> findByClienteIdWithPagination(Long clienteId) {
        return find("cliente.id = ?1 ORDER BY dataPedido DESC", clienteId);
    }

    public List<Pedido> findByStatus(StatusPedido status) {
        return find("status = ?1 ORDER BY dataPedido DESC", status).list();
    }

    public List<Pedido> findByDataPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return find("dataPedido BETWEEN ?1 AND ?2 ORDER BY dataPedido DESC", inicio, fim).list();
    }

    public boolean existsByEnderecoIdAndStatusIn(Long enderecoId, List<StatusPedido> statusList) {
        return count("endereco.id = ?1 AND status IN ?2", enderecoId, statusList) > 0;
    }

    public List<Pedido> findByUsuario(Cliente cliente) {
        return find("cliente = ?1", cliente).list();
    }

    public List<Pedido> findByTipoAtendimentoAndPeriodo(TipoAtendimento tipoAtendimento,
            LocalDateTime inicio,
            LocalDateTime fim) {
        return find("atendimento.tipo = ?1 AND dataPedido BETWEEN ?2 AND ?3 ORDER BY dataPedido DESC",
                tipoAtendimento, inicio, fim).list();
    }

    // Para relatórios
    public List<Object[]> findFaturamentoPorTipoAtendimento(LocalDateTime inicio, LocalDateTime fim) {
        return getEntityManager()
                .createQuery(
                        "SELECT a.tipo, SUM(p.valorTotal), COUNT(p) " +
                                "FROM Pedido p JOIN p.atendimento a " +
                                "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
                                "GROUP BY a.tipo",
                        Object[].class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<Object[]> findItensMaisVendidos(LocalDateTime inicio, LocalDateTime fim, int limit) {
        return getEntityManager()
                .createQuery(
                        "SELECT i.nome, COUNT(i), SUM(i.precoBase) " +
                                "FROM Pedido p JOIN p.itens i " +
                                "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
                                "GROUP BY i.id, i.nome " +
                                "ORDER BY COUNT(i) DESC",
                        Object[].class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Object[]> findItensMaisVendidosPorPeriodo(TipoPeriodo periodo,
            LocalDateTime inicio,
            LocalDateTime fim,
            int limit) {
        return getEntityManager()
                .createQuery(
                        "SELECT i.nome, COUNT(i), SUM(i.precoBase) " +
                                "FROM Pedido p JOIN p.itens i " +
                                "WHERE p.periodo = :periodo AND p.dataPedido BETWEEN :inicio AND :fim " +
                                "GROUP BY i.id, i.nome " +
                                "ORDER BY COUNT(i) DESC",
                        Object[].class)
                .setParameter("periodo", periodo)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setMaxResults(limit)
                .getResultList();
    }

    public long countByClienteId(Long clienteId) {
        return count("cliente.id", clienteId);
    }
}