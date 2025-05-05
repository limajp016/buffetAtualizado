package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroOrcamento(
    @NotNull Cliente cliente,
    @NotNull Tema tema,
    @NotNull List<Item> itens,
    @NotNull LocalDateTime dtHoraInicio,
    @NotNull StatusOrcamento status,
    @NotNull Double valorTotal,
    @NotBlank String logradouro,
    @NotBlank String bairro,
    @NotBlank String cidade,
    @NotBlank String uf,
    @NotBlank String cep
) {}
