package com.fateczl.BuffetRafaela.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public String carregaPaginaListagem(Model model) {
		model.addAttribute("listaClientes", repository.findAll(Sort.by("nome").ascending()));
		return "cliente/listagem";
	}
	
	@PostMapping
	@Transactional
	public String cadastrar(@Valid DadosCadastroCliente dados, BindingResult result, 
	                        RedirectAttributes redirectAttributes, Model model) {
	    if (result.hasErrors()) {
	        model.addAttribute("dadosCadastro", dados);
	        return "cliente/formulario";
	    }
	    repository.save(new Cliente(dados));
	    redirectAttributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
	    return "redirect:/cliente";
	}

	@PutMapping
	@Transactional
	public String atualizar(@Valid DadosAtualizacaoCliente dados, BindingResult result, 
	                        RedirectAttributes redirectAttributes, Model model) {
	    if (result.hasErrors()) {
	        DadosCadastroCliente dadosCadastro = new DadosCadastroCliente(dados.id(), dados.nome(), dados.cpf(), dados.telefone(), dados.email());
	        model.addAttribute("dadosCadastro", dadosCadastro);
	        return "cliente/formulario";
	    }
	    try {
	        Cliente cliente = repository.findById(dados.id())
	                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	        cliente.atualizarInformacoes(dados);
	        redirectAttributes.addFlashAttribute("mensagem", "Cliente atualizado com sucesso!");
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado");
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
