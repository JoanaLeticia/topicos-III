package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.SugestaoChefe;
import java.time.LocalDate;

public record SugestaoChefResponseDTO(
        Long id,
        LocalDate data,
        ItemCardapioResponseDTO itemAlmoco,
        ItemCardapioResponseDTO itemJantar
) {
    public static SugestaoChefResponseDTO valueOf(SugestaoChefe sugestao) {
        if (sugestao == null) {
            return null;
        }

        return new SugestaoChefResponseDTO(
                sugestao.getId(),
                sugestao.getData(),
                sugestao.getItemAlmoco() != null 
                    ? ItemCardapioResponseDTO.valueOf(sugestao.getItemAlmoco(), true)
                    : null,
                sugestao.getItemJantar() != null 
                    ? ItemCardapioResponseDTO.valueOf(sugestao.getItemJantar(), true)
                    : null
        );
    }
}
