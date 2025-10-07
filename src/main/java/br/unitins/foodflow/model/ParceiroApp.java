package br.unitins.foodflow.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "parceiros_app")
public class ParceiroApp extends PanacheEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, name = "percentual_comissao")
    private BigDecimal percentualComissao; // ex: 15 (para 15%)

    @Column(name = "taxa_fixa")
    private BigDecimal taxaFixa; // Opcional

    @OneToMany(mappedBy = "parceiro")
    private List<AtendimentoDeliveryAplicativo> atendimentos = new ArrayList<>();

    public BigDecimal calcularTaxa() {
        // Você pode implementar lógica mais complexa aqui
        return taxaFixa != null ? taxaFixa : new BigDecimal("3.00");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(BigDecimal percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public BigDecimal getTaxaFixa() {
        return taxaFixa;
    }

    public void setTaxaFixa(BigDecimal taxaFixa) {
        this.taxaFixa = taxaFixa;
    }

    public List<AtendimentoDeliveryAplicativo> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(List<AtendimentoDeliveryAplicativo> atendimentos) {
        this.atendimentos = atendimentos;
    }


}
