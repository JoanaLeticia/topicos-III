package br.unitins.foodflow.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoPeriodo {
    ALMOCO(1, "Almoço"), 
    JANTAR(2, "Jantar");

    private final int ID;
    private final String NOME;

    TipoPeriodo(int id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public int getId() {
        return ID;
    }

    public String getNome() {
        return NOME;
    }

     public static TipoPeriodo valueOf(int id) {
        for (TipoPeriodo p : TipoPeriodo.values()) {
            if (p.getId() == id)
                return p;
        }
        return null;
     }
}
