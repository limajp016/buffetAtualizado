package com.fateczl.BuffetRafaela.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
	
	boolean existsByCpf(String cpf);
	boolean existsByEmail(String email);
	boolean existsByTelefone(String telefone);
	
	Optional<Cliente> findByCpf(String cpf);
	Optional<Cliente> findByEmail(String email);
	Optional<Cliente> findByTelefone(String telefone);
	
}
