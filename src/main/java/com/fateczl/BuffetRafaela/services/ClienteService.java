package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;

@Service
public class ClienteService {
	@Autowired
	private ClienteRepository repository;
	
	public List<Cliente> getAllCliente(){
		return repository.findAll(Sort.by("nome"));
	}
	
	public Cliente getPessoaById(Long id) {
		return repository.getReferenceById(id);
	}

}
