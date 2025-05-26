package com.fateczl.BuffetRafaela.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;


@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {
	
	boolean existsByDtHoraInicio(LocalDateTime dtHoraInicio);
	
	boolean existsByDtHoraInicioAndIdNot(LocalDateTime dtHoraInicio, Long id);
	
	@Query("SELECT o FROM Orcamento o WHERE o.status = :status")
	List<Orcamento> findByStatus(StatusOrcamento status);
	
	@Query("SELECT o FROM Orcamento o WHERE o.status = com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento.APROVADO")
    List<Orcamento> findOrcamentosAprovados();

}
