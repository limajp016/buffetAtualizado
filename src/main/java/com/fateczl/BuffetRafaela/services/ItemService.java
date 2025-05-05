package com.fateczl.BuffetRafaela.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;

@Service
public class ItemService {
	@Autowired
	private ItemRepository repository;

	public List<Item> getAllItem(){
		return repository.findAll();
	}
	
	public Item getItemById(Long id) {
		return repository.getReferenceById(id);
	}

}
