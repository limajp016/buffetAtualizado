package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Categoria;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repository;
	
	public List<Categoria> getAllCategoria(){
		return repository.findAll(Sort.by(Sort.Direction.DESC, "dataCriacao"));
	}
	
	public Categoria getCategoriaById(Long id) {
		return repository.getReferenceById(id);
	}
}
