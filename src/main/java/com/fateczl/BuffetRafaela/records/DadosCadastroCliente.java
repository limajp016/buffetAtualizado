package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroCliente(
		@NotNull String nome,
		@NotNull String cpf,
		@NotNull String telefone,
		@NotNull String email) {
	
}
