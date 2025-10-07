package br.unitins.foodflow.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusPedido {
    PENDENTE(1, "Pendente"),
    CONFIRMADO(2, "Confirmado"),
    EM_PREPARO(3, "Em preparo"),
    CONCLUIDO(4, "Concluído"),
    CANCELADO(5, "Cancelado");

    private final int ID;
    private final String NOME;

    StatusPedido(int id, String nome) {
        this.ID = id;
        this.NOME = nome;
    }

    public int getId() {
        return ID;
    }

    public String getNome() {
        return NOME;
    }

    public static StatusPedido valueOf(int id) {
        for (StatusPedido s : StatusPedido.values()) {
            if (s.getId() == id)
                return s;
        }
        return null;
    }
}
