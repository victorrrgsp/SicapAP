package com.example.sicapweb.web.controller.ap.concurso;

import aj.org.objectweb.asm.TypeReference;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import br.gov.to.tce.validation.ValidationException;
import com.example.sicapweb.model.HashMessenger;
import com.example.sicapweb.model.ProcessoAdmissaoConcurso;
import com.example.sicapweb.repository.concurso.ConcursoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.EditalRepository;
import com.example.sicapweb.repository.geral.UsuarioRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.service.AssinarCertificado;
import com.example.sicapweb.service.Login;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.AssinarCertificadoDigital;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.gson.JsonArray;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


@RestController
@RequestMapping("/assinarConcurso")
public class AssinarConcursoController {

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ConcursoEnvioAssinaturaRepository concursoEnvioAssinaturaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ConcursoEnvio>> listaAEnviosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ConcursoEnvio> paginacaoUtil = concursoEnvioRepository.buscarEnviosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<?> AssinarConcurso(@RequestBody String hashassinante_hashAssinado ) {
        User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());
        try {
            if (userlogado != null) {
                JsonNode requestJson = new ObjectMapper().readTree(hashassinante_hashAssinado);
                String hashassinante =  URLDecoder.decode(requestJson.get("hashassinante").asText(), StandardCharsets.UTF_8);
                String hashassinado =  URLDecoder.decode(requestJson.get("hashassinado").asText(), StandardCharsets.UTF_8);
                String processosBase64Decoded = new String(Base64.getDecoder().decode(hashassinante.getBytes()));
                ArrayNode arrayNodeproc = (ArrayNode) new ObjectMapper().readTree(processosBase64Decoded);
                Iterator<JsonNode> itrproc = arrayNodeproc.elements();
                System.out.println("hashassinante:"+hashassinante);
                System.out.println("hashassinado:"+hashassinado);



                if (arrayNodeproc.isArray()) {
                    while (itrproc.hasNext()) {
                        JsonNode aux = itrproc.next();
                        System.out.println("id: " + aux.get("id").asText());
                        System.out.println("edital.id: " + aux.get("edital").get("id").asText());

                        // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante

                            //  Concurso
                            BigInteger idenvio = (BigInteger) aux.get("id").bigIntegerValue();
                            System.out.println("idenvio: " + idenvio);
                            ConcursoEnvio envio = (ConcursoEnvio) concursoEnvioRepository.findById(idenvio);
                            if (envio!=null ){
                                ConcursoEnvioAssinatura novo = new ConcursoEnvioAssinatura();
                                novo.setIdCargo(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
                                novo.setCpf(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCpf());
                                ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                                getIp.getRequest().getRemoteAddr();
                                //InetAddress.getLocalHost().getHostAddress()
                                novo.setIp(getIp.getRequest().getRemoteAddr());
                                novo.setConcursoEnvio(envio);
                                novo.setData_Assinatura(new Date());
                                novo.setHashAssinante(hashassinante);
                                novo.setHashAssinado(hashassinado);
//                                concursoEnvioAssinaturaRepository.save(novo);
                            }
                            else{
                                throw new Exception("id do envio não encontrado!");
                            }
//
                    }
                }
            } else {
                System.out.println(" nao encontrou usuario logado!!");
            }
        }catch(Exception e){
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(hashassinante_hashAssinado);
    }

    @CrossOrigin
    @PostMapping(path="/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash  ){
        String respostaIniciarAssinatura=new String();
        try {
            User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());

            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagem_hash);

            JsonNode certificadoJson = new ObjectMapper().readTree(userlogado.getCertificado());

            System.out.println("original:"+respostaJson.get("original"));
            System.out.println(certificado_mensagem_hash.toString());
            String certificado = respostaJson.get("certificado").asText();

            System.out.println("certificado:"+certificado);
            String Original =  respostaJson.get("original").asText();

            String hash = URLDecoder.decode(respostaJson.get("hashcertificado").asText(), StandardCharsets.UTF_8)  ;



           String assinatura = certificadoJson.get("validacaoAssinatura").get("dados").get("assinatura").asText();

            System.out.println("certificado logado:"+certificadoJson.asText());

            System.out.println("Assinatura logada:"+assinatura);




            if (userlogado == null ){
                System.out.println(" nao encontrou usuario logado!!");
            }
            else if(!userlogado.getHashCertificado().equals(hash) ){
                throw new Exception("nao é o mesmo  certificado que esta logado!!");
            }
            else {
                respostaIniciarAssinatura = AssinarCertificadoDigital.inicializarAssinatura(certificado,Original);
                System.out.println("respostainicializarAssinatura:"+respostaIniciarAssinatura);
            }

            //decodigica a mensagem



            System.out.println("mensagem: "+Original);
            byte[] decodedBytes2 = Base64.getDecoder().decode(Original);
            String decodedoriginal = new String(decodedBytes2);


            //gera lista de processo enviados selecionados pelo usuario para serem manipulados
            ArrayNode arrayNode =  (ArrayNode) new ObjectMapper().readTree(decodedoriginal);
            Iterator<JsonNode> itr =  arrayNode.elements();
            //ArrayNode alvoAssinatura = new ObjectMapper().createArrayNode();



            System.out.println("mensagem decodificada: "+decodedoriginal);




        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
           return  ResponseEntity.ok().body(e.getMessage());
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
            System.out.println("desafioback:"+desafio);
            System.out.println("assinatura:"+assinatura);
            System.out.println("original:"+mensagem);
            respostaFinalizarAssinatura = AssinarCertificadoDigital.FinalizarAssinatura(desafio,assinatura,mensagem);

        } catch(Exception e ){
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }
        System.out.println("respostafinalizarassinatura:"+respostaFinalizarAssinatura);
        return ResponseEntity.ok().body(respostaFinalizarAssinatura);
    }

}
