package com.fateczl.BuffetRafaela.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.OrcamentoItem;
import com.fateczl.BuffetRafaela.entities.Pedido;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.Estados;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoOrcamento;
import com.fateczl.BuffetRafaela.records.DadosCadastroOrcamento;
import com.fateczl.BuffetRafaela.records.ItemQuantidade;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.PedidoRepository;
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

	@Autowired
	private PedidoRepository pedidoRepository;

	@GetMapping("/formulario")
	public String carregaPaginaFormulario(@RequestParam(required = false) Long id, Model model) {
		Orcamento orcamento = id != null ? orcamentoRepository.findById(id).orElse(new Orcamento()) : new Orcamento();

		if (orcamento.getOrcamentoItens() == null || orcamento.getOrcamentoItens().isEmpty()) {
			orcamento.setOrcamentoItens(new ArrayList<>());
			orcamento.getOrcamentoItens().add(new OrcamentoItem());
		}

		orcamento.setUf(orcamento.getUf() != null ? orcamento.getUf() : Estados.SP);

		model.addAttribute("orcamento", orcamento);
		carregarDadosBaseModel(model);

		return "orcamento/formulario";
	}

	@GetMapping
	public String carregaPaginalistagem(Model model) {
		model.addAttribute("orcamentos", orcamentoRepository.findAll(Sort.by("id")));
		return "orcamento/listagem";
	}
	
	@PostMapping
	@Transactional
	public String cadastrar(@Valid @ModelAttribute("orcamento") Orcamento orcamento, 
	                       BindingResult result,
	                       RedirectAttributes redirectAttributes, 
	                       Model model) {

	    if (result.hasErrors()) {
	        carregarDadosBaseModel(model);
	        return "orcamento/formulario";
	    }
	    
	    if (orcamento.getOrcamentoItens() == null) {
	        orcamento.setOrcamentoItens(new ArrayList<>());
	    }
	    
	    try {
	        if (orcamento.getCliente() == null || orcamento.getCliente().getId() == null) {
	            result.rejectValue("cliente", "NotNull", "Selecione um cliente");
	        }
	        
	        if (orcamento.getTema() == null || orcamento.getTema().getId() == null) {
	            result.rejectValue("tema", "NotNull", "Selecione um tema");
	        }

	        if (orcamento.getDtHoraInicio() == null) {
	            result.rejectValue("dtHoraInicio", "NotNull", "Informe a data do evento");
	        } else if (orcamento.getDtHoraInicio().isBefore(LocalDateTime.now())) {
	            result.rejectValue("dtHoraInicio", "Future", "A data do evento deve ser futura");
	        }

	        if (orcamento.getOrcamentoItens() == null || orcamento.getOrcamentoItens().isEmpty()) {
	            result.rejectValue("orcamentoItens", "NotEmpty", "Adicione pelo menos um item");
	        } else {
	            for (int i = 0; i < orcamento.getOrcamentoItens().size(); i++) {
	                OrcamentoItem item = orcamento.getOrcamentoItens().get(i);
	                
	                if (item.getItem() == null || item.getItem().getId() == null) {
	                    result.rejectValue("orcamentoItens["+i+"].item", "NotNull", "Selecione um item válido");
	                    continue;
	                }
	                
	                if (item.getQuantidade() == null || item.getQuantidade() < 1) {
	                    result.rejectValue("orcamentoItens["+i+"].quantidade", "Min", "Quantidade deve ser maior que 0");
	                }
	            }
	        }

	        if (result.hasErrors()) {
	            System.out.println("ERROS DE VALIDAÇÃO: " + result.getAllErrors());
	            carregarDadosBaseModel(model);
	            return "orcamento/formulario";
	        }

	        boolean existeConflito = orcamentoRepository.existsByDataAndLocalAndEnderecoAndStatusNot(
	                orcamento.getDtHoraInicio(), 
	                orcamento.getLogradouro(), 
	                orcamento.getBairro(), 
	                orcamento.getNumero(), 
	                orcamento.getCidade(), 
	                orcamento.getUf(), 
	                orcamento.getCep(), 
	                orcamento.getComplemento(), 
	                StatusOrcamento.REPROVADO);

	        if (existeConflito) {
	            result.reject("error.conflito", "Já existe um orçamento com mesma data, local e endereço");
	            carregarDadosBaseModel(model);
	            return "orcamento/formulario";
	        }

	        orcamento.setStatus(StatusOrcamento.PENDENTE);
	        
	        Cliente cliente = clienteRepository.findById(orcamento.getCliente().getId())
	            .orElseThrow(() -> new IllegalArgumentException("Cliente inválido"));
	        orcamento.setCliente(cliente);
	        
	        Tema tema = temaRepository.findById(orcamento.getTema().getId())
	            .orElseThrow(() -> new IllegalArgumentException("Tema inválido"));
	        orcamento.setTema(tema);

	        for (OrcamentoItem item : orcamento.getOrcamentoItens()) {
	            Item itemDoBanco = itemRepository.findById(item.getItem().getId())
	                .orElseThrow(() -> new IllegalArgumentException("Item inválido"));
	            
	            item.setItem(itemDoBanco);
	            item.setOrcamento(orcamento);
	        }
	        
	        orcamento.getOrcamentoItens().forEach(item -> {
	            System.out.println("Item ID: " + item.getItem().getId() + 
	                              ", Quantidade: " + item.getQuantidade() +
	                              ", Preço: " + item.getItem().getValorVenda());
	        });
	        
	        orcamento.setValorTotal();
	        orcamentoRepository.save(orcamento);
	        
	        redirectAttributes.addFlashAttribute("success", "Orçamento salvo com sucesso!");
	        return "redirect:/orcamento";

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Erro ao cadastrar orçamento: " + e.getMessage());
	        carregarDadosBaseModel(model);
	        return "orcamento/formulario";
	    }
	}

	private void carregarDadosBaseModel(Model model) {
		model.addAttribute("clientes", clienteRepository.findAll());
	    model.addAttribute("temas", temaRepository.findAll());
	    model.addAttribute("items", itemRepository.findAll());
	    model.addAttribute("statusOrcamentoValues", StatusOrcamento.values());
	    model.addAttribute("estados", Estados.values());
	}

	@GetMapping("/editar/{id}")
	public String carregarFormularioEdicao(@PathVariable Long id, Model model) throws JsonProcessingException {
	    Orcamento orcamento = orcamentoRepository.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
	    
	    Hibernate.initialize(orcamento.getOrcamentoItens());
	    
	    List<Map<String, Object>> itensParaFront = new ArrayList<>();
	    for (OrcamentoItem item : orcamento.getOrcamentoItens()) {
	        Map<String, Object> itemMap = new HashMap<>();
	        itemMap.put("itemId", item.getItem().getId());
	        itemMap.put("descricao", item.getItem().getDescricao());
	        itemMap.put("quantidade", item.getQuantidade());
	        itemMap.put("valorVenda", item.getItem().getValorVenda());
	        itensParaFront.add(itemMap);
	    }
	    
	    model.addAttribute("orcamento", orcamento);
	    model.addAttribute("itensJson", new ObjectMapper().writeValueAsString(itensParaFront));
	    model.addAttribute("items", itemRepository.findAll());
	    model.addAttribute("temas", temaRepository.findAll());
	    model.addAttribute("clientes", clienteRepository.findAll());
	    
	    return "orcamento/formulario";
	}

	@PutMapping("/{id}")
	@Transactional
	public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("orcamento") Orcamento orcamento, 
	                       BindingResult result,
	                       RedirectAttributes redirectAttributes, 
	                       Model model) {
	    
	    if (result.hasErrors()) {
	        carregarDadosBaseModel(model);
	        return "orcamento/formulario";
	    }
	    
	    try {
	        Orcamento orcamentoExistente = orcamentoRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

	        if (orcamento.getDtHoraInicio().isBefore(LocalDateTime.now())) {
	            model.addAttribute("error", "A data do evento deve ser futura");
	            carregarDadosBaseModel(model);
	            return "orcamento/formulario";
	        }

	        boolean existeConflito = orcamentoRepository.existsByDataAndLocalAndEnderecoAndStatusNotExcluindoId(
	                orcamento.getDtHoraInicio(), 
	                orcamento.getLogradouro(), 
	                orcamento.getBairro(), 
	                orcamento.getNumero(), 
	                orcamento.getCidade(), 
	                orcamento.getUf(), 
	                orcamento.getCep(), 
	                orcamento.getComplemento(), 
	                StatusOrcamento.REPROVADO, 
	                id);

	        if (existeConflito) {
	            model.addAttribute("error", "Já existe outro orçamento com mesma data, local e endereço");
	            carregarDadosBaseModel(model);
	            return "orcamento/formulario";
	        }

	        orcamentoExistente.setDtHoraInicio(orcamento.getDtHoraInicio());
	        orcamentoExistente.setLogradouro(orcamento.getLogradouro());
	        orcamentoExistente.setBairro(orcamento.getBairro());
	        orcamentoExistente.setNumero(orcamento.getNumero());
	        orcamentoExistente.setCidade(orcamento.getCidade());
	        orcamentoExistente.setUf(orcamento.getUf());
	        orcamentoExistente.setCep(orcamento.getCep());
	        orcamentoExistente.setComplemento(orcamento.getComplemento());
	        orcamentoExistente.setStatus(orcamento.getStatus());

	        if (orcamento.getCliente() != null && orcamento.getCliente().getId() != null) {
	            Cliente cliente = clienteRepository.findById(orcamento.getCliente().getId())
	                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	            orcamentoExistente.setCliente(cliente);
	        }

	        if (orcamento.getTema() != null && orcamento.getTema().getId() != null) {
	            Tema tema = temaRepository.findById(orcamento.getTema().getId())
	                    .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));
	            orcamentoExistente.setTema(tema);
	        }

	        orcamentoExistente.getOrcamentoItens().clear();
	        if (orcamento.getOrcamentoItens() != null) {
	            for (OrcamentoItem item : orcamento.getOrcamentoItens()) {
	                Item itemDoBanco = itemRepository.findById(item.getItem().getId())
	                        .orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + item.getItem().getId()));
	                
	                OrcamentoItem novoItem = new OrcamentoItem();
	                novoItem.setItem(itemDoBanco);
	                novoItem.setQuantidade(item.getQuantidade());
	                novoItem.getItem().setValorVenda(itemDoBanco.getValorVenda());
	                novoItem.setOrcamento(orcamentoExistente);
	                
	                orcamentoExistente.getOrcamentoItens().add(novoItem);
	            }
	        }

	        orcamentoExistente.setValorTotal();
	        
	        orcamentoRepository.save(orcamentoExistente);
	        redirectAttributes.addFlashAttribute("success", "Orçamento atualizado com sucesso!");
	        return "redirect:/orcamento";

	    } catch (Exception e) {
	        model.addAttribute("error", "Erro ao atualizar orçamento: " + e.getMessage());
	        carregarDadosBaseModel(model);
	        return "orcamento/formulario";
	    }
	}

	@DeleteMapping
	@Transactional
	public String removeOrcamento(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			var orcamento = orcamentoRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

			if (orcamento.getStatus() == StatusOrcamento.APROVADO
					|| orcamento.getStatus() == StatusOrcamento.REPROVADO) {
				redirectAttributes.addFlashAttribute("error",
						"Não é possível remover um orçamento com status APROVADO ou REPROVADO");
				return "redirect:/orcamento";
			}

			orcamento.getOrcamentoItens().forEach(orcamentoItem -> {
				Item item = orcamentoItem.getItem();
			});
			orcamento.getOrcamentoItens().clear();

			orcamentoRepository.delete(orcamento);

			redirectAttributes.addFlashAttribute("success", "Orçamento removido com sucesso!");
			return "redirect:/orcamento";

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/orcamento";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erro ao remover orçamento: " + e.getMessage());
			return "redirect:/orcamento";
		}
	}

	@PostMapping("/submitOrcamento")
	@Transactional
	public String submitOrcamento(@RequestParam Long clienteId, @RequestParam Long temaId,
			@RequestParam(required = false) List<Long> itemIds,
			@RequestParam(required = false) List<Integer> quantidades, @RequestParam LocalDateTime dtHoraInicio,
			@RequestParam String status, @RequestParam String logradouro, @RequestParam String numero,
			@RequestParam String complemento, @RequestParam String bairro, @RequestParam String cidade,
			@RequestParam String uf, @RequestParam String cep, RedirectAttributes redirectAttributes) {
		try {
			Estados estado = Estados.valueOf(uf);
			StatusOrcamento statusOrcamento = StatusOrcamento.valueOf(status);

			if (dtHoraInicio.isBefore(LocalDateTime.now())) {
				redirectAttributes.addFlashAttribute("error", "A data do evento deve ser futura");
				return "redirect:/orcamento/formulario";
			}

			var cliente = clienteRepository.findById(clienteId)
					.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
			var tema = temaRepository.findById(temaId)
					.orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));

			List<ItemQuantidade> itensComQuantidade = new ArrayList<>();
			if (itemIds != null && quantidades != null && itemIds.size() == quantidades.size()) {
				for (int i = 0; i < itemIds.size(); i++) {
					Long itemId = itemIds.get(i);
					Integer quantidade = quantidades.get(i);
					if (quantidade != null && quantidade > 0) {
						var item = itemRepository.findById(itemId)
								.orElseThrow(() -> new IllegalArgumentException("Item não encontrado: " + itemId));
						itensComQuantidade.add(new ItemQuantidade(item, quantidade));
					}
				}
			}

			DadosCadastroOrcamento dados = new DadosCadastroOrcamento(cliente, tema, itensComQuantidade, dtHoraInicio,
					statusOrcamento, logradouro, numero, bairro, cidade, estado, cep, complemento);

			Orcamento orcamento = new Orcamento(dados);
			orcamentoRepository.save(orcamento);

			if (statusOrcamento == StatusOrcamento.APROVADO) {
				Pedido pedido = new Pedido(orcamento);
				pedidoRepository.save(pedido);
			}

			redirectAttributes.addFlashAttribute("success", "Orçamento submetido com sucesso!");
			return "redirect:/orcamento";

		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/orcamento";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Erro ao submeter orçamento: " + e.getMessage());
			return "redirect:/orcamento";
		}
	}
}