package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;

import com.fateczl.BuffetRafaela.entities.Orcamento;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroPedido(
		@NotNull Orcamento orcamento,
		@NotNull Double valorTotal,
		@NotNull LocalDateTime dtHoraInicio
		) {

}
