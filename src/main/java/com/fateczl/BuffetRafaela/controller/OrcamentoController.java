package com.fateczl.BuffetRafaela.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.ItemOrcamento;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.repositories.ClienteRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;
import com.fateczl.BuffetRafaela.services.EmailService;
import com.fateczl.BuffetRafaela.services.OrcamentoService;

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
	private OrcamentoService orcamentoService;

	@Autowired
	private EmailService emailService;

	private static final Logger logger = LoggerFactory.getLogger(OrcamentoController.class);

	@GetMapping("/formulario")
	public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
		Orcamento orcamento = id == null ? new Orcamento()
				: orcamentoRepository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

		if (orcamento.getId() != null) {
			Hibernate.initialize(orcamento.getItens());
		}

		model.addAttribute("orcamento", orcamento);
		model.addAttribute("clientes", clienteRepository.findAll(Sort.by("nome")));
		model.addAttribute("temas", temaRepository.findAll(Sort.by("descricao")));
		model.addAttribute("itens", itemRepository.findAll(Sort.by("descricao")));

		List<Long> selectedItemIds = orcamento.getItens().stream().map(itemOrcamento -> itemOrcamento.getItem().getId())
				.collect(Collectors.toList());

		if (selectedItemIds == null) {
			selectedItemIds = new ArrayList<>();
		}
		model.addAttribute("selectedItemIds", selectedItemIds);
		model.addAttribute("selectedItemIds", selectedItemIds);

		return "orcamento/formulario";
	}

	@GetMapping
	public String listarOrcamentos(@RequestParam(defaultValue = "0") int page,
	                               @RequestParam(defaultValue = "8") int size,
	                               Model model) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
	    Page<Orcamento> pagina = orcamentoRepository.findAll(pageable);

	    model.addAttribute("pagina", pagina);
	    return "orcamento/listagem";
	}

	@PostMapping
	@Transactional
	public String cadastrar(@RequestParam(name = "clienteId", required = true) Long clienteId,
			@RequestParam(name = "temaId", required = true) Long temaId, @ModelAttribute @Valid Orcamento orcamento,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (clienteId == null || temaId == null) {
			bindingResult.rejectValue("cliente", "field.required", "Cliente é obrigatório");
			bindingResult.rejectValue("tema", "field.required", "Tema é obrigatório");
		}

		boolean hasItemNulo = orcamento.getItens().stream().anyMatch(itemOrcamento -> itemOrcamento.getItem() == null);

		if (hasItemNulo) {
			bindingResult.rejectValue("itens", "field.required", "Todos os itens devem estar preenchidos.");
			model.addAttribute("error", "Todos os itens devem ser preenchidos.");
			recarregarDadosFormulario(model);
			model.addAttribute("orcamento", orcamento);
			return "orcamento/formulario";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "Verifique os campos obrigatórios");
			recarregarDadosFormulario(model);
			model.addAttribute("orcamento", orcamento);
			return "orcamento/formulario";
		}

		try {
			List<Long> itens = orcamento.getItens().stream().map(itemOrcamento -> itemOrcamento.getItem().getId())
					.collect(Collectors.toList());
			List<Integer> quantidades = orcamento.getItens().stream().map(ItemOrcamento::getQuantidade)
					.collect(Collectors.toList());

			validarParametros(itens, quantidades);

			Cliente cliente = buscarCliente(clienteId);
			Tema tema = buscarTema(temaId);
			Map<Long, Item> mapaItemPorId = buscarItens(itens).stream()
					.collect(Collectors.toMap(Item::getId, item -> item));

			List<Item> itemsEncontrados = itens.stream().map(mapaItemPorId::get).collect(Collectors.toList());

			orcamento.setCliente(cliente);
			orcamento.setTema(tema);

			orcamento.setStatus(StatusOrcamento.PENDENTE);

			orcamento.getItens().clear();
			adicionarItensAoOrcamento(orcamento, itemsEncontrados, quantidades);

			try {
				orcamentoService.validarEnderecoEDataDuplicados(orcamento);
			} catch (IllegalArgumentException e) {
				bindingResult.reject("validacao.enderecoDataDuplicados", e.getMessage());
				model.addAttribute("error", e.getMessage());
				recarregarDadosFormulario(model);
				model.addAttribute("orcamento", orcamento);
				return "orcamento/formulario";
			}
			
			orcamento.setDataEnvioEmail(LocalDateTime.now());

			orcamentoRepository.save(orcamento);

			emailService.enviarEmailAprovacao(orcamento);

			Map<String, Object> props = new HashMap<>();
			props.put("orcamento", orcamento);
			props.put("linkAprovacao", "http://localhost:8080/orcamento/aprovar/" + orcamento.getId());
			props.put("linkReprovacao", "http://localhost:8080/orcamento/reprovar/" + orcamento.getId());

			emailService.enviarEmailTemplate(orcamento.getCliente().getEmail(), "Aprovação de Orçamento",
					"email-aprovacao", props);

			enviarMensagemSucesso(redirectAttributes, orcamento);

			return "redirect:/orcamento";

		} catch (IllegalArgumentException e) {
			return tratarErroValidacao(e, model, orcamento);
		} catch (Exception e) {
			return tratarErroInesperado(e, model, orcamento);
		}
	}

	private void validarParametros(List<Long> itens, List<Integer> quantidades) {
		if (itens == null || quantidades == null) {
			throw new IllegalArgumentException("Lista de itens ou quantidades não pode ser nula");
		}
		if (itens.isEmpty()) {
			throw new IllegalArgumentException("Pelo menos um item deve ser selecionado");
		}
		if (itens.size() != quantidades.size()) {
			throw new IllegalArgumentException("Quantidade de itens e quantidades não correspondem");
		}
	}

	private Cliente buscarCliente(Long clienteId) {
		return clienteRepository.findById(clienteId)
				.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado - ID: " + clienteId));
	}

	private Tema buscarTema(Long temaId) {
		return temaRepository.findById(temaId)
				.orElseThrow(() -> new IllegalArgumentException("Tema não encontrado - ID: " + temaId));
	}

	private List<Item> buscarItens(List<Long> itensIds) {
		List<Item> itemsEncontrados = itemRepository.findAllById(itensIds);

		if (itemsEncontrados.size() != itensIds.size()) {
			List<Long> idsNaoEncontrados = itensIds.stream()
					.filter(id -> itemsEncontrados.stream().noneMatch(item -> item.getId().equals(id)))
					.collect(Collectors.toList());

			throw new IllegalArgumentException("Itens não encontrados: " + idsNaoEncontrados);
		}

		return itemsEncontrados;
	}

	private void adicionarItensAoOrcamento(Orcamento orcamento, List<Item> itens, List<Integer> quantidades) {
		for (int i = 0; i < itens.size(); i++) {
			int quantidade = quantidades.get(i);
			if (quantidade <= 0) {
				throw new IllegalArgumentException(
						"Quantidade inválida (" + quantidade + ") para o item: " + itens.get(i).getDescricao());
			}
			orcamento.addItem(itens.get(i), quantidade);
		}
		orcamento.calcularValorTotal();
	}

	private void enviarMensagemSucesso(RedirectAttributes redirectAttributes, Orcamento orcamento) {
		redirectAttributes.addFlashAttribute("success", "Orçamento #" + orcamento.getId() + " cadastrado com sucesso!");
	}

	private String tratarErroValidacao(IllegalArgumentException e, Model model, @Valid Orcamento orcamento) {
		logger.error("Erro de validação: " + e.getMessage(), e);
		model.addAttribute("error", e.getMessage());
		recarregarDadosFormulario(model);
		model.addAttribute("orcamento", orcamento);
		return "orcamento/formulario";
	}

	private String tratarErroInesperado(Exception e, Model model, @Valid Orcamento orcamento) {
		logger.error("Erro inesperado ao cadastrar orçamento", e);
		model.addAttribute("error", "Erro inesperado: " + e.getMessage() + ". Por favor, tente novamente.");
		recarregarDadosFormulario(model);
		model.addAttribute("orcamento", orcamento);
		return "orcamento/formulario";
	}

	private void recarregarDadosFormulario(Model model) {
		model.addAttribute("clientes", clienteRepository.findAll(Sort.by("nome")));
		model.addAttribute("temas", temaRepository.findAll(Sort.by("descricao")));
		model.addAttribute("itens", itemRepository.findAll(Sort.by("descricao")));
	}

	@PutMapping("/{id}")
	@Transactional
	public String atualizar(@PathVariable(name = "id") Long id,
			@RequestParam(name = "clienteId", required = true) Long clienteId,
			@RequestParam(name = "temaId", required = true) Long temaId, @ModelAttribute @Valid Orcamento orcamento,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (clienteId == null || temaId == null) {
			bindingResult.rejectValue("cliente", "field.required", "Cliente é obrigatório");
			bindingResult.rejectValue("tema", "field.required", "Tema é obrigatório");
		}

		boolean hasItemNulo = orcamento.getItens().stream().anyMatch(itemOrcamento -> itemOrcamento.getItem() == null);

		if (hasItemNulo) {
			bindingResult.rejectValue("itens", "field.required", "Todos os itens devem estar preenchidos.");
			model.addAttribute("error", "Todos os itens devem ser preenchidos.");
			recarregarDadosFormulario(model);
			model.addAttribute("orcamento", orcamento);
			return "orcamento/formulario";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", "Verifique os campos obrigatórios");
			recarregarDadosFormulario(model);
			model.addAttribute("orcamento", orcamento);
			return "orcamento/formulario";
		}

		try {
			Orcamento orcamentoExistente = orcamentoRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

			List<Long> itens = orcamento.getItens().stream().map(itemOrcamento -> itemOrcamento.getItem().getId())
					.collect(Collectors.toList());

			List<Integer> quantidades = orcamento.getItens().stream().map(ItemOrcamento::getQuantidade)
					.collect(Collectors.toList());

			validarParametros(itens, quantidades);

			Cliente cliente = buscarCliente(clienteId);
			Tema tema = buscarTema(temaId);
			Map<Long, Item> mapaItemPorId = buscarItens(itens).stream()
					.collect(Collectors.toMap(Item::getId, item -> item));

			List<Item> itemsEncontrados = itens.stream().map(mapaItemPorId::get).collect(Collectors.toList());

			orcamentoExistente.setCliente(cliente);
			orcamentoExistente.setTema(tema);
			orcamentoExistente.setDtHoraInicio(orcamento.getDtHoraInicio());
			orcamentoExistente.setValorTotal(orcamento.getValorTotal());

			orcamentoExistente.setLogradouro(orcamento.getLogradouro());
			orcamentoExistente.setNumero(orcamento.getNumero());
			orcamentoExistente.setBairro(orcamento.getBairro());
			orcamentoExistente.setCidade(orcamento.getCidade());
			orcamentoExistente.setUf(orcamento.getUf());
			orcamentoExistente.setCep(orcamento.getCep());
			orcamentoExistente.setComplemento(orcamento.getComplemento());

			orcamentoExistente.getItens().clear();
			adicionarItensAoOrcamento(orcamentoExistente, itemsEncontrados, quantidades);

			try {
				orcamentoService.validarEnderecoEDataDuplicados(orcamentoExistente, id);
			} catch (IllegalArgumentException e) {
				bindingResult.reject("validacao.enderecoDataDuplicados", e.getMessage());
				model.addAttribute("error", e.getMessage());
				recarregarDadosFormulario(model);
				model.addAttribute("orcamento", orcamento);
				return "orcamento/formulario";
			}
			
			orcamentoExistente.setDataEnvioEmail(LocalDateTime.now());
			orcamentoRepository.save(orcamentoExistente);

			if (orcamentoExistente.getStatus() == StatusOrcamento.PENDENTE) {
				emailService.enviarEmailAprovacao(orcamentoExistente);
			}

			enviarMensagemSucessoAtualizacao(redirectAttributes, orcamentoExistente);

			return "redirect:/orcamento";

		} catch (IllegalArgumentException e) {
			return tratarErroValidacao(e, model, orcamento);
		} catch (Exception e) {
			return tratarErroInesperado(e, model, orcamento);
		}
	}

	private void enviarMensagemSucessoAtualizacao(RedirectAttributes redirectAttributes, Orcamento orcamento) {
		redirectAttributes.addFlashAttribute("success", "Orçamento #" + orcamento.getId() + " atualizado com sucesso!");
	}

	@GetMapping("/aprovar/{id}")
	public String aprovarOrcamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		boolean aprovado = orcamentoService.aprovarOrcamento(id);
		if (aprovado) {
			redirectAttributes.addFlashAttribute("success", "Orçamento aprovado com sucesso!");
		} else {
			redirectAttributes.addFlashAttribute("error", "Não foi possível aprovar. Orçamento já processado.");
		}
		return "redirect:/orcamento";
	}

	@GetMapping("/reprovar/{id}")
	public String reprovarOrcamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		boolean reprovado = orcamentoService.reprovarOrcamento(id);
		if (reprovado) {
			redirectAttributes.addFlashAttribute("success", "Orçamento reprovado com sucesso!");
		} else {
			redirectAttributes.addFlashAttribute("error", "Não foi possível reprovar. Orçamento já processado.");
		}
		return "redirect:/orcamento";
	}

	@DeleteMapping
	@Transactional
	public String removeOrcamento(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		var orcamento = orcamentoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

		if (orcamento.getStatus() == StatusOrcamento.APROVADO || orcamento.getStatus() == StatusOrcamento.REPROVADO) {
			redirectAttributes.addFlashAttribute("error",
					"Não é permitido remover orçamentos com status APROVADO ou REPROVADO.");
			return "redirect:/orcamento";
		}

		orcamentoRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("success", "Orçamento removido com sucesso.");
		return "redirect:/orcamento";
	}
	
	@GetMapping("/rastreamento/orcamento/{id}")
	public ResponseEntity<Void> registrarAberturaEmail(@PathVariable Long id) {
	    orcamentoService.registrarAberturaEmail(id);
	    return ResponseEntity.ok().build();
	}


}