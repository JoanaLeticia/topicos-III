package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Reserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        UsuarioResponseDTO usuario,
        MesaResponseDTO mesa,
        LocalDateTime dataHora,
        Integer numeroPessoas,
        String codigoConfirmacao
) {
    public static ReservaResponseDTO valueOf(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        return new ReservaResponseDTO(
                reserva.id,
                UsuarioResponseDTO.valueOf(reserva.getUsuario()),
                MesaResponseDTO.valueOf(reserva.getMesa()),
                reserva.getDataHora(),
                reserva.getNumeroPessoas(),
                reserva.getCodigoConfirmacao()
        );
    }
}
