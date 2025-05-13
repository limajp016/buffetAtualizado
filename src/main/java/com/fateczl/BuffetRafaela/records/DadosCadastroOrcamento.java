package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroOrcamento(
    @NotNull Cliente cliente,
    @NotNull Tema tema,
    @NotEmpty List<ItemQuantidade> itens,
    @NotNull LocalDateTime dtHoraInicio,
    @NotNull StatusOrcamento status,
    @NotBlank String logradouro,
    @NotBlank String bairro,
    @NotBlank String cidade,
    @NotBlank String uf,
    @NotBlank String cep
) {}