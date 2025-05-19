package com.fateczl.BuffetRafaela.repositories;

import java.time.LocalDateTime;
import java.util.List;

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
	
	@Query("SELECT COUNT(o) > 0 FROM Orcamento o WHERE o.dtHoraInicio = :dt_hora_inicio AND o.logradouro = :logradouro AND o.bairro = :bairro AND o.numero = :numero AND o.cidade = :cidade AND o.uf = :uf AND o.cep = :cep AND o.complemento = :complemento")
	boolean existsByDataAndLocalAndEndereco(
	    @Param("dt_hora_inicio") LocalDateTime dtHoraInicio,
	    @Param("logradouro") String logradouro,
	    @Param("bairro") String bairro,
	    @Param("cidade") String cidade,
	    @Param("numero") String numero,
	    @Param("uf") Estados uf,
	    @Param("cep") String cep,
	    @Param("complemento") String complemento
	);
	
	@Query("""
		    SELECT COUNT(o) > 0 FROM Orcamento o
		    WHERE o.dtHoraInicio = :dtHoraInicio
		      AND o.logradouro = :logradouro
		      AND o.bairro = :bairro
		      AND o.numero = :numero
		      AND o.cidade = :cidade
		      AND o.uf = :uf
		      AND o.cep = :cep
		      AND o.complemento = :complemento
		      AND o.id <> :id
		""")
		boolean existsByDataAndLocalAndEnderecoExcluindoId(@Param("dtHoraInicio") LocalDateTime dtHoraInicio,
		                                                    @Param("logradouro") String logradouro,
		                                                    @Param("bairro") String bairro,
		                                                    @Param("numero") String numero,
		                                                    @Param("cidade") String cidade,
		                                                    @Param("uf") Estados uf,
		                                                    @Param("cep") String cep,
		                                                    @Param("complemento") String complemento,
		                                                    @Param("id") Long id);
	
	boolean existsByClienteId(Long clienteId);
	
	boolean existsByTemaId(Long temaId);
	
    @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END 
            FROM Orcamento o 
            WHERE o.dtHoraInicio = :data 
            AND o.logradouro = :logradouro 
            AND o.bairro = :bairro
            AND o.numero = :numero 
            AND o.cidade = :cidade 
            AND o.uf = :uf 
            AND o.cep = :cep
            AND o.complemento = :complemento
            AND o.status <> :status
            """)
        boolean existsByDataAndLocalAndEnderecoAndStatusNot(
            LocalDateTime data,
            String logradouro,
            String bairro,
            String numero,
            String cidade,
            Estados uf,
            String cep,
            String complemento,
            StatusOrcamento status);

        @Query("""
            SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END 
            FROM Orcamento o 
            WHERE o.dtHoraInicio = :data 
            AND o.logradouro = :logradouro 
            AND o.bairro = :bairro
            AND o.numero = :numero 
            AND o.cidade = :cidade 
            AND o.uf = :uf 
            AND o.cep = :cep
            AND o.complemento = :complemento
            AND o.status <> :status
            AND o.id <> :id
            """)
        boolean existsByDataAndLocalAndEnderecoAndStatusNotExcluindoId(
            LocalDateTime data,
            String logradouro,
            String bairro,
            String numero,
            String cidade,
            Estados uf,
            String cep,
            String complemento,
            StatusOrcamento status,
            Long id);

}
