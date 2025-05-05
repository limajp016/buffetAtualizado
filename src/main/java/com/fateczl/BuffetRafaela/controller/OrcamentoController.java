package com.fateczl.BuffetRafaela.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoOrcamento;
import com.fateczl.BuffetRafaela.records.DadosCadastroOrcamento;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/orcamento")
public class OrcamentoController {
	
	@Autowired
	private OrcamentoRepository orcamentoRepository;
	
	@Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TemaRepository temaRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        if (id != null) {
            var orcamento = orcamentoRepository.getReferenceById(id);
            model.addAttribute("orcamento", orcamento);
        }
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("temas", temaRepository.findAll());
        model.addAttribute("items", itemRepository.findAll());
        model.addAttribute("statusOrcamentoValues", StatusOrcamento.values());
        return "orcamento/formulario";
    }

    @GetMapping
    public String carregaPaginalistagem(Model model) {
        model.addAttribute("orcamentos", orcamentoRepository.findAll(Sort.by("id")));
        return "orcamento/listagem";
    }
    
    @PostMapping
    @Transactional
    public String cadastrar(@RequestParam Long clienteId,
                            @RequestParam Long temaId,
                            @RequestParam List<Long> itens,
                            @Valid DadosCadastroOrcamento dados) {
        var cliente = clienteRepository.findById(clienteId)
                                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        var tema = temaRepository.findById(temaId)
                                .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));
        var orcamento = new Orcamento(dados);
        orcamento.setCliente(cliente);
        orcamento.setTema(tema);
        
        for (Long itemId : itens) {
            var item = itemRepository.findById(itemId)
                                    .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + itemId));
            orcamento.addItem(item);
        }

        orcamento.setValorTotal();
        
        orcamentoRepository.save(orcamento);
        return "redirect:/orcamento";
    }
    
    @PutMapping
    @Transactional
    public String atualizar(@RequestParam Long id,
                            @RequestParam Long clienteId,
                            @RequestParam Long temaId,
                            @RequestParam List<Long> itens,
                            @Valid DadosAtualizacaoOrcamento dados) {
        var orcamento = orcamentoRepository.findById(id)
                                       .orElseThrow(() -> new IllegalArgumentException("Aluguel não encontrado"));
        var cliente = clienteRepository.findById(clienteId)
                                       .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        var tema = temaRepository.findById(temaId)
                                 .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));
        
        orcamento.setCliente(cliente);
        orcamento.setTema(tema);
        orcamento.atualizarInformacoes(dados);

        for (Long itemId : itens) {
            var item = itemRepository.findById(itemId)
                                     .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + itemId));
            orcamento.addItem(item);
        }

        orcamento.setValorTotal();
        
        orcamentoRepository.save(orcamento);
        return "redirect:/orcamento";
    }
    
    @DeleteMapping
    @Transactional
    public String removeOrcamento(@RequestParam Long id) {
        var orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        
        orcamento.getItens().forEach(item -> {
            item.getOrcamentos().remove(orcamento);
        });
        orcamento.getItens().clear();

        orcamentoRepository.delete(orcamento);
        
        return "redirect:/orcamento";
    }
    
    @PostMapping("/submitOrcamento")
    @Transactional
    public String submitAluguel(@RequestParam List<Long> itens, Model model) {
        Orcamento orcamento = new Orcamento();
        orcamento.setItens(new ArrayList<>());

        for (Long itemId : itens) {
            var item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + itemId));
            orcamento.addItem(item);
        }

        orcamentoRepository.save(orcamento);

        model.addAttribute("message", "Orcamento submetido com sucesso!");
        return "redirect:/aluguel";
    }
    
}