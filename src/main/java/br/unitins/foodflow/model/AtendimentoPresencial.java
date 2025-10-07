package br.unitins.foodflow.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PRESENCIAL")
public class AtendimentoPresencial extends Atendimento {

    @Column(name = "numero_mesa")
    private Integer numeroMesa;

    @Override
    public BigDecimal calcularTaxa() {
        return BigDecimal.ZERO;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

}
