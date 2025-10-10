package br.unitins.foodflow.service;

import br.unitins.foodflow.dto.SugestaoChefDTO;
import br.unitins.foodflow.dto.SugestaoChefResponseDTO;

import java.time.LocalDate;

public interface SugestaoChefService {
    SugestaoChefResponseDTO create(SugestaoChefDTO dto);
    SugestaoChefResponseDTO update(SugestaoChefDTO dto, Long id);
    void delete(Long id);
    SugestaoChefResponseDTO findById(Long id);
    SugestaoChefResponseDTO findByData(LocalDate data);
    SugestaoChefResponseDTO findSugestaoAtiva();
    void deletarSugestoesAntigas(int diasAtras);
}