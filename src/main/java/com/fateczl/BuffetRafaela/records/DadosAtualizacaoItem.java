package com.fateczl.BuffetRafaela.records;

import com.fateczl.BuffetRafaela.entities.Categoria;

public record DadosAtualizacaoItem(
    String descricao,
    Double valorCusto,
    Double valorVenda,
    String campoDesc,
    byte[] imagem,
    Categoria categoria
) {}
