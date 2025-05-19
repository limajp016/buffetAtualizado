package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoOrcamento(
    @NotNull Long id,
    Cliente cliente,
    Tema tema,
    List<ItemQuantidade> itens,
    LocalDateTime dtHoraInicio,
    StatusOrcamento status,
    String logradouro,
    String bairro,
    String numero,
    String cidade,
    Estados uf,
    String cep,
    String complemento
) {}