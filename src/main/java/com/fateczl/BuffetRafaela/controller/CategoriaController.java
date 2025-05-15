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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fateczl.BuffetRafaela.entities.Categoria;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoCategoria;
import com.fateczl.BuffetRafaela.records.DadosCadastroCategoria;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {
	
	@Autowired
	private CategoriaRepository repository;
	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping("/formulario")
	public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
	    Categoria categoria;
	    if (id != null) {
	        categoria = repository.findById(id).orElse(new Categoria());
	    } else {
	        categoria = new Categoria();
	    }
	    model.addAttribute("categoria", categoria);
	    return "categoria/formulario";
	}
	
	@GetMapping
	public String carregaPaginaListagem(Model model) {
		model.addAttribute("listaCategoria", repository.findAll(Sort.by("descricao").ascending()));
		return "categoria/listagem";
	}
	
	@PostMapping
    @Transactional
    public String cadastrar(@Valid DadosCadastroCategoria dados, RedirectAttributes redirectAttributes) {
        repository.save(new Categoria(dados));
        redirectAttributes.addFlashAttribute("mensagem", "Categoria cadastrada com sucesso!");
        return "redirect:/categoria";
    }
	
	 @PutMapping
	 @Transactional
	 public String atualizar(@Valid DadosAtualizacaoCategoria dados, RedirectAttributes redirectAttributes) {
		 var item = repository.getReferenceById(dados.id());
	     item.atualizarInformacoes(dados);
	     redirectAttributes.addFlashAttribute("mensagem", "Categoria atualizada com sucesso!");
	     return "redirect:/categoria";
	 }
	
	 @DeleteMapping
	 @Transactional
	 public String removeCategoria(Long id, RedirectAttributes redirectAttributes) {
	     boolean possuiItensVinculados = itemRepository.existsByCategoriaId(id);
	     
	     if (possuiItensVinculados) {
	         redirectAttributes.addFlashAttribute(
	             "erroVinculado", 
	             "Não é possível remover esta categoria pois ela está vinculada a um ou mais itens."
	         );
	         return "redirect:/categoria";
	     }

	     try {
	         repository.deleteById(id);
	         redirectAttributes.addFlashAttribute("mensagem", "Categoria removida com sucesso!");
	     } catch (Exception e) {
	         redirectAttributes.addFlashAttribute(
	             "erro", 
	             "Ocorreu um erro ao tentar remover a categoria: " + e.getMessage()
	         );
	     }
	     
	     return "redirect:/categoria";
	 }
}
