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

     public static TipoPeriodo fromString(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }
        
        try {
            return TipoPeriodo.valueOf(nome.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (TipoPeriodo tipo : values()) {
                if (tipo.getNome().equalsIgnoreCase(nome)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Período inválido: " + nome);
        }
    }
}
