package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoTema(
		@NotNull
		Long id,
		String descricao,
		Double preco){

}
