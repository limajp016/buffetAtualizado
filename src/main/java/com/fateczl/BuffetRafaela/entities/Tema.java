package com.fateczl.BuffetRafaela.entities;

import com.fateczl.BuffetRafaela.records.DadosAtualizacaoTema;
import com.fateczl.BuffetRafaela.records.DadosCadastroTema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
	@Lob
    @Column(name = "imagem", columnDefinition = "LONGBLOB")
    private byte[] imagem;
    @Transient
    private String imagemBase64;
	
	public Tema() {
		
	}
	
	public Tema(DadosCadastroTema dados) {
		this.descricao = dados.descricao();
		this.preco = dados.preco();
		this.imagem = dados.imagem();
	}

	public void atualizarInformacoes(@Valid DadosAtualizacaoTema dados) {
		if (dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
		if (dados.preco() != null) {
			this.preco = dados.preco();
		}
		if (dados.imagem() != null) {
			this.imagem = dados.imagem();
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

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public String getImagemBase64() {
		return imagemBase64;
	}

	public void setImagemBase64(String imagemBase64) {
		this.imagemBase64 = imagemBase64;
	}
	
}

