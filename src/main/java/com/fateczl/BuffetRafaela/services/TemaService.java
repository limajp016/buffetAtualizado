package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;

@Service
public class TemaService {
	@Autowired
	private TemaRepository repository;
	
	public List<Tema> getAllTema(){
		return repository.findAll(Sort.by("descricao"));
	}

}
