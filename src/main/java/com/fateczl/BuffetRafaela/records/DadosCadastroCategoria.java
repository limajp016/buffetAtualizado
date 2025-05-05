package com.fateczl.BuffetRafaela.records;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroCategoria(
		@NotNull String descricao) {

}
