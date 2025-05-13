package com.fateczl.BuffetRafaela.controller;

import java.time.LocalDateTime;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.enums.StatusOrcamento;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoOrcamento;
import com.fateczl.BuffetRafaela.records.DadosCadastroOrcamento;
import com.fateczl.BuffetRafaela.records.ItemQuantidade;
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
                            @RequestParam(required = false) List<Long> itemIds,
                            @RequestParam(required = false) List<Integer> quantidades,
                            @Valid DadosCadastroOrcamento dados,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            if (orcamentoRepository.existsByDataAndLocalAndEndereco(
                    dados.dtHoraInicio(),
                    dados.logradouro(),
                    dados.bairro(),
                    dados.cidade(),
                    dados.uf(),
                    dados.cep())) {
                model.addAttribute("error", "Já existe um orçamento com mesma data, local e endereço");
                return carregarFormularioNovo(model, clienteId, temaId, itemIds, dados);
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

            DadosCadastroOrcamento dadosComItens = new DadosCadastroOrcamento(
                cliente,
                tema,
                itensComQuantidade,
                dados.dtHoraInicio(),
                dados.status(),
                dados.logradouro(),
                dados.bairro(),
                dados.cidade(),
                dados.uf(),
                dados.cep()
            );

            var orcamento = new Orcamento(dadosComItens);
            orcamentoRepository.save(orcamento);
            redirectAttributes.addFlashAttribute("success", "Orçamento cadastrado com sucesso!");
            return "redirect:/orcamento";

        } catch (IllegalStateException e) {
            model.addAttribute("error", "Erro: " + e.getMessage());
            return carregarFormularioNovo(model, clienteId, temaId, itemIds, dados);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return carregarFormularioNovo(model, clienteId, temaId, itemIds, dados);
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao cadastrar orçamento: " + e.getMessage());
            return carregarFormularioNovo(model, clienteId, temaId, itemIds, dados);
        }
    }

    private String carregarFormularioNovo(Model model, Long clienteId, Long temaId, List<Long> itens, DadosCadastroOrcamento dados) {
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("temas", temaRepository.findAll());
        model.addAttribute("items", itemRepository.findAll());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("temaId", temaId);
        model.addAttribute("itens", itens);
        model.addAttribute("dados", dados);
        model.addAttribute("statusOrcamentoValues", StatusOrcamento.values());
        return "orcamento/formulario";
    }
    
    @PutMapping
    @Transactional
    public String atualizar(@RequestParam Long id,
                            @RequestParam Long clienteId,
                            @RequestParam Long temaId,
                            @RequestParam(required = false) List<Long> itemIds,
                            @RequestParam(required = false) List<Integer> quantidades,
                            @Valid DadosAtualizacaoOrcamento dados,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        try {
            var orcamento = orcamentoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

            boolean existeConflito = orcamentoRepository.existsByDataAndLocalAndEnderecoExcluindoId(
                dados.dtHoraInicio(),
                dados.logradouro(),
                dados.bairro(),
                dados.cidade(),
                dados.uf(),
                dados.cep(),
                orcamento.getId()
            );

            if (existeConflito) {
                model.addAttribute("error", "Já existe outro orçamento com mesma data, local e endereço");
                return carregarFormularioEdicao(id, model, clienteId, temaId, itemIds, dados);
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

            DadosAtualizacaoOrcamento dadosAtualizados = new DadosAtualizacaoOrcamento(
                id,
                cliente,
                tema,
                itensComQuantidade,
                dados.dtHoraInicio(),
                dados.status(),
                dados.logradouro(),
                dados.bairro(),
                dados.cidade(),
                dados.uf(),
                dados.cep()
            );

            orcamento.atualizarInformacoes(dadosAtualizados);
            orcamentoRepository.save(orcamento);
            redirectAttributes.addFlashAttribute("success", "Orçamento atualizado com sucesso!");
            return "redirect:/orcamento";

        } catch (IllegalStateException e) {
            model.addAttribute("error", "Erro: " + e.getMessage());
            return carregarFormularioEdicao(id, model, clienteId, temaId, itemIds, dados);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return carregarFormularioEdicao(id, model, clienteId, temaId, itemIds, dados);
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao atualizar orçamento: " + e.getMessage());
            return carregarFormularioEdicao(id, model, clienteId, temaId, itemIds, dados);
        }
    }

    private String carregarFormularioEdicao(Long id, Model model, Long clienteId, Long temaId, List<Long> itens, DadosAtualizacaoOrcamento dados) {
        var orcamento = orcamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
        
        model.addAttribute("orcamento", orcamento);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("temas", temaRepository.findAll());
        model.addAttribute("items", itemRepository.findAll());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("temaId", temaId);
        model.addAttribute("itens", itens);
        model.addAttribute("dados", dados);
        model.addAttribute("statusOrcamentoValues", StatusOrcamento.values());
        return "orcamento/formulario";
    }
    
    @DeleteMapping
    @Transactional
    public String removeOrcamento(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            var orcamento = orcamentoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
            
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
    public String submitOrcamento(@RequestParam Long clienteId,
                                  @RequestParam Long temaId,
                                  @RequestParam(required = false) List<Long> itemIds,
                                  @RequestParam(required = false) List<Integer> quantidades,
                                  @RequestParam LocalDateTime dtHoraInicio,
                                  @RequestParam String status,
                                  @RequestParam String logradouro,
                                  @RequestParam String bairro,
                                  @RequestParam String cidade,
                                  @RequestParam String uf,
                                  @RequestParam String cep,
                                  RedirectAttributes redirectAttributes) {
        try {
            var cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            var tema = temaRepository.findById(temaId)
                    .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));

            StatusOrcamento statusOrcamento = StatusOrcamento.valueOf(status);

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

            DadosCadastroOrcamento dados = new DadosCadastroOrcamento(
                cliente,
                tema,
                itensComQuantidade,
                dtHoraInicio,
                statusOrcamento,
                logradouro,
                bairro,
                cidade,
                uf,
                cep
            );

            Orcamento orcamento = new Orcamento(dados);
            orcamentoRepository.save(orcamento);

            redirectAttributes.addFlashAttribute("success", "Orçamento submetido com sucesso!");
            return "redirect:/orcamento";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Erro: " + e.getMessage());
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