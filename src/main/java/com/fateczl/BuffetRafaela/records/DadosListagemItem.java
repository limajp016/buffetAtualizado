package com.fateczl.BuffetRafaela.records;

import com.fateczl.BuffetRafaela.entities.Categoria;
import com.fateczl.BuffetRafaela.entities.Item;

public record DadosListagemItem(
        Long id,
        String descricao,
        Double valorCusto,  
        Double valorVenda,
        String campoDesc,
        byte[] imagem,
        Categoria categoria) {
    
    public DadosListagemItem(Item item) {
        this(item.getId(), 
             item.getDescricao(), 
             item.getValorCusto(),
             item.getValorVenda(),
             item.getCampoDesc(),
             item.getImagem(),
             item.getCategoria());
    }
}
