package br.unitins.foodflow.model;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("DELIVERY_APLICATIVO")
public class AtendimentoDeliveryAplicativo extends Atendimento {
    @ManyToOne
    @JoinColumn(name = "parceiro_id")
    private ParceiroApp parceiro;

    @ManyToOne
    @JoinColumn(name = "endereco_entrega_id")
    private Endereco enderecoEntrega;

    @Override
    public BigDecimal calcularTaxa() {
        return parceiro != null ? parceiro.calcularTaxa() : BigDecimal.ZERO;
    }

    public ParceiroApp getParceiro() {
        return parceiro;
    }

    public void setParceiro(ParceiroApp parceiro) {
        this.parceiro = parceiro;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }
}
