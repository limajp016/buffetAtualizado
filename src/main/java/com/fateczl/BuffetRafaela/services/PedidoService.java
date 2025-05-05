package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fateczl.BuffetRafaela.entities.Pedido;
import com.fateczl.BuffetRafaela.repositories.PedidoRepository;

public class PedidoService {
	@Autowired
	private PedidoRepository repository;
	
	public List<Pedido> getAllPedido() {
		return repository.findAll();
	}
	
	public Pedido getPedidoById(Long id) {
		return repository.getReferenceById(id);
	}

}
