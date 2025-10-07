package br.unitins.foodflow.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoAtendimento {
    PRESENCIAL(1, "Presencial"),
    DELIVERY_PROPRIO(2, "Delivery Próprio"),
    DELIVERY_APLICATIVO(3, "Delivery pelo Aplicativo");

    private final int ID;
    private final String NOME;

    TipoAtendimento(int id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public int getId() {
        return ID;
    }

    public String getNome() {
        return NOME;
    }

    public static TipoAtendimento valueOf(int id) {
        for (TipoAtendimento a : TipoAtendimento.values()) {
            if (a.getId() == id)
                return a;
        }
        return null;
    }
}
