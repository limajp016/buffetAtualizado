package com.fateczl.BuffetRafaela.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.PedidoRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private OrcamentoRepository orcamentoRepository;
    
    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        if (id != null) {
            var pedido = pedidoRepository.getReferenceById(id);
            model.addAttribute("pedido", pedido);
        }
        model.addAttribute("orcamentos", orcamentoRepository.findByStatus(StatusOrcamento.APROVADO));
        return "pedido/formulario";
    }
    
    @GetMapping
    public String carregarPaginaListagem(Model model) {
        model.addAttribute("listaPedidos", pedidoRepository.findAll(Sort.by("id")));
        return "pedido/listagem";
    }
 
    /*
    @PostMapping
    @Transactional
    public String cadastrar(
        @RequestParam Long orcamentoId,
        @RequestParam Double valorTotal,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataHoraEvento) {
        
        Orcamento orcamento = orcamentoRepository.getReferenceById(orcamentoId);
        Pedido pedido = new Pedido(orcamento, valorTotal, dataHoraEvento);
        pedidoRepository.save(pedido);
        
        return "redirect:/pedido";
    }
   
    
    @PutMapping
    @Transactional
    public String atualizar(
        @RequestParam Long id,
        @RequestParam Double valorTotal,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime dataHoraEvento) {
        
        Pedido pedido = pedidoRepository.getReferenceById(id);
        pedido.setValorTotal(valorTotal);
        pedido.setDtHoraInicio(dataHoraEvento);
        
        return "redirect:/pedido";
    }
     */
    @DeleteMapping
    @Transactional
    public String removerPedido(Long id) {
        pedidoRepository.deleteById(id);
        return "redirect:/pedido";
    }
}
