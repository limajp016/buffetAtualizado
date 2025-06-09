package com.fateczl.BuffetRafaela.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoCliente;
import com.fateczl.BuffetRafaela.records.DadosCadastroCliente;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteRepository repository;
	@Autowired
	private OrcamentoRepository orcamentoRepository;
	
	@GetMapping("/formulario")
	public String carregaPaginaFormulario(@RequestParam(required = false) Long id, Model model, RedirectAttributes redirectAttributes) {
	    DadosCadastroCliente dadosCadastro;
	    
	    if (id != null) {
	        try {
	            Cliente cliente = repository.findById(id)
	                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	            dadosCadastro = new DadosCadastroCliente(cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getTelefone(), cliente.getEmail());
	        } catch (IllegalArgumentException e) {
	            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
	            return "redirect:/cliente/formulario";
	        }
	    } else {
	        dadosCadastro = new DadosCadastroCliente(null, "", "", "", "");
	    }
	    
	    model.addAttribute("dadosCadastro", dadosCadastro);
	    return "cliente/formulario";
	}
	
	@GetMapping
	public String listarClientes(@RequestParam(defaultValue = "0") int page,
	                             @RequestParam(defaultValue = "5") int size,
	                             Model model) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("nome").ascending());
	    Page<Cliente> pagina = repository.findAll(pageable);

	    model.addAttribute("pagina", pagina);
	    return "cliente/listagem";
	}

	
	@PostMapping
	@Transactional
	public String cadastrar(@Valid DadosCadastroCliente dados, BindingResult result,
	                       RedirectAttributes redirectAttributes) {
	    
	    if (repository.existsByCpf(dados.cpf())) {
	        redirectAttributes.addFlashAttribute("erroCpf", "CPF já cadastrado");
	    }
	    if (repository.existsByTelefone(dados.telefone())) {
	        redirectAttributes.addFlashAttribute("erroTelefone", "Telefone já cadastrado");
	    }
	    if (repository.existsByEmail(dados.email())) {
	        redirectAttributes.addFlashAttribute("erroEmail", "Email já cadastrado");
	    }

	    if (result.hasErrors() || 
	        redirectAttributes.getFlashAttributes().containsKey("erroCpf") || 
	        redirectAttributes.getFlashAttributes().containsKey("erroTelefone") || 
	        redirectAttributes.getFlashAttributes().containsKey("erroEmail")) {
	        
	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.dadosCadastro", result);
	        redirectAttributes.addFlashAttribute("dadosCadastro", dados);
	        return "redirect:/cliente/formulario";
	    }

	    try {
	        Cliente novoCliente = new Cliente(dados);
	        Cliente clienteSalvo = repository.save(novoCliente);
	        
	        redirectAttributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar cliente: " + e.getMessage());
	        redirectAttributes.addFlashAttribute("dadosCadastro", dados);
	        return "redirect:/cliente/formulario";
	    }

	    return "redirect:/cliente";
	}
	
	@PutMapping
	@Transactional
	public String atualizar(@Valid DadosAtualizacaoCliente dados, BindingResult result,
	                      RedirectAttributes redirectAttributes) {

	    if (result.hasErrors()) {
	        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.dadosCadastro", result);
	        redirectAttributes.addFlashAttribute("dadosCadastro", 
	            new DadosAtualizacaoCliente(dados.id(), dados.nome(), dados.cpf(), dados.telefone(), dados.email()));
	        return "redirect:/cliente/formulario?id=" + dados.id();
	    }

	    try {
	        Cliente cliente = repository.findById(dados.id())
	                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

	        if (!cliente.getCpf().equals(dados.cpf())) {
	            Optional<Cliente> outroClienteComCpf = repository.findByCpf(dados.cpf());
	            if (outroClienteComCpf.isPresent() && !outroClienteComCpf.get().getId().equals(cliente.getId())) {
	                redirectAttributes.addFlashAttribute("erroCpf", "CPF já cadastrado por outro cliente");
	            }
	        }

	        if (!cliente.getTelefone().equals(dados.telefone())) {
	            Optional<Cliente> outroClienteComTelefone = repository.findByTelefone(dados.telefone());
	            if (outroClienteComTelefone.isPresent() && !outroClienteComTelefone.get().getId().equals(cliente.getId())) {
	                redirectAttributes.addFlashAttribute("erroTelefone", "Telefone já cadastrado por outro cliente");
	            }
	        }
	        
	        if (!cliente.getEmail().equals(dados.email())) {
	            Optional<Cliente> outroClienteComEmail = repository.findByEmail(dados.email());
	            if (outroClienteComEmail.isPresent() && !outroClienteComEmail.get().getId().equals(cliente.getId())) {
	                redirectAttributes.addFlashAttribute("erroEmail", "Email já cadastrado por outro cliente");
	            }
	        }

	        if (redirectAttributes.getFlashAttributes().containsKey("erroCpf") || 
	            redirectAttributes.getFlashAttributes().containsKey("erroTelefone") || 
	            redirectAttributes.getFlashAttributes().containsKey("erroEmail")) {
	            
	            redirectAttributes.addFlashAttribute("dadosCadastro", 
	                new DadosAtualizacaoCliente(dados.id(), dados.nome(), dados.cpf(), dados.telefone(), dados.email()));
	            return "redirect:/cliente/formulario?id=" + dados.id();
	        }

	        cliente.atualizarInformacoes(dados);
	        redirectAttributes.addFlashAttribute("mensagem", "Cliente atualizado com sucesso!");

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
	        return "redirect:/cliente/formulario?id=" + dados.id();
	    }

	    return "redirect:/cliente";
	}
	
	@DeleteMapping
	@Transactional
	public String removeCliente(@RequestParam Long id, RedirectAttributes redirectAttributes) {
	    boolean possuiPedidosVinculados = orcamentoRepository.existsByClienteId(id);
	    
	    if (possuiPedidosVinculados) {
	        redirectAttributes.addFlashAttribute(
	            "erroVinculado", 
	            "Não é possível remover este cliente pois ele está vinculado a um ou mais pedidos."
	        );
	        return "redirect:/cliente";
	    }

	    try {
	        if (repository.existsById(id)) {
	            repository.deleteById(id);
	            redirectAttributes.addFlashAttribute("mensagem", "Cliente removido com sucesso!");
	        } else {
	            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
	        }
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute(
	            "erro", 
	            "Ocorreu um erro ao tentar remover o cliente: " + e.getMessage()
	        );
	    }
	    
	    return "redirect:/cliente";
	}

}
