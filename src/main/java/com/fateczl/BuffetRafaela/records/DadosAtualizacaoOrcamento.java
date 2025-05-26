package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoOrcamento(@NotNull Long id,
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

}
