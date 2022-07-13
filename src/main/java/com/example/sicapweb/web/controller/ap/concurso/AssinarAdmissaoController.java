package com.example.sicapweb.web.controller.ap.concurso;

import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.AdmissaoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.ProcessoAdmissaoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.AssinarCertificadoDigital;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/assinarAdmissao")
public class AssinarAdmissaoController {
    @Autowired
    private ProcessoAdmissaoRepository processoAdmissaoRepository;

    @Autowired
    private AdmissaoEnvioAssinaturaRepository admissaoEnvioAssinaturaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmissaoEnvioAssRetorno>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtil = processoAdmissaoRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @PostMapping(path="/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash  ){
        String respostaIniciarAssinatura=new String();
        try {
            User userlogado = User.getUser(admissaoEnvioAssinaturaRepository.getRequest());

            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagem_hash);

            JsonNode certificadoJson = new ObjectMapper().readTree(userlogado.getCertificado());

            String certificado = respostaJson.get("certificado").asText();

            String Original =  respostaJson.get("original").asText();

            String hash = URLDecoder.decode(respostaJson.get("hashcertificado").asText(), StandardCharsets.UTF_8)  ;

            String assinatura = certificadoJson.get("validacaoAssinatura").get("dados").get("assinatura").asText();

            if (userlogado == null ){
                throw new Exception("nao encontrou usuario logado!!");
            }
            else if(!userlogado.getHashCertificado().equals(hash) ){
                throw new Exception("nao é o mesmo  certificado que esta logado!!");
            }
            else {
                respostaIniciarAssinatura = AssinarCertificadoDigital.inicializarAssinatura(certificado,Original);
            }




        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(respostaIniciarAssinatura);
    }

    @CrossOrigin
    @PostMapping(path="/finalizarAssinatura")
    public ResponseEntity<?> finalizarAssinatura(@RequestBody String desafio_assinatura_mensagem  ){
        String respostaFinalizarAssinatura=new String();

        try {
            JsonNode respostaJson = new ObjectMapper().readTree(desafio_assinatura_mensagem);
            String desafio=respostaJson.get("desafio").asText();
            String assinatura=respostaJson.get("assinatura").asText();
            String mensagem=respostaJson.get("original").asText();
            respostaFinalizarAssinatura = AssinarCertificadoDigital.FinalizarAssinatura(desafio,assinatura,mensagem);

        } catch(Exception e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(respostaFinalizarAssinatura);
    }

}
