package br.unitins.foodflow.model;

import java.math.BigDecimal;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "itens_cardapio")
public class ItemCardapio extends PanacheEntity {

    @Column(nullable = false)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false, name = "preco_base")
    private BigDecimal precoBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPeriodo periodo;

    @Column(name = "nome_imagem")
    private String nomeImagem;

    public String getNomeImagem() {
        return nomeImagem;
    }

    public void setNomeImagem(String nomeImagem) {
        this.nomeImagem = nomeImagem;
    }

    public BigDecimal calcularPrecoComDesconto() {
        return precoBase.multiply(new BigDecimal("0.80"));
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public TipoPeriodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(TipoPeriodo periodo) {
        this.periodo = periodo;
    }

}
