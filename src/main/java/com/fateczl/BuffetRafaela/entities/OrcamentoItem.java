package com.fateczl.BuffetRafaela.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="orcamento_item")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class OrcamentoItem {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.AUTO)
	  @Column(name = "orcamento_item_id")
	  private Long id;

	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "orcamento_id", nullable = false)
	  @NotNull(message = "Selecione um item")
	  private Orcamento orcamento;

	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "item_id", nullable = false)
	  private Item item;

	  @Min(value = 1, message = "Quantidade deve ser maior que zero")
	  @Column(name = "quantidade", nullable = false)
	  private Integer quantidade;
	  
	  public OrcamentoItem() {
		  
	  }

	  public OrcamentoItem(Orcamento orcamento, Item item, Integer quantidade) {
		  this.orcamento = orcamento;
		  this.item = item;
		  this.quantidade = quantidade;
	   }

	  public Double getSubtotal() {
		  return item.getValorVenda() * quantidade;
	  }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
