package com.fateczl.BuffetRafaela.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoItem;
import com.fateczl.BuffetRafaela.records.DadosCadastroItem;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;
import com.fateczl.BuffetRafaela.repositories.OrcamentoItemRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private OrcamentoItemRepository orcamentoItemRepository;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        Item item = (id != null) ? repository.findById(id).orElse(new Item()) : new Item();
        
        if (item.getImagem() != null) {
            String imagemBase64 = Base64.getEncoder().encodeToString(item.getImagem());
            item.setImagemBase64(imagemBase64);
        }
        
        model.addAttribute("item", item);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "item/formulario";
    }

    @GetMapping
    public String carregaPaginaListagem(Model model) {
        List<Item> itens = repository.findAll(Sort.by("descricao").ascending());

        itens.forEach(item -> {
            if (item.getImagem() != null) {
                item.setImagemBase64(Base64.getEncoder().encodeToString(item.getImagem()));
            }
        });

        model.addAttribute("listaItens", itens);
        return "item/listagem";
    }

    @PostMapping
    @Transactional
    public String cadastrar(
            @RequestParam Long categoriaId,
            @RequestParam String descricao,
            @RequestParam Double valorCusto,
            @RequestParam Double valorVenda,
            @RequestParam String campoDesc,
            @RequestParam(required = false) MultipartFile imagem,
            RedirectAttributes redirectAttributes) throws IOException {

        try {
            var categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

            byte[] imagemBytes = imagem != null && !imagem.isEmpty() ? imagem.getBytes() : null;

            var dados = new DadosCadastroItem(descricao, valorCusto, valorVenda, campoDesc, imagemBytes, categoria);
            var item = new Item(dados);

            repository.save(item);
            redirectAttributes.addFlashAttribute("mensagem", "Item cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar item: " + e.getMessage());
        }

        return "redirect:/item";
    }

    @PutMapping
    @Transactional
    public String atualizar(
            @RequestParam Long id,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) Double valorCusto,
            @RequestParam(required = false) Double valorVenda,
            @RequestParam(required = false) String campoDesc,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) MultipartFile imagem,
            RedirectAttributes redirectAttributes) throws IOException {

        try {
            var item = repository.getReferenceById(id);
            byte[] imagemBytes = imagem != null && !imagem.isEmpty() ? imagem.getBytes() : item.getImagem();

            var categoria = categoriaId != null ? 
                    categoriaRepository.findById(categoriaId).orElse(null) : 
                    item.getCategoria();

            var dados = new DadosAtualizacaoItem(
                    descricao != null ? descricao : item.getDescricao(),
                    valorCusto != null ? valorCusto : item.getValorCusto(),
                    valorVenda != null ? valorVenda : item.getValorVenda(),
                    campoDesc != null ? campoDesc : item.getCampoDesc(),
                    imagemBytes,
                    categoria
            );

            item.atualizarInformacoes(dados);
            redirectAttributes.addFlashAttribute("mensagem", "Item atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar item: " + e.getMessage());
        }

        return "redirect:/item";
    }

    @DeleteMapping
    @Transactional
    public String removeItem(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!repository.existsById(id)) {
                redirectAttributes.addFlashAttribute("erro", "Item não encontrado!");
                return "redirect:/item";
            }
            
            if (orcamentoItemRepository.existsByItemId(id)) {
                redirectAttributes.addFlashAttribute(
                    "erroVinculado", 
                    "Não é possível remover: item vinculado a orçamentos."
                );
                return "redirect:/item";
            }
            
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagem", "Item removido com sucesso!");
            
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                "erroVinculado", 
                "Não é possível remover: item vinculado a outros registros."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover item: " + e.getMessage());
        }
        
        return "redirect:/item";
    }
}