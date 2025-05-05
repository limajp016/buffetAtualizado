package com.fateczl.BuffetRafaela.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoOrcamento;
import com.fateczl.BuffetRafaela.records.DadosCadastroOrcamento;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orcamento")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orcamento_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tema_id", nullable = false)
    private Tema tema;

    @JsonManagedReference
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
    		name="orcamento_item",
    		joinColumns = @JoinColumn(name="orcamento_id"),
    		inverseJoinColumns = @JoinColumn(name= "item_id") 		
    		)
    private List<Item> itens = new ArrayList<>();

    @Column(name = "dt_hr_inicio", nullable = false)
    private LocalDateTime dtHoraInicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusOrcamento status;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;
    
    @Column(name = "bairro", nullable = false)
    private String bairro;
    
    @Column(name = "cidade", nullable = false)
    private String cidade;
    
    @Column(name = "uf", nullable = false, length = 2)
    private String uf;
    
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;
    
    public Orcamento() {
    	
    }
    
    public Orcamento(DadosCadastroOrcamento dados) {
    	this.cliente = dados.cliente();
    	this.tema = dados.tema();
    	this.itens = dados.itens();
        this.dtHoraInicio = dados.dtHoraInicio();
        this.status = dados.status();
        this.logradouro = dados.logradouro();
        this.bairro = dados.bairro();
        this.cidade = dados.cidade();
        this.uf = dados.uf();
        this.cep = dados.cep();
    }
    
    public void atualizarInformacoes(@Valid DadosAtualizacaoOrcamento dados) {
    	if (dados.cliente() != null) {
            this.cliente = dados.cliente();
        }
    	if (dados != null && dados.itens() != null) {
    	    this.itens.clear();
    	    this.itens.addAll(dados.itens());
    	}
    	if (dados.dtHoraInicio() != null) {
            this.dtHoraInicio = dados.dtHoraInicio();
        }
        if (dados.status() != null) {
            this.status = dados.status();
        }
        if (dados.logradouro() != null) {
            this.logradouro = dados.logradouro();
        }
        if (dados.bairro() != null) {
            this.bairro = dados.bairro();
        }
        if (dados.cidade() != null) {
            this.cidade = dados.cidade();
        }
        if (dados.uf() != null) {
            this.uf = dados.uf();
        }
        if (dados.cep() != null) {
            this.cep = dados.cep();
        }
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Tema getTema() {
		return tema;
	}

	public void setTema(Tema tema) {
		this.tema = tema;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}

	public LocalDateTime getDtHoraInicio() {
		return dtHoraInicio;
	}

	public void setDtHoraInicio(LocalDateTime dtHoraInicio) {
		this.dtHoraInicio = dtHoraInicio;
	}

	public StatusOrcamento getStatus() {
		return status;
	}

	public void setStatus(StatusOrcamento status) {
		this.status = status;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}
	
	public void addItem(Item item) {
	    if (this.itens == null) {
	        this.itens = new ArrayList<>();
	    }
	    if (!this.itens.contains(item)) {
	        this.itens.add(item);
	        item.getOrcamentos().add(this); 
	    }
	}

	public void removeItem(Item item) {
	    if (this.itens != null && this.itens.contains(item)) {
	        this.itens.remove(item);
	    }
	}
	
	public void setValorTotal() {
	    Double valorItens = 0.0;

	    Map<Item, Long> contagem = this.itens.stream()
	    	    .collect(Collectors.groupingBy(item -> item, Collectors.counting()));

	    for (Map.Entry<Item, Long> entry : contagem.entrySet()) {
	        Item item = entry.getKey();
	        Long qtd = entry.getValue();
	        valorItens += item.getValorVenda() * qtd;
	    }

	    Double precoTema = (this.tema != null) ? this.tema.getPreco() : 0.0;

	    this.valorTotal = valorItens + precoTema;
	}
	
}