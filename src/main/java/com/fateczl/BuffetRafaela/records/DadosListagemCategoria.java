package com.fateczl.BuffetRafaela.records;

import com.fateczl.BuffetRafaela.entities.Categoria;

public record DadosListagemCategoria(
		Long id,
		String descricao) {
	
	public DadosListagemCategoria(Categoria categoria) {
		this(categoria.getId(),
				categoria.getDescricao());
	}

}
