package com.fateczl.BuffetRafaela.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private TemaRepository temaRepository;
    
    @Autowired
    private OrcamentoRepository orcamentoRepository;
    

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalCategorias", categoriaRepository.count());
        model.addAttribute("totalItens", itemRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("totalTemas", temaRepository.count());
        model.addAttribute("totalOrcamentos", orcamentoRepository.count());
        
        return "index";
    }
}