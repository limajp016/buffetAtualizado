package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DadosAtualizacaoCliente(
		 @NotNull Long id,
	        String nome,
	        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 123.456.789-01")
	        String cpf,
	        @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (99) 99999-9999 ou (99) 9999-9999")
	        String telefone,
	        @Email(message = "O email deve ser válido")
	        String email) {

}
