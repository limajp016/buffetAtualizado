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

import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoTema;
import com.fateczl.BuffetRafaela.records.DadosCadastroTema;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tema")
public class TemaController {
	
	@Autowired
	private TemaRepository repository;
	
	@GetMapping ("/formulario")
	public String carregaPaginaFormulario(Long id, Model model) {
		if(id != null) {
			var tema = repository.getReferenceById(id);
			model.addAttribute("tema", tema);
		}
		return "tema/formulario";
	}
	
	@GetMapping
	public String carregaPaginaListagem(Model model) {
		model.addAttribute("listaTemas", repository.findAll(Sort.by("descricao").ascending()));
		return "tema/listagem";
	}
	
	@PostMapping
	@Transactional
	public String cadastrar(@Valid DadosCadastroTema dados) {
		repository.save(new Tema(dados));
		return "redirect:tema";
	}

	@PutMapping
	@Transactional
	public String atualizar(@Valid DadosAtualizacaoTema dados) {
		var tema = repository.getReferenceById(dados.id());
		tema.atualizarInformacoes(dados);
		return "redirect:tema";
	}
	
	@DeleteMapping
	@Transactional
	public String removeTema(Long id) {
		repository.deleteById(id);
		return "redirect:tema";
	}
	
}
