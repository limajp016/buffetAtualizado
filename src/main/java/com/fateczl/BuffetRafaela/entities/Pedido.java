package com.fateczl.BuffetRafaela.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@Getter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pedido_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "preco_tema", nullable = false)
    private Double precoTema;

    @Column(name = "descricao_tema", nullable = false)
    private String descricaoTema;

    @Lob
    @Column(name = "imagem_tema", columnDefinition = "LONGBLOB")
    private byte[] imagemTema;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<ItemPedido> itens;

    @Column(name = "dt_hr_inicio", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dtHoraInicio;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "bairro", nullable = false)
    private String bairro;

    @Column(name = "cidade", nullable = false)
    private String cidade;

    @Column(name = "uf", nullable = false)
    private Estados uf;

    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @Column(name = "complemento")
    private String complemento;
    
    public Pedido() {
    	
    }

    public Pedido(Orcamento orcamento) {
        if (orcamento == null || orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new IllegalArgumentException("O orçamento deve ser válido e estar aprovado.");
        }

        this.id = null;
        this.orcamento = orcamento;
        this.nomeCliente = orcamento.getCliente() != null ? orcamento.getCliente().getNome() : "";
        this.precoTema = orcamento.getTema() != null ? orcamento.getTema().getPreco() : 0.0;
        this.descricaoTema = orcamento.getTema() != null ? orcamento.getTema().getDescricao() : "";
        this.imagemTema = orcamento.getTema() != null ? orcamento.getTema().getImagem() : null;
        this.itens = orcamento.getItens() != null 
        	    ? orcamento.getItens().stream()
        	        .map(itemOrcamento -> new ItemPedido(
        	            itemOrcamento.getItem().getDescricao(),
        	            itemOrcamento.getItem().getValorVenda(),
        	            itemOrcamento.getQuantidade(),
        	            itemOrcamento.getPrecoUnitario(),
        	            itemOrcamento.getItem().getImagem(),
        	            this))
        	        .collect(Collectors.toList())
        	    : new ArrayList<>();

        this.dtHoraInicio = orcamento.getDtHoraInicio();
        this.valorTotal = orcamento.getValorTotal();
        this.logradouro = orcamento.getLogradouro();
        this.numero = orcamento.getNumero();
        this.bairro = orcamento.getBairro();
        this.cidade = orcamento.getCidade();
        this.uf = orcamento.getUf();
        this.cep = orcamento.getCep();
        this.complemento = orcamento.getComplemento();
    }
    
    public Long getId() {
		return id;
	}

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public Double getPrecoTema() {
		return precoTema;
	}

	public String getDescricaoTema() {
		return descricaoTema;
	}

	public byte[] getImagemTema() {
		return imagemTema;
	}

	public LocalDateTime getDtHoraInicio() {
		return dtHoraInicio;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public String getBairro() {
		return bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public Estados getUf() {
		return uf;
	}

	public String getCep() {
		return cep;
	}

	public String getComplemento() {
		return complemento;
	}

	public List<ItemPedido> getItens() {
	    return itens;
	}
	
}