package com.fateczl.BuffetRafaela.records;

public record DadosCadastroPedido(
    Long orcamentoId
) {
	
    public DadosCadastroPedido {
        if (orcamentoId == null) {
            throw new IllegalArgumentException("ID do orçamento é obrigatório");
        }
    }
}