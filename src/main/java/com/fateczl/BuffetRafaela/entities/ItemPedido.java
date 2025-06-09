package com.fateczl.BuffetRafaela.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_pedido")
@Getter
@NoArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_pedido_id")
    private Long id;

    @Column(name = "descricao_item", nullable = false)
    private String descricaoItem;

    @Column(name = "valor_venda", nullable = false)
    private Double valorVenda;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private Double precoUnitario;

    @Lob
    @Column(name = "imagem_item", columnDefinition = "LONGBLOB")
    private byte[] imagemItem;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    public ItemPedido() {
    	
    }

    public ItemPedido(String descricaoItem, Double valorVenda, Integer quantidade, Double precoUnitario, byte[] imagemItem, Pedido pedido) {
        this.descricaoItem = descricaoItem;
        this.valorVenda = valorVenda;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.imagemItem = imagemItem;
        this.pedido = pedido;
    }

	public Long getId() {
		return id;
	}

	public String getDescricaoItem() {
		return descricaoItem;
	}

	public Double getValorVenda() {
		return valorVenda;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Double getPrecoUnitario() {
		return precoUnitario;
	}

	public byte[] getImagemItem() {
		return imagemItem;
	}

	public Pedido getPedido() {
		return pedido;
	}
    
}
