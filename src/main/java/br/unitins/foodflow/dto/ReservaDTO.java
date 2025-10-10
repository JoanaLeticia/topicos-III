package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ReservaDTO(
        @JsonProperty(required = true) 
        @NotNull(message = "O campo data/hora não pode ser nulo.") 
        @Future(message = "A reserva deve ser para uma data futura.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHora,
        
        @NotNull(message = "O campo mesa não pode ser nulo.") 
        Long idMesa,
        
        @NotNull(message = "O número de pessoas não pode ser nulo.") 
        @Positive(message = "O número de pessoas deve ser positivo.") 
        Integer numeroPessoas
) {}
