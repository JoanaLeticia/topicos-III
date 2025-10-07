package br.unitins.foodflow.dto;

import java.util.Date;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdministradorDTO(
        @NotBlank(message = "Digite o seu nome.")
        String nome,
        @NotBlank(message = "Digite o seu e-mail.")
        @Email(message = "O e-mail fornecido não está formatado corretamente!")
        String email,
        @NotBlank(message = "Digite a senha.")
        String senha,
        @NotBlank(message = "Digite o seu CPF.")
        @Pattern(regexp = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$", message = "CPF não está no formato correto!")
        String cpf,
        Date dataNascimento){
}

