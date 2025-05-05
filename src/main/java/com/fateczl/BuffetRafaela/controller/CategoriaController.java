package com.fateczl.BuffetRafaela.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fateczl.BuffetRafaela.entities.Categoria;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoCategoria;
import com.fateczl.BuffetRafaela.records.DadosCadastroCategoria;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {
	
	@Autowired
	private CategoriaRepository repository;
	
	@GetMapping("/formulario")
	public String carregaPaginaFormulario(Long id, Model model) {
		if (id != null ) {
			var categoria = repository.getReferenceById(id);
			model.addAttribute("categoria", categoria);
		}
		return "categoria/formulario";
	}
	
	@GetMapping
	public String carregaPaginaListagem(Model model) {
		model.addAttribute("listaCategoria", repository.findAll(Sort.by("descricao").ascending()));
		return "categoria/listagem";
	}
	
	@PostMapping
	@Transactional
	public String cadastrar (@Valid DadosCadastroCategoria dados) {
		repository.save(new Categoria(dados));
		return "redirect:categoria";
	}
	
	@PutMapping
	@Transactional
	public String atualizar(@Valid DadosAtualizacaoCategoria dados) {
		var item = repository.getReferenceById(dados.id());
		item.atualizarInformacoes(dados);
		return "redirect:categoria";
	}
	
	@DeleteMapping
	@Transactional
	public String removeItem(Long id) {
		repository.deleteById(id);
		return "redirect:categoria";
	}

}
