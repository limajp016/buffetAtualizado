package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Pedido;

public record DadosListagemPedido(
		Long id,
		Orcamento orcamento,
		Double valorTotal,
		LocalDateTime dtHoraInicio) {
	public DadosListagemPedido(Pedido pedido) {
		this(pedido.getId(),
				pedido.getOrcamento(),
				pedido.getValorTotal(),
				pedido.getDtHoraInicio());
	}

}
