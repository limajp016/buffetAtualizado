package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoCategoria(
		@NotNull Long id,
		String descricao) {

}
