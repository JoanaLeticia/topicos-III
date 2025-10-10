package br.unitins.foodflow.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sugestoes_chefe")
public class SugestaoChefe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
            return itemAlmoco != null && itemAlmoco.getId().equals(item.getId());
        } else {
            return itemJantar != null && itemJantar.getId().equals(item.getId());
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
