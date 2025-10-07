package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ParceiroAppDTO(
        @JsonProperty(required = true) 
        @NotBlank(message = "O campo nome não pode ser nulo.") 
        String nome,
        
        @NotNull(message = "O campo percentual de comissão não pode ser nulo.") 
        @Positive(message = "O percentual deve ser positivo.") 
        BigDecimal percentualComissao,
        
        BigDecimal taxaFixa
) {}
