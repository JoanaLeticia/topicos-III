package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SugestaoChefDTO(
        @JsonProperty(required = true) 
        @NotNull(message = "O campo data não pode ser nulo.") 
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate data,
        
        Long idItemAlmoco,
        
        Long idItemJantar
) {}
