package br.unitins.foodflow.dto;

import br.unitins.foodflow.model.Reserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        String nomeReservante,
        String emailReservante,
        String telefoneReservante,
        MesaResponseDTO mesa,
        LocalDateTime dataHora,
        Integer numeroPessoas,
        String codigoConfirmacao
) {
    public static ReservaResponseDTO valueOf(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        String nome = null;
        String email = null;
        String telefone = null;

        if (reserva.getUsuario() != null) {
            nome = reserva.getUsuario().getNome();
            email = reserva.getUsuario().getEmail();
            if (reserva.getUsuario() instanceof br.unitins.foodflow.model.Cliente &&
                !((br.unitins.foodflow.model.Cliente) reserva.getUsuario()).getTelefones().isEmpty()) {
                telefone = ((br.unitins.foodflow.model.Cliente) reserva.getUsuario()).getTelefones().get(0).getNumeroCompleto();
            }
        } 
        else {
            nome = reserva.getNomeConvidado();
            email = reserva.getEmailConvidado();
            telefone = reserva.getTelefoneConvidado();
        }

        return new ReservaResponseDTO(
                reserva.getId(),
                nome,
                email,
                telefone,
                MesaResponseDTO.valueOf(reserva.getMesa()),
                reserva.getDataHora(),
                reserva.getNumeroPessoas(),
                reserva.getCodigoConfirmacao()
        );
    }
}