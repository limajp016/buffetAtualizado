package com.fateczl.BuffetRafaela.controller;

import java.io.IOException;
import java.util.Base64;
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
import org.springframework.web.multipart.MultipartFile;

import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoItem;
import com.fateczl.BuffetRafaela.records.DadosCadastroItem;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/formulario")
    public String carregaPaginaFormulario(Long id, Model model) {
        if (id != null) {
            var item = repository.getReferenceById(id);
            model.addAttribute("item", item);
        }
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "item/formulario";
    }

    @GetMapping
    public String carregaPaginaListagem(Model model) {
        List<Item> itens = repository.findAll(Sort.by("descricao").ascending());

        for (Item item : itens) {
            if (item.getImagem() != null) {
                String imagemBase64 = Base64.getEncoder().encodeToString(item.getImagem());
                item.setImagemBase64(imagemBase64);            }
        }

        model.addAttribute("listaItens", itens);
        return "item/listagem";
    }

    @PostMapping
    @Transactional
    public String cadastrar(@RequestParam Long categoriaId,
                            @RequestParam("descricao") String descricao,
                            @RequestParam("valorCusto") Double valorCusto,
                            @RequestParam("valorVenda") Double valorVenda,
                            @RequestParam("campoDesc") String campoDesc,
                            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        var categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

        byte[] imagemBytes = imagem != null && !imagem.isEmpty() ? imagem.getBytes() : null;

        var dados = new DadosCadastroItem(descricao, valorCusto, valorVenda, campoDesc, imagemBytes, categoria);
        var item = new Item(dados);

        repository.save(item);
        return "redirect:/item";
    }

    @PutMapping
    @Transactional
    public String atualizar(@RequestParam Long id,
                            @RequestParam(required = false) String descricao,
                            @RequestParam(required = false) Double valorCusto,
                            @RequestParam(required = false) Double valorVenda,
                            @RequestParam(required = false) String campoDesc,
                            @RequestParam(required = false) Long categoriaId,
                            @RequestParam(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        var item = repository.getReferenceById(id);
        byte[] imagemBytes = imagem != null && !imagem.isEmpty() ? imagem.getBytes() : null;

        var categoria = categoriaId != null ? categoriaRepository.findById(categoriaId).orElse(null) : null;

        var dados = new DadosAtualizacaoItem(descricao, valorCusto, valorVenda, campoDesc, imagemBytes, categoria);

        item.atualizarInformacoes(dados);
        return "redirect:/item";
    }

    @DeleteMapping
    @Transactional
    public String removeItem(Long id) {
        repository.deleteById(id);
        return "redirect:/item";
    }
}

