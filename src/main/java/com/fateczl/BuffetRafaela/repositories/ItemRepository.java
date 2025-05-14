package com.fateczl.BuffetRafaela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>{
	
    boolean existsByCategoriaId(Long categoriaId); 
	
}
