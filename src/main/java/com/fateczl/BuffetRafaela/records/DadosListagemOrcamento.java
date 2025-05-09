package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

public record DadosListagemOrcamento(
		Long id,
		Cliente cliente,
		Tema tema,
		List<Item> itens,
		LocalDateTime dtHoraInicio,
		StatusOrcamento status,
		Double valorTotal,
		String logradouro,
		String bairro,
		String cidade,
		String uf,
		String cep) {
	
	public DadosListagemOrcamento(Orcamento orcamento) {
		this(orcamento.getId(),
				orcamento.getCliente(),
				orcamento.getTema(),
				orcamento.getItens(),
				orcamento.getDtHoraInicio(),
				orcamento.getStatus(),
				orcamento.getValorTotal(),
				orcamento.getLogradouro(),
				orcamento.getBairro(),
				orcamento.getCidade(),
				orcamento.getUf(),
				orcamento.getCep());
	}

}
