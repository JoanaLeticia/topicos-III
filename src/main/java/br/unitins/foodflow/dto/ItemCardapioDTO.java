package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemCardapioDTO(
        @JsonProperty(required = true) 
        @NotBlank(message = "O campo nome não pode ser nulo.") 
        String nome,
        
        @NotBlank(message = "O campo descrição não pode ser nulo.") 
        String descricao,
        
        @NotNull(message = "O campo preço não pode ser nulo.") 
        @Positive(message = "O preço deve ser positivo.") 
        BigDecimal precoBase,
        
        @NotNull(message = "O campo período não pode ser nulo.") 
        Integer idPeriodo // 1=ALMOCO, 2=JANTAR
) {}
