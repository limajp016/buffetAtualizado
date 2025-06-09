package com.fateczl.BuffetRafaela.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;

@Repository
public interface OrcamentoRepository extends JpaRepository<Orcamento, Long> {

	boolean existsByDtHoraInicio(LocalDateTime dtHoraInicio);

	boolean existsByDtHoraInicioAndIdNot(LocalDateTime dtHoraInicio, Long id);

	@Query("SELECT o FROM Orcamento o WHERE o.status = :status")
	List<Orcamento> findByStatus(StatusOrcamento status);

	@Query("SELECT o FROM Orcamento o WHERE o.status = com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento.APROVADO")
	List<Orcamento> findOrcamentosAprovados();

	boolean existsByClienteId(Long clienteId);

	boolean existsByTemaId(Long temaId);

	@Query("""
			    SELECT o FROM Orcamento o
			    WHERE o.logradouro = :logradouro
			      AND o.numero = :numero
			      AND o.bairro = :bairro
			      AND o.cidade = :cidade
			      AND o.uf = :uf
			      AND o.cep = :cep
			      AND o.complemento = :complemento
			      AND o.dtHoraInicio = :dtHoraInicio
			      AND o.status <> 'REPROVADO'
			""")
	List<Orcamento> buscarOrcamentosPorEnderecoEData(@Param("logradouro") String logradouro,
			@Param("numero") String numero, @Param("bairro") String bairro, @Param("cidade") String cidade,
			@Param("uf") Estados uf, @Param("cep") String cep, @Param("complemento") String complemento,
			@Param("dtHoraInicio") LocalDateTime dtHoraInicio);

	@EntityGraph(attributePaths = { "cliente", "tema", "itens" })
	Optional<Orcamento> findWithAssociationsById(Long id);

	List<Orcamento> findByStatusAndDataAberturaEmailIsNullAndDataEnvioEmailBefore(StatusOrcamento status,
			LocalDateTime limite);

}