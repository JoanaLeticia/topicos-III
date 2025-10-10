package br.unitins.foodflow.dto;

import java.util.List;

public record DisponibilidadeMesaResponse(
    MesaResponseDTO mesa,
    List<String> horariosDisponiveis
) {}