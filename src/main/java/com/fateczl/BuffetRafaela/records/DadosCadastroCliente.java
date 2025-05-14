package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DadosCadastroCliente(Long id,
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 123.456.789-01")
        String cpf,
        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (99) 99999-9999 ou (99) 9999-9999")
        String telefone,
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        String email) {
	
}
