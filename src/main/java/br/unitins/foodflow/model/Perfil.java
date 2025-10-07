package br.unitins.foodflow.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Perfil {
    ADMIN(1, "Administrador"), 
    CLIENTE(2, "Cliente");

    private final int ID;
    private final String NOME;

    Perfil(int id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public int getId() {
        return ID;
    }

    public String getNome() {
        return NOME;
    }

     public static Perfil valueOf(int id) {
        for (Perfil p : Perfil.values()) {
            if (p.getId() == id)
                return p;
        }
        return null;
     }
}

