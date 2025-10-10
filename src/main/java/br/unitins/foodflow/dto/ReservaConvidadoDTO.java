package br.unitins.foodflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaConvidadoDTO(
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime dataHora,

    @NotNull
    Long idMesa,

    @NotNull
    Integer numeroPessoas,

    @NotBlank
    String nomeConvidado,

    @NotBlank @Email
    String emailConvidado,

    @NotBlank
    String telefoneConvidado
) {}