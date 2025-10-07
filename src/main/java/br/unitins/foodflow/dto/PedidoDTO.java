package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoDTO(
        @JsonProperty(required = true) 
        @NotNull(message = "O campo período não pode ser nulo.") 
        Integer idPeriodo, // 1=ALMOCO, 2=JANTAR
        
        @NotEmpty(message = "O pedido deve conter ao menos um item.") 
        List<Long> idsItens,
        
        @NotNull(message = "O campo tipo de atendimento não pode ser nulo.") 
        Integer idTipoAtendimento, // 1=PRESENCIAL, 2=DELIVERY_PROPRIO, 3=DELIVERY_APLICATIVO
        
        // Campos específicos por tipo de atendimento
        Integer numeroMesa, // Para PRESENCIAL
        Long idEndereco, // Para DELIVERY
        Long idParceiro // Para DELIVERY_APLICATIVO
) {}
