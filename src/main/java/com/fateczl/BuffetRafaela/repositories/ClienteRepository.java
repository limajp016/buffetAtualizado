package com.fateczl.BuffetRafaela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
	
	
}
