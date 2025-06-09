package com.fateczl.BuffetRafaela.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "orcamento_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tema_id", nullable = false)
    private Tema tema;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itens = new ArrayList<>();

    @Column(name = "dt_hr_inicio", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dtHoraInicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusOrcamento status;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "uf", nullable = false)
    private Estados uf;
    
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;
    
    @Column(name = "complemento")
    private String complemento;
    
    private LocalDateTime dataEnvioEmail;
    private LocalDateTime dataAberturaEmail;
    
    public Orcamento() {
    }

    public Orcamento(DadosCadastroOrcamento dados) {
        this.dtHoraInicio = dados.dtHoraInicio();
        this.status = dados.status();
        this.logradouro = dados.logradouro();
        this.numero = dados.numero();
        this.bairro = dados.bairro();
        this.cidade = dados.cidade();
        this.uf = dados.uf();
        this.cep = dados.cep();
        this.complemento = dados.complemento();
        
        this.itens = new ArrayList<>();
    }

    public void atualizarInformacoes(@Valid DadosAtualizacaoOrcamento dados) {
        if (dados.cliente() != null) {
            this.cliente = dados.cliente();
        }
        if (dados.tema() != null) {
            this.tema = dados.tema();
        }
        if (dados.itens() != null) {
            this.itens.clear();
            dados.itens().forEach(itemQuantidade -> 
                addItem(itemQuantidade.item(), itemQuantidade.quantidade()));
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
        if (dados.numero() != null) {
            this.numero = dados.numero();
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
        if (dados.complemento() != null) {
            this.complemento = dados.complemento();
        }
        
        calcularValorTotal();
    }
    
    public void addItem(Item item, Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        
        boolean itemJaAdicionado = itens.stream()
            .anyMatch(io -> io.getItem().getId().equals(item.getId()));
            
        if (itemJaAdicionado) {
            throw new IllegalStateException("Item já adicionado ao orçamento.");
        }
        
        ItemOrcamento itemOrcamento = new ItemOrcamento(this, item, quantidade);
        itens.add(itemOrcamento);
        calcularValorTotal();
    }
    
    public void removeItem(Item item) {
        itens.removeIf(io -> io.getItem().getId().equals(item.getId()));
        calcularValorTotal();
    }
    
    public void calcularValorTotal() {
        double valorItens = itens.stream()
            .filter(Objects::nonNull)
            .mapToDouble(ItemOrcamento::getSubtotal)
            .sum();
        
        double precoTema = (this.tema != null && this.tema.getPreco() != null) ? 
            this.tema.getPreco() : 0.0;
        
        this.valorTotal = valorItens + precoTema;
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

	public List<ItemOrcamento> getItens() {
		return itens;
	}

	public void setItens(List<ItemOrcamento> itens) {
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

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
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

	public Estados getUf() {
		return uf;
	}

	public void setUf(Estados uf) {
		this.uf = uf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public LocalDateTime getDataEnvioEmail() {
		return dataEnvioEmail;
	}

	public void setDataEnvioEmail(LocalDateTime dataEnvioEmail) {
		this.dataEnvioEmail = dataEnvioEmail;
	}

	public LocalDateTime getDataAberturaEmail() {
		return dataAberturaEmail;
	}

	public void setDataAberturaEmail(LocalDateTime dataAberturaEmail) {
		this.dataAberturaEmail = dataAberturaEmail;
	}
}