package com.fateczl.BuffetRafaela.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
	
	@Query("SELECT COUNT(o) > 0 FROM Orcamento o WHERE o.dtHoraInicio = :dt_hora_inicio AND o.logradouro = :logradouro AND o.bairro = :bairro AND o.cidade = :cidade AND o.uf = :uf AND o.cep = :cep")
	boolean existsByDataAndLocalAndEndereco(
	    @Param("dt_hora_inicio") LocalDateTime dtHoraInicio,
	    @Param("logradouro") String logradouro,
	    @Param("bairro") String bairro,
	    @Param("cidade") String cidade,
	    @Param("uf") String uf,
	    @Param("cep") String cep
	);
	
	@Query("""
		    SELECT COUNT(o) > 0 FROM Orcamento o
		    WHERE o.dtHoraInicio = :dtHoraInicio
		      AND o.logradouro = :logradouro
		      AND o.bairro = :bairro
		      AND o.cidade = :cidade
		      AND o.uf = :uf
		      AND o.cep = :cep
		      AND o.id <> :id
		""")
		boolean existsByDataAndLocalAndEnderecoExcluindoId(@Param("dtHoraInicio") LocalDateTime dtHoraInicio,
		                                                    @Param("logradouro") String logradouro,
		                                                    @Param("bairro") String bairro,
		                                                    @Param("cidade") String cidade,
		                                                    @Param("uf") String uf,
		                                                    @Param("cep") String cep,
		                                                    @Param("id") Long id);

}
