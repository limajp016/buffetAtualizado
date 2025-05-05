package com.fateczl.BuffetRafaela.records;

import com.fateczl.BuffetRafaela.entities.Categoria;

import jakarta.validation.constraints.NotNull;

public record DadosCadastroItem(
    @NotNull String descricao,
    @NotNull Double valorCusto,
    @NotNull Double valorVenda,
    @NotNull String campoDesc,
    byte[] imagem,
    @NotNull Categoria categoria
) {}
