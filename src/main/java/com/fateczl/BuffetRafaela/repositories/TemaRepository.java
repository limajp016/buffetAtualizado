package com.fateczl.BuffetRafaela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema,Long>{
	
}
