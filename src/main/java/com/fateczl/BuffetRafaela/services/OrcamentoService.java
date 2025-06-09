package com.fateczl.BuffetRafaela.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Pedido;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.exception.ValidacaoException;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;

import jakarta.transaction.Transactional;

@Service
public class OrcamentoService {

	@Autowired
	private OrcamentoRepository repository;
	@Autowired
	private PedidoService pedidoService;
	@Autowired
    private EmailService emailService;

	public List<Orcamento> getAllOrcamento() {
		return repository.findAll(Sort.by("id"));
	}

	public Orcamento getOrcamentoById(Long id) {
		return repository.getReferenceById(id);
	}

	public void validarEnderecoEDataDuplicados(Orcamento orcamento) {
		List<Orcamento> existentes = repository.buscarOrcamentosPorEnderecoEData(orcamento.getLogradouro(),
				orcamento.getNumero(), orcamento.getBairro(), orcamento.getCidade(), orcamento.getUf(),
				orcamento.getCep(), orcamento.getComplemento(), orcamento.getDtHoraInicio());

		if (!existentes.isEmpty()) {
			throw new ValidacaoException("Já existe um orçamento para esse endereço e data.");
		}
	}

	public void validarEnderecoEDataDuplicados(Orcamento orcamento, Long idAtual) {
		List<Orcamento> existentes = repository.buscarOrcamentosPorEnderecoEData(orcamento.getLogradouro(),
				orcamento.getNumero(), orcamento.getBairro(), orcamento.getCidade(), orcamento.getUf(),
				orcamento.getCep(), orcamento.getComplemento(), orcamento.getDtHoraInicio());

		boolean existeOutro = existentes.stream().anyMatch(o -> !o.getId().equals(idAtual));

		if (existeOutro) {
			throw new ValidacaoException("Já existe um orçamento para esse endereço e data.");
		}
	}
	
    @Transactional
    public boolean aprovarOrcamento(Long id) {
        Optional<Orcamento> optional = repository.findById(id);
        if (optional.isPresent()) {
            Orcamento orcamento = optional.get();
            if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
                return false;
            }
            if (orcamento.getCliente() == null || orcamento.getTema() == null || orcamento.getItens().isEmpty()) {
                throw new IllegalStateException("Orçamento incompleto: cliente, tema ou itens não podem estar vazios.");
            }
            orcamento.setStatus(StatusOrcamento.APROVADO);
            repository.save(orcamento);
            
            Pedido pedido = pedidoService.gerarPedidoAPartirDoOrcamento(orcamento);
            
            emailService.enviarEmailPedidoCriado(orcamento, pedido);
            return true;
        }
        return false;
    }

    public boolean reprovarOrcamento(Long id) {
        Optional<Orcamento> optional = repository.findById(id);
        if (optional.isPresent()) {
            Orcamento orcamento = optional.get();
            if (orcamento.getStatus() != StatusOrcamento.PENDENTE) {
                return false;
            }
            orcamento.setStatus(StatusOrcamento.REPROVADO);
            repository.save(orcamento);
            return true;
        }
        return false;
    }
    
    public void registrarAberturaEmail(Long id) {
        Orcamento orcamento = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

        if (orcamento.getDataAberturaEmail() == null) {
            orcamento.setDataAberturaEmail(LocalDateTime.now());
            repository.save(orcamento);
        }
    }
    
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void reprovarOrcamentosNaoAbertos() {
        LocalDateTime limite = LocalDateTime.now().minusHours(24);
        List<Orcamento> orcamentos = repository
            .findByStatusAndDataAberturaEmailIsNullAndDataEnvioEmailBefore(StatusOrcamento.PENDENTE, limite);

        for (Orcamento o : orcamentos) {
            o.setStatus(StatusOrcamento.REPROVADO);
        }

       repository.saveAll(orcamentos);
    }



}