package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;

public record DadosListagemPedido(
		Long id,
	    Long orcamentoId,
	    LocalDateTime dataEvento,
	    Double valorTotal) {
/*	public DadosListagemPedido(Pedido pedido) {
		this(pedido.getId(),
	            pedido.getOrcamento().getId(),
	            pedido.getDtHoraInicio(),
	            pedido.getValorTotal());
	}
	*/

}
