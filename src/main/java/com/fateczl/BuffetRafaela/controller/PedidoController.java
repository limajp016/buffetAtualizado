package com.fateczl.BuffetRafaela.controller;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fateczl.BuffetRafaela.entities.ItemPedido;
import com.fateczl.BuffetRafaela.entities.Pedido;
import com.fateczl.BuffetRafaela.services.PedidoService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarPedidos(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "9") int size,
                                Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Pedido> pagina = pedidoService.listarPedidos(pageable);
            model.addAttribute("pagina", pagina);
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao listar pedidos: " + e.getMessage());
        }
        return "pedido/listagem";
    }


    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> gerarPDF(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(id);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            document.setFont(font);

            document.add(new Paragraph("Comprovante de Pedido")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Dados do Pedido").setBold().setFontSize(14));
            document.add(new LineSeparator(new SolidLine()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String dataFormatada = pedido.getDtHoraInicio().format(formatter);

            document.add(new Paragraph("ID: " + pedido.getId()));
            document.add(new Paragraph("Cliente: " + pedido.getNomeCliente()));
            document.add(new Paragraph("Data/Hora Início: " + dataFormatada));
            document.add(new Paragraph("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal())));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Tema").setBold().setFontSize(14));
            document.add(new LineSeparator(new SolidLine()));

            document.add(new Paragraph("Descrição: " + pedido.getDescricaoTema()));
            document.add(new Paragraph("Preço: R$ " + String.format("%.2f", pedido.getPrecoTema())));

            if (pedido.getImagemTema() != null && pedido.getImagemTema().length > 0) {
                ImageData imageData = ImageDataFactory.create(pedido.getImagemTema());
                Image image = new Image(imageData);
                image.setMaxWidth(60);
                image.setAutoScale(true);
                document.add(new Paragraph("Imagem do Tema:"));
                document.add(image);
            }

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Endereço").setBold().setFontSize(14));
            document.add(new LineSeparator(new SolidLine()));

            document.add(new Paragraph("Logradouro: " + pedido.getLogradouro()));
            document.add(new Paragraph("Número: " + pedido.getNumero()));
            document.add(new Paragraph("Bairro: " + pedido.getBairro()));
            document.add(new Paragraph("Cidade: " + pedido.getCidade()));
            document.add(new Paragraph("UF: " + pedido.getUf()));
            document.add(new Paragraph("CEP: " + pedido.getCep()));
            document.add(new Paragraph("Complemento: " + 
                (pedido.getComplemento() != null ? pedido.getComplemento() : "N/A")));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Itens do Pedido").setBold().setFontSize(14));
            document.add(new LineSeparator(new SolidLine()));

            float[] columnWidths = {60F, 150F, 50F, 60F, 60F, 60F};
            Table table = new Table(columnWidths);

            table.addHeaderCell("Imagem");
            table.addHeaderCell("Descrição");
            table.addHeaderCell("Qtd");
            table.addHeaderCell("Preço Unit.");
            table.addHeaderCell("Valor Venda");
            table.addHeaderCell("Subtotal");

            for (ItemPedido item : pedido.getItens()) {
                if (item.getImagemItem() != null && item.getImagemItem().length > 0) {
                    ImageData imageData = ImageDataFactory.create(item.getImagemItem());
                    Image image = new Image(imageData);
                    image.setMaxWidth(50);
                    image.setAutoScale(true);
                    Cell imageCell = new Cell().add(image);
                    table.addCell(imageCell);
                } else {
                    table.addCell(new Cell().add(new Paragraph("N/A")));
                }

                double subtotal = item.getQuantidade() * item.getPrecoUnitario();
                table.addCell(new Paragraph(item.getDescricaoItem()));
                table.addCell(new Paragraph(String.valueOf(item.getQuantidade())));
                table.addCell(new Paragraph("R$ " + String.format("%.2f", item.getPrecoUnitario())));
                table.addCell(new Paragraph("R$ " + String.format("%.2f", item.getValorVenda())));
                table.addCell(new Paragraph("R$ " + String.format("%.2f", subtotal)));
            }

            document.add(table);

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=pedido_" + pedido.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Erro ao gerar PDF: " + e.getMessage()).getBytes());
        }
    }



}
