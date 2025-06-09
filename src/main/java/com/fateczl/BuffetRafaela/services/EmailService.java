package com.fateczl.BuffetRafaela.services;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fateczl.BuffetRafaela.entities.Cliente;
import com.fateczl.BuffetRafaela.entities.Orcamento;
import com.fateczl.BuffetRafaela.entities.Pedido;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Value("${spring.mail.username}")
	private String mailUsername;

	@Value("${app.host.url}")
	private String hostUrl;

	private InternetAddress criarRemetenteFormatado() throws UnsupportedEncodingException {
		String nomeRemetente = MimeUtility.encodeText("Buffet Managment - Sistema de Gerenciamento de Buffets", "UTF-8", "B");
		return new InternetAddress(mailUsername, nomeRemetente);
	}

	public void enviarEmailAprovacao(Orcamento orcamento) {
		try {
			Cliente cliente = orcamento.getCliente();
			Context context = new Context();

			context.setVariable("cliente", cliente);
			context.setVariable("orcamento", orcamento);
			context.setVariable("linkAprovacao", hostUrl + "/orcamentos/aprovar/" + orcamento.getId());

			String corpoEmail = templateEngine.process("email-aprovacao", context);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

			String destinatario = cliente.getEmail().trim().replaceAll("[\\r\\n]", "");

			helper.setTo(new InternetAddress(destinatario));
			helper.setFrom(criarRemetenteFormatado());
			helper.setSubject(MimeUtility.encodeText("Aprovação do seu Orçamento", "UTF-8", "B"));
			helper.setText(corpoEmail, true);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Erro ao enviar e-mail de aprovação", e);
		}
	}

	public void enviarEmailPedidoCriado(Orcamento orcamento, Pedido pedido) {
		try {
			Context context = new Context();
			context.setVariable("orcamento", orcamento);
			context.setVariable("urlPdf", hostUrl + "/pedido/pdf/" + pedido.getId());

			String corpo = templateEngine.process("email-pedido-criado.html", context);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(orcamento.getCliente().getEmail());
			helper.setFrom(criarRemetenteFormatado());
			helper.setSubject("Pedido criado - Orçamento #" + orcamento.getId());
			helper.setText(corpo, true);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Erro ao enviar e-mail de pedido criado", e);
		}
	}

	public void enviarEmailTemplate(String destinatario, String assunto, String template, Map<String, Object> props) {
		try {
			Context context = new Context();
			context.setVariables(props);

			String corpo = templateEngine.process(template, context);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(destinatario);
			helper.setFrom(criarRemetenteFormatado());
			helper.setSubject(assunto);
			helper.setText(corpo, true);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			logger.error("Erro ao enviar e-mail com template: " + template, e);
			throw new RuntimeException("Erro ao enviar e-mail", e);
		}
	}
}
