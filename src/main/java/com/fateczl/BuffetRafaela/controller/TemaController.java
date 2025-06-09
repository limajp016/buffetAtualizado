package com.fateczl.BuffetRafaela.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Controller
@RequestMapping("/tema")
public class TemaController {

    private final TemaRepository repository;
    private final OrcamentoRepository orcamentoRepository;

    public TemaController(TemaRepository repository, OrcamentoRepository orcamentoRepository) {
        this.repository = repository;
        this.orcamentoRepository = orcamentoRepository;
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {
        Tema tema = (id != null) ? repository.findById(id).orElse(new Tema()) : new Tema();
        
        if (tema.getImagem() != null) {
            String mimeType = determineImageMimeType(tema.getImagem());
            tema.setImagemBase64("data:" + mimeType + ";base64," + 
                Base64.getEncoder().encodeToString(tema.getImagem()));
        }
        
        model.addAttribute("tema", tema);
        return "tema/formulario";
    }

    @GetMapping
    public String listarTemas(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "4") int size,
                              Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("descricao").ascending());
        Page<Tema> pagina = repository.findAll(pageable);

        pagina.forEach(tema -> {
            if (tema.getImagem() != null) {
                String mimeType = determineImageMimeType(tema.getImagem());
                tema.setImagemBase64("data:" + mimeType + ";base64," +
                        Base64.getEncoder().encodeToString(tema.getImagem()));
            }
        });

        model.addAttribute("pagina", pagina);
        return "tema/listagem";
    }


    @PostMapping
    @Transactional
    public String cadastrar(@RequestParam String descricao,
                          @RequestParam Double preco,
                          @RequestParam(required = false) MultipartFile imagem,
                          RedirectAttributes redirectAttributes,
                          Model model) throws IOException {
        
        try {
            byte[] imagemBytes = null;
            
            if (imagem != null && !imagem.isEmpty()) {
                long maxFileSize = 2 * 1024 * 1024;
                if (imagem.getSize() > maxFileSize) {
                    model.addAttribute("erroImagem", "O tamanho da imagem não pode exceder 2MB");
                    return "tema/formulario";
                }

                String contentType = imagem.getContentType();
                if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                    model.addAttribute("erroImagem", "Apenas arquivos JPEG e PNG são permitidos");
                    return "tema/formulario";
                }

                imagemBytes = compressImage(imagem);
                if (imagemBytes.length > 1_000_000) {
                    model.addAttribute("erroImagem",
                            "A imagem é muito grande mesmo após compressão. Por favor, use uma imagem menor.");
                    return "tema/formulario";
                }
            }
            
            var dados = new DadosCadastroTema(descricao, preco, imagemBytes);
            Tema tema = new Tema(dados);
            repository.save(tema);
            
            redirectAttributes.addFlashAttribute("mensagem", "Tema cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao cadastrar tema: " + (e.getMessage().contains("max_allowed_packet")
                            ? "A imagem é muito grande para o banco de dados. Por favor, reduza o tamanho."
                            : e.getMessage()));
            return "redirect:/tema/formulario";
        }
        
        return "redirect:/tema";
    }

    @PutMapping
    @Transactional
    public String atualizar(@RequestParam Long id,
                          @RequestParam(required = false) String descricao,
                          @RequestParam(required = false) Double preco,
                          @RequestParam(required = false) MultipartFile imagem,
                          @RequestParam(required = false, defaultValue = "false") boolean removerImagem,
                          RedirectAttributes redirectAttributes,
                          Model model) throws IOException {
        
        try {
            Tema tema = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tema não encontrado"));
            
            byte[] imagemBytes = null;

            if (removerImagem) {
                imagemBytes = null;
            } else if (imagem != null && !imagem.isEmpty()) {
                long maxFileSize = 2 * 1024 * 1024;
                if (imagem.getSize() > maxFileSize) {
                    model.addAttribute("erroImagem", "O tamanho da imagem não pode exceder 2MB");
                    model.addAttribute("tema", tema);
                    return "tema/formulario";
                }

                String contentType = imagem.getContentType();
                if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                    model.addAttribute("erroImagem", "Apenas arquivos JPEG e PNG são permitidos");
                    model.addAttribute("tema", tema);
                    return "tema/formulario";
                }

                imagemBytes = compressImage(imagem);
                if (imagemBytes.length > 1_000_000) {
                    model.addAttribute("erroImagem",
                            "A imagem é muito grande mesmo após compressão. Por favor, use uma imagem menor.");
                    model.addAttribute("tema", tema);
                    return "tema/formulario";
                }
            } else {
                imagemBytes = tema.getImagem();
            }
            
            var dados = new DadosAtualizacaoTema(
                id,
                descricao != null ? descricao : tema.getDescricao(),
                preco != null ? preco : tema.getPreco(),
                imagemBytes
            );
            
            tema.atualizarInformacoes(dados);
            
            redirectAttributes.addFlashAttribute("mensagem", "Tema atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao atualizar tema: " + (e.getMessage().contains("max_allowed_packet")
                            ? "A imagem é muito grande para o banco de dados. Por favor, reduza o tamanho."
                            : e.getMessage()));
            return "redirect:/tema/formulario?id=" + id;
        }
        
        return "redirect:/tema";
    }

    private byte[] compressImage(MultipartFile imagem) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedImage originalImage = ImageIO.read(imagem.getInputStream());

        if (originalImage == null) {
            return imagem.getBytes();
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        float ratio = (float) width / (float) height;
        int newWidth = 800;
        int newHeight = (int) (newWidth / ratio);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        ImageIO.write(resizedImage, "jpg", outputStream);
        return outputStream.toByteArray();
    }

    private String determineImageMimeType(byte[] imageData) {
        try {
            String mimeType = URLConnection.guessContentTypeFromStream(
                new ByteArrayInputStream(imageData));
            return mimeType != null ? mimeType : "image/jpeg";
        } catch (IOException e) {
            return "image/jpeg";
        }
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