package com.fateczl.BuffetRafaela.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pedido_id")
    private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orcamento_id", nullable = false)
	private Orcamento orcamento;
	
    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;
	
	@Column(name = "dt_hr_inicio", nullable = false)
    private LocalDateTime dtHoraInicio;
	
	public Pedido() {
		
	}
	
/*	public Pedido(DadosCadastroPedido dados) {
		this.orcamento = dados.orcamento();
		this.valorTotal = dados.valorTotal();
		this.dtHoraInicio = dados.dtHoraInicio();
	}
	*/
	
	public Pedido(Orcamento orcamento, Double valorTotal, LocalDateTime dtHoraInicio) {
        this.orcamento = orcamento;
        this.valorTotal = valorTotal;
        this.dtHoraInicio = dtHoraInicio;
    }

	/*
	public void atualizarInformacoes(@Valid DadosAtualizacaoPedido dados) {
		if(dados.orcamento() != null) {
			this.orcamento = dados.orcamento();
		}
		
		if(dados.valorTotal() != null) {
			this.valorTotal = dados.valorTotal();
		}
		
		if (dados.dtHoraInicio() != null) {
			this.dtHoraInicio = dados.dtHoraInicio();
		}
	}
	*/
	
	public void atualizarInformacoes(Double valorTotal, LocalDateTime dtHoraInicio) {
        if (valorTotal != null) {
            this.valorTotal = valorTotal;
        }
        if (dtHoraInicio != null) {
            this.dtHoraInicio = dtHoraInicio;
        }
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

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public LocalDateTime getDtHoraInicio() {
		return dtHoraInicio;
	}

	public void setDtHoraInicio(LocalDateTime dtHoraInicio) {
		this.dtHoraInicio = dtHoraInicio;
	}

}
