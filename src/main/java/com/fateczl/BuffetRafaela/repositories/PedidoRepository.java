package com.fateczl.BuffetRafaela.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fateczl.BuffetRafaela.entities.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	@Query("SELECT COUNT(p) > 0 FROM Pedido p WHERE p.orcamento.id = :orcamentoId")
    boolean existsByOrcamentoId(@Param("orcamentoId") Long orcamentoId);

}
