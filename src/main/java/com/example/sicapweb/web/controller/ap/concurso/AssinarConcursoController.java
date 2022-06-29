package com.example.sicapweb.web.controller.ap.concurso;

import aj.org.objectweb.asm.TypeReference;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
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
    @PostMapping
    public ResponseEntity<?> AddAssinaturas(@RequestBody String certificado_mensagemn  ){

        try {
//            ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//            OkHttpClient client = new OkHttpClient().newBuilder().build();
            User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());
            System.out.println("CertificadoLogado:"+userlogado.getCertificado());

            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagemn);

            JsonNode certificadoJson = new ObjectMapper().readTree(userlogado.getCertificado());

            System.out.println("original:"+respostaJson.get("original"));
            System.out.println(certificado_mensagemn.toString());
            String certificado = respostaJson.get("certificado").asText();

            System.out.println("certificado:"+certificado);
            String Original =  respostaJson.get("original").asText();



           String assinatura = certificadoJson.get("validacaoAssinatura").get("dados").get("assinatura").asText();

            System.out.println("Assinatura:"+assinatura);

//            String desafio = Login.getDesafio(certificado);

//            System.out.println("desafio:"+desafio);




            if (userlogado != null ){
                String resposta = AssinarCertificadoDigital.inicializarAssinatura(certificado,Original);
//                JsonNode respostaJson1 = new ObjectMapper().readTree(resposta);
                System.out.println("respostainicializarAssinatura:"+resposta);
            }
            else {
                System.out.println(" nao encontrou usuario logado!!");
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



            if (arrayNode.isArray()) {
                while (itr.hasNext()) {
                    JsonNode aux = itr.next();
                    System.out.println("id: " + aux.get("id").asText());
                    System.out.println("edital.id: " + aux.get("edital").get("id").asText());

                    // para cada envio adiciona uma assinatura
                    try {
                      //  Concurso
                        ConcursoEnvioAssinatura novo = new ConcursoEnvioAssinatura();
                        novo.setIdCargo(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
                        novo.setCpf(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCpf());
                        BigInteger idenvio = (BigInteger) aux.get("id").bigIntegerValue();
                        System.out.println("idenvio: " + idenvio);
                        ConcursoEnvio envio = (ConcursoEnvio) concursoEnvioRepository.findById(idenvio);
                        if (envio!=null ){
                            novo.setConcursoEnvio(envio);
                            novo.setData_Assinatura(new Date());
                            novo.setHashAssinante(Original);
                            novo.setHashAssinado(Original);
                        }
//                        concursoEnvioAssinaturaRepository.save(novo);
                    } catch (Exception ex) {
                        System.out.println("[falha]: " + ex.toString());
                        ex.printStackTrace();
                    }
                    //JsonNode = new ObjectMapper().readTree().
                }
            }



            //System.out.println("autenticar(): " + resposta);


//            List<Object> lista = usuarioRepository.getUser(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").asText(),
//                    user.getSistema());
//            if (lista == null || lista.isEmpty())
//                throw new ValidationException("Usuário sem permissão ou certificado inválido");
//
//            User userLogado = new User();
//
//            lista.forEach(res -> {
//                userLogado.setId(java.util.UUID.randomUUID().toString());
//                userLogado.setCpf(respostaJson.get("validacaoAssinatura").get("dados").get("cpf").toString().replace("\"", ""));
//                userLogado.setUserName(userLogado.getCpf());
//                userLogado.setNome(respostaJson.get("validacaoAssinatura").get("dados").get("nome").toString());
//                userLogado.setCertificado(resposta);
//                userLogado.getDateEnd().addHours(2);
//                userLogado.setUnidadeGestora(new UnidadeGestora(((Object[]) res)[1].toString(), ((Object[]) res)[2].toString(),
//                        Integer.parseInt(((Object[]) res)[3].toString())));
//                userLogado.setUnidadeGestoraList(userLogado.getUnidadeGestora());
//
//                userLogado.setCargoByInteger(Integer.parseInt(((Object[]) res)[4].toString()));
//            });
//            //System.out.println(cpf);
//
//            Session.setUsuario(userLogado);
//            getIp.getRequest().getSession().setAttribute(userLogado.getCpf(), userLogado);
//            config.jedis.set(userLogado.getId(), Config.json(userLogado));
//
//            return ResponseEntity.ok().body(userLogado.getId());
        } catch (Exception e) {
            System.out.println("[falha]: " + e.toString());
            e.printStackTrace();
        }


//        for(Integer i= 0; i < listaconcursoenvios.size(); i++){
//            ConcursoEnvio envio = (ConcursoEnvio) listaconcursoenvios.get(i);
//            ConcursoEnvioAssinatura assinatura =  new ConcursoEnvioAssinatura();
//            assinatura.setData_Assinatura(new Date());
//            System.out.println("dataasinatura:"+assinatura.getData_Assinatura());
//            assinatura.setConcursoEnvio(envio);
//            System.out.println("numeroEditalassinatura:"+assinatura.getConcursoEnvio().getEdital().getNumeroEdital()) ;
//            System.out.println("dataEditalassinatura:"+assinatura.getConcursoEnvio().getEdital().getDataPublicacao()) ;
//            System.out.println("cnpjorganizadorassinatura:"+assinatura.getConcursoEnvio().getEdital().getCnpjEmpresaOrganizadora()) ;
//            assinatura.setIdCargo(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
//            System.out.println("cargoasinatura:"+assinatura.getIdCargo());
//            assinatura.setCpf(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCpf());
//            System.out.println("cpfasinatura:"+assinatura.getCpf());
////            HashMap<String, String> camposEnvioHasheaveis = new HashMap<>();
////            camposEnvioHasheaveis.put("id",envio.getId().toString());
////            camposEnvioHasheaveis.put("fase",envio.getFase().toString());
////            camposEnvioHasheaveis.put("numero_edital",envio.getEdital().getNumeroEdital());
////            camposEnvioHasheaveis.put("organizacao",envio.getEdital().getCnpjEmpresaOrganizadora());
////            camposEnvioHasheaveis.put("meuCargo",User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor().toString());
////            HashMessenger messenger = new HashMessenger(camposEnvioHasheaveis.toString());
////            assinatura.setHashAssinado(messenger.getTexthashed());
////            concursoEnvioAssinaturaRepository.save(assinatura);
//        }
        return ResponseEntity.ok().body(certificado_mensagemn);
    }

}
