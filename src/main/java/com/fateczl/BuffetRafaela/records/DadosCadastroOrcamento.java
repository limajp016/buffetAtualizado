package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DadosCadastroOrcamento(
	    @NotNull Long clienteId,
	    @NotNull Long temaId,
	    @NotNull List<ItemQuantidade> itens,
	    @NotNull @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime dtHoraInicio,
	    @NotNull StatusOrcamento status,
	    @NotBlank String logradouro,
	    @NotBlank String numero,
	    @NotBlank String bairro,
	    @NotBlank String cidade,
	    @NotNull Estados uf,
	    @NotBlank String cep,
	    String complemento
	) {
	    public record ItemQuantidade(
	        @NotNull Long itemId,
	        @NotNull @Positive Integer quantidade
	    ) {}
	}