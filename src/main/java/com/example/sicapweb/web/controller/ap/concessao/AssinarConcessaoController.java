package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.adm.AdmEnvioAssinatura;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.repository.concessao.AdmEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.service.AssinarCertificadoDigital;
import com.example.sicapweb.web.controller.DefaultController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;

@CrossOrigin
@RestController
@RequestMapping("/assinarConcessao")
public class AssinarConcessaoController extends DefaultController<AdmEnvio> {

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    @Autowired
    private AdmEnvioAssinaturaRepository admEnvioAssinaturaRepository;


    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmEnvio>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AdmEnvio> paginacaoUtil = admEnvioRepository.buscaPaginada(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @PostMapping(path = "/assinar")
    public ResponseEntity<?> AssinarConcessorio(@RequestBody String hashassinante_hashAssinado) {
        try {
            JsonNode requestJson = new ObjectMapper().readTree(hashassinante_hashAssinado);
            String hashassinante = URLDecoder.decode(requestJson.get("hashassinante").asText(), StandardCharsets.UTF_8);
            String hashassinado = URLDecoder.decode(requestJson.get("hashassinado").asText(), StandardCharsets.UTF_8);
            String processosBase64Decoded = new String(Base64.getDecoder().decode(hashassinante.getBytes()));
            ArrayNode arrayNodeproc = (ArrayNode) new ObjectMapper().readTree(processosBase64Decoded);
            Iterator<JsonNode> itrproc = arrayNodeproc.elements();

            if (arrayNodeproc.isArray()) {
                while (itrproc.hasNext()) {
                    JsonNode aux = itrproc.next();
                    // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante
                    AdmEnvio envio = admEnvioRepository.findById(aux.get("id").bigIntegerValue());
                    if (envio != null) {
                        AdmEnvioAssinatura admEnvioAssinatura = new AdmEnvioAssinatura();
                        admEnvioAssinatura.setIdCargo(User.getUser(admEnvioRepository.getRequest()).getCargo().getValor());
                        admEnvioAssinatura.setCpf(User.getUser(admEnvioRepository.getRequest()).getCpf());
                        admEnvioAssinatura.setIpAssinante(InetAddress.getLocalHost().getHostAddress());
                        admEnvioAssinatura.setAdmEnvio(envio);
                        admEnvioAssinatura.setData_assinatura(new Date());
                        admEnvioAssinatura.setHash_assinante(hashassinante);
                        admEnvioAssinatura.setHash_assinado(hashassinado);
                        admEnvioAssinaturaRepository.save(admEnvioAssinatura);
                        envio.setStatus(4);
                        admEnvioRepository.update(envio);
                    } else {
                        throw new Exception("id do envio não encontrado!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(hashassinante_hashAssinado);
    }

    @CrossOrigin
    @PostMapping(path = "/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash) {
        String respostaIniciarAssinatura = new String();
        try {
            User userlogado = User.getUser(admEnvioRepository.getRequest());
            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagem_hash);
            JsonNode certificadoJson = new ObjectMapper().readTree(userlogado.getCertificado());
            String certificado = respostaJson.get("certificado").asText();
            String Original = respostaJson.get("original").asText();
            String hash = certificadoJson.get("validacaoAssinatura").get("dados").get("cpf").asText();

            if (userlogado == null) {
                System.out.println(" nao encontrou usuario logado!!");
            } else if (!userlogado.getCpf().equals(hash)) {
                throw new Exception("nao é o mesmo  certificado que esta logado!!");
            } else {
                respostaIniciarAssinatura = AssinarCertificadoDigital.inicializarAssinatura(certificado, Original);
            }
            //decodigica a mensagem
            byte[] decodedBytes2 = Base64.getDecoder().decode(Original);
            String decodedoriginal = new String(decodedBytes2);
            //gera lista de processo enviados selecionados pelo usuario para serem manipulados
            ArrayNode arrayNode = (ArrayNode) new ObjectMapper().readTree(decodedoriginal);
            Iterator<JsonNode> itr = arrayNode.elements();
            //ArrayNode alvoAssinatura = new ObjectMapper().createArrayNode();
        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
            return ResponseEntity.ok().body(e.getMessage());
        }
        return ResponseEntity.ok().body(respostaIniciarAssinatura);
    }

    @CrossOrigin
    @PostMapping(path = "/finalizarAssinatura")
    public ResponseEntity<?> finalizarAssinatura(@RequestBody String desafio_assinatura_mensagem) {
        String respostaFinalizarAssinatura = new String();
        try {
            JsonNode respostaJson = new ObjectMapper().readTree(desafio_assinatura_mensagem);
            String desafio = respostaJson.get("desafio").asText();
            String assinatura = respostaJson.get("assinatura").asText();
            String mensagem = respostaJson.get("original").asText();
            respostaFinalizarAssinatura = AssinarCertificadoDigital.FinalizarAssinatura(desafio, assinatura, mensagem);
        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(respostaFinalizarAssinatura);
    }

}
