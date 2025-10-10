package br.unitins.foodflow.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("DELIVERY_PROPRIO")
public class AtendimentoDeliveryProprio extends Atendimento {
    @ManyToOne
    @JoinColumn(name = "endereco_entrega_id")
    private Endereco enderecoEntrega;

    @Column(name = "taxa_entrega")
    private BigDecimal taxaEntrega = new BigDecimal("5.00");

    @Override
    public BigDecimal calcularTaxa() {
        return taxaEntrega;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

}
