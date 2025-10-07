package br.unitins.foodflow.service;

public interface HashService {
    String getHashSenha(String senha);
    boolean verificarSenha(String senhaTextoPuro, String senhaHash);
}
