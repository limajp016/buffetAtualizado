package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroTema(
		@NotNull String descricao,
		@NotNull Double preco,
		@NotNull byte[] imagem) {

}
