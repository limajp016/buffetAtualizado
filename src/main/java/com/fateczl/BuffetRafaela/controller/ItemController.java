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

import com.fateczl.BuffetRafaela.entities.Item;
import com.fateczl.BuffetRafaela.records.DadosAtualizacaoItem;
import com.fateczl.BuffetRafaela.records.DadosCadastroItem;
import com.fateczl.BuffetRafaela.repositories.CategoriaRepository;
import com.fateczl.BuffetRafaela.repositories.ItemRepository;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/item")
public class ItemController {

	private final ItemRepository repository;
	private final CategoriaRepository categoriaRepository;

	public ItemController(ItemRepository repository, CategoriaRepository categoriaRepository) {
		this.repository = repository;
		this.categoriaRepository = categoriaRepository;
	}

	@GetMapping("/formulario")
	public String formulario(@RequestParam(required = false) Long id, Model model) {
		if (id != null) {
			Item item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));
			model.addAttribute("item", item);
		}
		model.addAttribute("categorias", categoriaRepository.findAll());
		return "item/formulario";
	}

	@PostMapping
	@Transactional
	public String cadastrar(@RequestParam Long categoriaId, @RequestParam String descricao,
			@RequestParam Double valorCusto, @RequestParam Double valorVenda, @RequestParam String campoDesc,
			@RequestParam(required = false) MultipartFile imagem, RedirectAttributes redirectAttributes, Model model)
			throws IOException {

		try {
			if (imagem != null && !imagem.isEmpty()) {
				long maxFileSize = 2 * 1024 * 1024;
				if (imagem.getSize() > maxFileSize) {
					model.addAttribute("erroImagem", "O tamanho da imagem não pode exceder 2MB");
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}

				String contentType = imagem.getContentType();
				if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
					model.addAttribute("erroImagem", "Apenas arquivos JPEG e PNG são permitidos");
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}

				byte[] imagemBytes = compressImage(imagem);
				if (imagemBytes.length > 1_000_000) {
					model.addAttribute("erroImagem",
							"A imagem é muito grande mesmo após compressão. Por favor, use uma imagem menor.");
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}

				var categoria = categoriaRepository.findById(categoriaId)
						.orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada"));

				var dados = new DadosCadastroItem(descricao, valorCusto, valorVenda, campoDesc, imagemBytes, categoria);
				var item = new Item(dados);

				repository.save(item);
				redirectAttributes.addFlashAttribute("mensagem", "Item cadastrado com sucesso!");
			}

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro",
					"Erro ao cadastrar item: " + (e.getMessage().contains("max_allowed_packet")
							? "A imagem é muito grande para o banco de dados. Por favor, reduza o tamanho."
							: e.getMessage()));
			return "redirect:/item/formulario";
		}

		return "redirect:/item";
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

	@PutMapping
	@Transactional
	public String atualizar(@RequestParam Long id, @RequestParam(required = false) String descricao,
			@RequestParam(required = false) Double valorCusto, @RequestParam(required = false) Double valorVenda,
			@RequestParam(required = false) String campoDesc, @RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) MultipartFile imagem,
			@RequestParam(required = false, defaultValue = "false") boolean removerImagem,
			RedirectAttributes redirectAttributes, Model model) throws IOException {

		try {
			var item = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

			byte[] imagemBytes = null;

			if (removerImagem) {
				imagemBytes = null;
			} else if (imagem != null && !imagem.isEmpty()) {
				long maxFileSize = 2 * 1024 * 1024;
				if (imagem.getSize() > maxFileSize) {
					model.addAttribute("erroImagem", "O tamanho da imagem não pode exceder 2MB");
					model.addAttribute("item", item);
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}

				String contentType = imagem.getContentType();
				if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
					model.addAttribute("erroImagem", "Apenas arquivos JPEG e PNG são permitidos");
					model.addAttribute("item", item);
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}

				imagemBytes = compressImage(imagem);
				if (imagemBytes.length > 1_000_000) {
					model.addAttribute("erroImagem",
							"A imagem é muito grande mesmo após compressão. Por favor, use uma imagem menor.");
					model.addAttribute("item", item);
					model.addAttribute("categorias", categoriaRepository.findAll());
					return "item/formulario";
				}
			} else {
				imagemBytes = item.getImagem();
			}

			var categoria = categoriaId != null ? categoriaRepository.findById(categoriaId).orElse(null)
					: item.getCategoria();

			var dados = new DadosAtualizacaoItem(descricao != null ? descricao : item.getDescricao(),
					valorCusto != null ? valorCusto : item.getValorCusto(),
					valorVenda != null ? valorVenda : item.getValorVenda(),
					campoDesc != null ? campoDesc : item.getCampoDesc(), imagemBytes, categoria);

			item.atualizarInformacoes(dados);
			redirectAttributes.addFlashAttribute("mensagem", "Item atualizado com sucesso!");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro",
					"Erro ao atualizar item: " + (e.getMessage().contains("max_allowed_packet")
							? "A imagem é muito grande para o banco de dados. Por favor, reduza o tamanho."
							: e.getMessage()));
			return "redirect:/item/formulario?id=" + id;
		}

		return "redirect:/item";
	}

	@DeleteMapping
	@Transactional
	public String excluir(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		try {
			repository.deleteById(id);
			redirectAttributes.addFlashAttribute("mensagem", "Item excluído com sucesso!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("erro", "Erro ao excluir item: " + e.getMessage());
		}
		return "redirect:/item";
	}

	@GetMapping
	public String listar(@RequestParam(defaultValue = "0") int page,
	                     @RequestParam(defaultValue = "4") int size,
	                     Model model) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("descricao").ascending());
	    Page<Item> pagina = repository.findAll(pageable);

	    pagina.forEach(item -> {
	        if (item.getImagem() != null && item.getImagem().length > 0) {
	            String mimeType = determineImageMimeType(item.getImagem());
	            item.setImagemBase64("data:" + mimeType + ";base64," +
	                    Base64.getEncoder().encodeToString(item.getImagem()));
	        }
	    });

	    model.addAttribute("pagina", pagina);
	    return "item/listagem";
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

}