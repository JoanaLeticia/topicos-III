package br.unitins.foodflow.model;

import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sugestoes_chefe")
public class SugestaoChefe extends PanacheEntity {

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "item_almoco_id")
    private ItemCardapio itemAlmoco;

    @ManyToOne
    @JoinColumn(name = "item_jantar_id")
    private ItemCardapio itemJantar;

    public boolean isItemSugestao(ItemCardapio item, TipoPeriodo periodo) {
        if (periodo == TipoPeriodo.ALMOCO) {
            return itemAlmoco != null && itemAlmoco.id.equals(item.id);
        } else {
            return itemJantar != null && itemJantar.id.equals(item.id);
        }
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public ItemCardapio getItemAlmoco() {
        return itemAlmoco;
    }

    public void setItemAlmoco(ItemCardapio itemAlmoco) {
        this.itemAlmoco = itemAlmoco;
    }

    public ItemCardapio getItemJantar() {
        return itemJantar;
    }

    public void setItemJantar(ItemCardapio itemJantar) {
        this.itemJantar = itemJantar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
