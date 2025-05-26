package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoCliente(
		@NotNull
		Long id,
		String nome,
		String cpf,
		String telefone,
		String email) {

}
