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

import com.fateczl.BuffetRafaela.entities.Tema;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoTema;
import com.fateczl.BuffetRafaela.records.DadosCadastroTema;
import com.fateczl.BuffetRafaela.repositories.OrcamentoRepository;
import com.fateczl.BuffetRafaela.repositories.TemaRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tema")
public class TemaController {
    
    @Autowired
    private TemaRepository repository;
    
    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
        Tema tema = (id != null) ? repository.findById(id).orElse(new Tema()) : new Tema();
        
        if (tema.getImagem() != null) {
            String imagemBase64 = Base64.getEncoder().encodeToString(tema.getImagem());
            tema.setImagemBase64(imagemBase64);
        }
        
        model.addAttribute("tema", tema);
        return "tema/formulario";
    }

    @GetMapping
    public String carregaPaginaListagem(Model model) {
        List<Tema> temas = repository.findAll(Sort.by("descricao").ascending());

        temas.forEach(tema -> {
            if (tema.getImagem() != null) {
                tema.setImagemBase64(Base64.getEncoder().encodeToString(tema.getImagem()));
            }
        });

        model.addAttribute("listaTemas", temas);
        return "tema/listagem";
    }

    @PostMapping
    @Transactional
    public String cadastrar(@Valid DadosCadastroTema dados, 
                          @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile,
                          RedirectAttributes redirectAttributes) throws IOException {
        
        try {
            byte[] imagemBytes = (imagemFile != null && !imagemFile.isEmpty()) ? 
                               imagemFile.getBytes() : null;
            
            Tema tema = new Tema(dados);
            tema.setImagem(imagemBytes);
            repository.save(tema);
            
            redirectAttributes.addFlashAttribute("mensagem", "Tema cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar tema: " + e.getMessage());
        }
        
        return "redirect:/tema";
    }

    @PutMapping
    @Transactional
    public String atualizar(@Valid DadosAtualizacaoTema dados, 
                          @RequestParam(value = "imagemFile", required = false) MultipartFile imagemFile,
                          RedirectAttributes redirectAttributes) throws IOException {
        
        try {
            Tema tema = repository.findById(dados.id())
                .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));
            
            byte[] imagemBytes = (imagemFile != null && !imagemFile.isEmpty()) ? 
                               imagemFile.getBytes() : tema.getImagem();
            
            DadosAtualizacaoTema dadosAtualizados = new DadosAtualizacaoTema(
                dados.id(), dados.descricao(), dados.preco(), imagemBytes
            );
            
            tema.atualizarInformacoes(dadosAtualizados);
            
            redirectAttributes.addFlashAttribute("mensagem", "Tema atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar tema: " + e.getMessage());
        }
        
        return "redirect:/tema";
    }

    @DeleteMapping
    @Transactional
    public String removeTema(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!repository.existsById(id)) {
                redirectAttributes.addFlashAttribute("erro", "Tema não encontrado!");
                return "redirect:/tema";
            }
            
            if (orcamentoRepository.existsByTemaId(id)) {
                redirectAttributes.addFlashAttribute(
                    "erroVinculado", 
                    "Não é possível remover: tema vinculado a orçamentos."
                );
                return "redirect:/tema";
            }
            
            repository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagem", "Tema removido com sucesso!");
            
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                "erroVinculado", 
                "Não é possível remover: tema vinculado a outros registros."
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover tema: " + e.getMessage());
        }
        
        return "redirect:/tema";
    }
}
