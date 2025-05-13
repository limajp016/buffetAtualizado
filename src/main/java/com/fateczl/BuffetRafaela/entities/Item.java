package com.fateczl.BuffetRafaela.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoItem;
import com.fateczl.BuffetRafaela.records.DadosCadastroItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "item")
@EqualsAndHashCode(of = "id")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "valor_custo")
    private Double valorCusto;

    @Column(name = "valor_venda")
    private Double valorVenda;

    @Column(name = "campo_desc")
    private String campoDesc;

    @Lob
    @Column(name = "imagem", columnDefinition = "LONGBLOB")
    private byte[] imagem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @JsonBackReference
    @OneToMany(mappedBy = "item", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<OrcamentoItem> orcamentoItens = new ArrayList<>();

    @Transient
    private String imagemBase64;

    public Item() {
    }

    public Item(Long id, String descricao, Double valorCusto, Double valorVenda, String campoDesc, byte[] imagem, Categoria categoria) {
        this.id = id;
        this.descricao = descricao;
        this.valorCusto = valorCusto;
        this.valorVenda = valorVenda;
        this.campoDesc = campoDesc;
        this.imagem = imagem;
        this.categoria = categoria;
    }

    public Item(DadosCadastroItem dados) {
        this.descricao = dados.descricao();
        this.campoDesc = dados.campoDesc();
        this.valorCusto = dados.valorCusto();
        this.valorVenda = dados.valorVenda();
        this.imagem = dados.imagem();
        this.categoria = dados.categoria();
    }

    public void atualizarInformacoes(@Valid DadosAtualizacaoItem dados) {
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
        if (dados.valorCusto() != null) {
            this.valorCusto = dados.valorCusto();
        }
        if (dados.valorVenda() != null) {
            this.valorVenda = dados.valorVenda();
        }
        if (dados.campoDesc() != null) {
            this.campoDesc = dados.campoDesc();
        }
        if (dados.imagem() != null) {
            this.imagem = dados.imagem();
        }
        if (dados.categoria() != null) {
            this.categoria = dados.categoria();
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

    public Double getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(Double valorCusto) {
        this.valorCusto = valorCusto;
    }

    public Double getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(Double valorVenda) {
        this.valorVenda = valorVenda;
    }

    public String getCampoDesc() {
        return campoDesc;
    }

    public void setCampoDesc(String campoDesc) {
        this.campoDesc = campoDesc;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }

    public List<OrcamentoItem> getOrcamentoItens() {
        return orcamentoItens;
    }

    public void setOrcamentoItens(List<OrcamentoItem> orcamentoItens) {
        this.orcamentoItens = orcamentoItens;
    }
    
    public List<Orcamento> getOrcamentos() {
        return orcamentoItens.stream()
                .map(OrcamentoItem::getOrcamento)
                .toList();
    }

    
}

