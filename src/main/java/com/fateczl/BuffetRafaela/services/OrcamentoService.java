package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository repository;
    
    public List<Orcamento> getAllOrcamento(){
    	return repository.findAll(Sort.by("id"));
    }
    
    public Orcamento getOrcamentoById(Long id) {
    	return repository.getReferenceById(id);
    }
    
}