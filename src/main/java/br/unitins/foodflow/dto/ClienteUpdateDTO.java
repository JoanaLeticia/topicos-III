package br.unitins.foodflow.dto;

import java.util.List;

public record ClienteUpdateDTO(
    String nome,
    List<TelefoneDTO> listaTelefone
) {}
