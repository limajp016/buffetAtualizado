package com.fateczl.BuffetRafaela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fateczl.BuffetRafaela.entities.OrcamentoItem;

public interface OrcamentoItemRepository extends JpaRepository<OrcamentoItem, Long> {
	
	boolean existsByItemId(Long itemId);

}
