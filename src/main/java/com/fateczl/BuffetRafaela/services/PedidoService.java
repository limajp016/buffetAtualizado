package com.fateczl.BuffetRafaela.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Pedido;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.repositories.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Page<Pedido> listarPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    public Pedido buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + id));
    }

    public Pedido salvarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public Pedido gerarPedidoAPartirDoOrcamento(Orcamento orcamento) {
        if (orcamento == null) {
            throw new IllegalArgumentException("Orçamento não pode ser nulo!");
        }

        if (orcamento.getStatus() != StatusOrcamento.APROVADO) {
            throw new IllegalArgumentException("Orçamento não está aprovado!");
        }

        Pedido pedido = new Pedido(orcamento);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return pedidoSalvo;
    }


}