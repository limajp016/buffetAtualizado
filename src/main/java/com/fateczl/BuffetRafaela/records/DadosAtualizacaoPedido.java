package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;

import com.fateczl.BuffetRafaela.entities.Orcamento;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoPedido(@NotNull Long id,
		Orcamento orcamento,
		Double valorTotal,
		LocalDateTime dtHoraInicio) {

}
