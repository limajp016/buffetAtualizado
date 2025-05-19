package com.fateczl.BuffetRafaela.entities;

import java.time.LocalDateTime;

import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.records.DadosListagemPedido;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false, unique = true)
    private Orcamento orcamento;
    
    public Long getId() {
        return this.id;
    }
    
    @Transient
    public LocalDateTime getDataEvento() {
        return orcamento != null ? orcamento.getDtHoraInicio() : null;
    }
    
    @Transient
    public Double getValorTotal() {
        return orcamento != null ? orcamento.getValorTotal() : null;
    }
    
    @Transient
    public Long getOrcamentoId() {
        return orcamento != null ? orcamento.getId() : null;
    }

    public Pedido(Orcamento orcamento) {
        if (orcamento == null) {
            throw new IllegalArgumentException("Orçamento não pode ser nulo");
        }
        if (!orcamento.getStatus().equals(StatusOrcamento.APROVADO)) {
            throw new IllegalStateException("Só é possível criar pedido para orçamento aprovado");
        }
        
        this.orcamento = orcamento;
    }
    
/*
    public DadosListagemPedido toDadosListagem() {
        return new DadosListagemPedido(this);
    }
*/    
    protected void setOrcamento(Orcamento orcamento) {
        if (this.orcamento != null) {
            throw new IllegalStateException("Não é possível alterar o orçamento associado a um pedido");
        }
        this.orcamento = orcamento;
    }

    public void setValorTotal(Double valorTotal) {
        throw new UnsupportedOperationException("Valor total é derivado do orçamento e não pode ser alterado diretamente");
    }

    public void setDataEvento(LocalDateTime dataEvento) {
        throw new UnsupportedOperationException("Data do evento é derivada do orçamento e não pode ser alterada diretamente");
    }
}
