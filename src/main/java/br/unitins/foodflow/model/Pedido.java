package br.unitins.foodflow.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido extends PanacheEntity {
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "atendimento_id")
    private Atendimento atendimento;

    @ManyToMany
    @JoinTable(name = "pedido_item", joinColumns = @JoinColumn(name = "pedido_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<ItemCardapio> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(nullable = false, name = "data_pedido")
    private LocalDateTime dataPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private TipoPeriodo periodo;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    public BigDecimal calcularValorTotal(SugestaoChefe sugestaoChefe) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemCardapio item : itens) {
            if (sugestaoChefe != null &&
                    sugestaoChefe.getData().equals(dataPedido.toLocalDate()) &&
                    sugestaoChefe.isItemSugestao(item, periodo)) {
                subtotal = subtotal.add(item.calcularPrecoComDesconto());
            } else {
                subtotal = subtotal.add(item.getPrecoBase());
            }
        }

        BigDecimal taxa = atendimento != null ? atendimento.calcularTaxa() : BigDecimal.ZERO;
        this.valorTotal = subtotal.add(taxa);
        return this.valorTotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Atendimento getAtendimento() {
        return atendimento;
    }

    public void setAtendimento(Atendimento atendimento) {
        this.atendimento = atendimento;
    }

    public List<ItemCardapio> getItens() {
        return itens;
    }

    public void setItens(List<ItemCardapio> itens) {
        this.itens = itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }

    public TipoPeriodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(TipoPeriodo periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }


}
