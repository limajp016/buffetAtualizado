package com.fateczl.BuffetRafaela.records;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

public record DadosListagemOrcamento(
    Long id,
    Cliente cliente,
    Tema tema,
    List<ItemQuantidade> itens,
    LocalDateTime dtHoraInicio,
    StatusOrcamento status,
    Double valorTotal,
    String logradouro,
    String numero,
    String bairro,
    String cidade,
    Estados uf,
    String cep,
    String complemento
) {

    public DadosListagemOrcamento(Orcamento orcamento) {
        this(
            orcamento.getId(),
            orcamento.getCliente(),
            orcamento.getTema(),
            orcamento.getOrcamentoItens().stream()
                .map(orcamentoItem -> new ItemQuantidade(orcamentoItem.getItem(), orcamentoItem.getQuantidade()))
                .collect(Collectors.toList()), 
            orcamento.getDtHoraInicio(),
            orcamento.getStatus(),
            orcamento.getValorTotal(),
            orcamento.getLogradouro(),
            orcamento.getNumero(),
            orcamento.getBairro(),
            orcamento.getCidade(),
            orcamento.getUf(),
            orcamento.getCep(),
            orcamento.getComplemento()
        );
    }
}