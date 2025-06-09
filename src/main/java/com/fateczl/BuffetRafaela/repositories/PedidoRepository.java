package com.fateczl.BuffetRafaela.repositories;

import com.fateczl.BuffetRafaela.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}