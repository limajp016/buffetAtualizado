package com.fateczl.BuffetRafaela.entities;

import com.fateczl.BuffetRafaela.records.DadosAtualizacaoCliente;
import com.fateczl.BuffetRafaela.records.DadosCadastroCliente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cliente")
@EqualsAndHashCode(of = "id")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cliente_id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve estar no formato 123.456.789-01")
    @Column(name = "cpf")
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (99) 99999-9999 ou (99) 9999-9999")
    @Column(name = "telefone")
    private String telefone;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email deve ser válido")
    @Column(name = "email")
    private String email;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String cpf, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }

    public Cliente(DadosCadastroCliente dados) {
        this.nome = dados.nome();
        this.cpf = dados.cpf();
        this.email = dados.email();
        this.telefone = dados.telefone();
    }

    public void atualizarInformacoes(@Valid DadosAtualizacaoCliente dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.email() != null) {
            this.email = dados.email();
        }
        if (dados.cpf() != null) {
            this.cpf = dados.cpf();
        }
        if (dados.telefone() != null) {
            this.telefone = dados.telefone();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
