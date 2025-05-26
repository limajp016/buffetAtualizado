package com.fateczl.BuffetRafaela.entities;

import com.fateczl.BuffetRafaela.records.DadosAtualizacaoTema;
import com.fateczl.BuffetRafaela.records.DadosCadastroTema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="tema")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class Tema {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(name="descricao")
	private String descricao;
	@Column(name="preco")
	private Double preco;
	
	public Tema() {
		
	}
	
	public Tema(DadosCadastroTema dados) {
		this.descricao = dados.descricao();
		this.preco = dados.preco();
	}

	public void atualizarInformacoes(@Valid DadosAtualizacaoTema dados) {
		if (dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
		if (dados.preco() != null) {
			this.preco = dados.preco();
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

	public Double getPreco() {
		return preco;
	}

	public void setPreco(Double preco) {
		this.preco = preco;
	}
	
}

