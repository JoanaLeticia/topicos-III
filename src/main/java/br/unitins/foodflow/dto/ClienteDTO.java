package br.unitins.foodflow.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(
        @NotBlank(message = "O campo nome não pode ser nulo.")
        String nome,
        @NotBlank(message = "O campo email não pode ser nulo.")
        @Email(message = "O campo email não esta no formato correto!")
        String email,
        @NotBlank(message = "O campo senha não pode ser nulo.")
        String senha,
        List<TelefoneDTO> listaTelefone,
        List<EnderecoDTO> listaEndereco) {

}

