package com.fateczl.BuffetRafaela.entities;

import java.util.ArrayList;
import java.util.List;

import com.fateczl.BuffetRafaela.records.DadosAtualizacaoCategoria;
import com.fateczl.BuffetRafaela.records.DadosCadastroCategoria;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "categoria")
@EqualsAndHashCode(of = "id")
public class Categoria {
	
	@Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "categoria_id")
    private Long id;
	
    @Column(name = "descricao")
    private String descricao;
    
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> itens = new ArrayList<>();
    
    public Categoria() {
    }

	public Categoria(Long id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
   
	
	public Categoria (DadosCadastroCategoria dados) {
		this.descricao = dados.descricao();
	}
	
	public void atualizarInformacoes(@Valid DadosAtualizacaoCategoria dados) {
		if (dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}
	
}
