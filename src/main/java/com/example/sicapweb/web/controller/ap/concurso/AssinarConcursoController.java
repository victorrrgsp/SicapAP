package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import br.gov.to.tce.util.Date;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.ConcursoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.ConcursoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.ConcursoEnvioRepository;
import com.example.sicapweb.repository.concurso.DocumentoEditalHomologacaoRepository;
import com.example.sicapweb.repository.concurso.DocumentoEditalRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.service.AssinarCertificadoDigital;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@RestController
@RequestMapping("/assinarConcurso")
public class AssinarConcursoController {

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;


    @Autowired
    private ConcursoEnvioAssinaturaRepository concursoEnvioAssinaturaRepository;

    @Autowired
    private DocumentoEditalRepository  documentoEditalRepository;

    @Autowired
    private DocumentoEditalHomologacaoRepository documentoEditalHomologacaoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ConcursoEnvioAssRetorno>> listaAEnviosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ConcursoEnvioAssRetorno> paginacaoUtil = concursoEnvioRepository.buscarEnviosAguardandoAss(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @Transactional( rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> AssinarConcurso(@RequestBody String hashassinante_hashAssinado )  throws JsonProcessingException,Exception {
        User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());
       // try {
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
                                novo.setIp(getIp.getRequest().getRemoteAddr());
                                novo.setConcursoEnvio(envio);
                                LocalDateTime dt =LocalDateTime.now();
                                novo.setData_Assinatura(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
                                novo.setHashAssinante(hashassinante);
                                novo.setHashAssinado(hashassinado);
                                concursoEnvioAssinaturaRepository.save(novo);
                                //coleta dados do cadun sobre o id  do responsavel da ug e o id da pessoa juridica
                                String Cnpj = User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getUnidadeGestora().getId();
                                Integer origem =   concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);

                                Integer vinculada = concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
                                Integer responsavel = concursoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
                                Integer responsavel_solicitante = concursoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
                                Integer solicitante = concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
                                LocalDateTime dh_protocolo= LocalDateTime.now();
                                String matricula = "000003";
                                Integer idassunto = null ;
                                Integer assuntocodigo = null ;
                                Integer classeassunto  = null;
                                String deptoAutuacao = "";
                                String tipodocumento = "";
                                Integer id_entidade_vinculada = null;
                                if (envio.getOrgaoorigem() != null) {
                                    id_entidade_vinculada = concursoEnvioAssinaturaRepository.getidCADUNPJ(envio.getOrgaoorigem());
                                }
                                switch (envio.getFase().intValue()){
                                    case 1:// fase edital
                                         idassunto = 64;
                                         assuntocodigo = 6;
                                         classeassunto = 8;
                                        deptoAutuacao = "COCAP";
                                        tipodocumento = "TA";
                                        break;
                                    case 2: // fase homologacao
                                         idassunto = 161;
                                         assuntocodigo = 1;
                                         classeassunto = 15;
                                         deptoAutuacao = "COREA";
                                        tipodocumento = "HOMOL";
                                        break;
                                }
                                Integer idprotocolo =concursoEnvioAssinaturaRepository.insertProtocolo(matricula,dh_protocolo.getYear(),dh_protocolo,origem);
                                if (idprotocolo != null )
                                {
                                    //prepara  variaveis de processo
                                    Integer procnumero = idprotocolo;
                                    Integer ProcessoNpai= null;
                                    Integer ProcessoApai= null;
                                    if (envio.getProcessoPai() != null) {
                                        String[] pc =envio.getProcessoPai().split("/");
                                        ProcessoNpai = Integer.valueOf(pc[0]) ;
                                        ProcessoApai = Integer.valueOf(pc[1]) ;
                                    }
                                    Integer ano = dh_protocolo.getYear();
                                    Integer anoreferencia = envio.getEdital().getDataPublicacao().getYear();
                                    Integer relatorio =70;
                                    String complemento=envio.getComplemento();
                                    Integer evento = 1;
                                    Integer numEdital=null;
                                    Integer anoEdital=null;
                                    numEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(0, envio.getEdital().getNumeroEdital().length() - 4));
                                    anoEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
                                    if (  numEdital==null || numEdital==0 ) throw  new InvalitInsert("numero do edital não esta no formato certo!");
                                    if (  anoEdital==null || anoEdital<1990 ) throw  new InvalitInsert("numero do edital não esta no formato certo!");
                                    concursoEnvioAssinaturaRepository.insertProcesso(procnumero,ano,anoEdital,ProcessoNpai , ProcessoApai ,relatorio, complemento ,assuntocodigo ,classeassunto , idprotocolo , origem, id_entidade_vinculada,idassunto );
                                    concursoEnvioAssinaturaRepository.insertAndamentoProcesso(procnumero,ano);
                                    concursoEnvioAssinaturaRepository.insertProcEdital(procnumero,ano,numEdital,anoEdital);
                                    concursoEnvioAssinaturaRepository.insertPessoaInteressada(procnumero,ano, responsavel , 1,4  );
                                    concursoEnvioAssinaturaRepository.insertHist(procnumero,ano,deptoAutuacao);
                                    BigDecimal idDocument =  concursoEnvioAssinaturaRepository.insertDocument(tipodocumento,procnumero,ano,evento);
                                    if ( idDocument==null ) throw  new InvalitInsert("não gerou o ID do documento no econtas!");
                                    String Arquivo=null;
                                    if (envio.getFase()==1){
                                        List<DocumentoEdital> ldocs =  documentoEditalRepository.buscarDocumentosEdital("'I','II','III','IV','V','VI','VII','VIII','IX','IX.I','X',''",envio.getEdital().getId());
                                        if(ldocs.size()>0){
                                            for(DocumentoEdital  doc: ldocs){
                                                Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(doc.getInciso(),envio.getFase());
                                                if (Arquivo != null){
                                                    concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocument,Arquivo,doc.getIdCastorFile());
                                                } else
                                                {
                                                    throw new Exception("não encontrou descrição do arquiva no inciso e fase!");
                                                }
                                            }
                                        }
                                        else{
                                            throw  new InvalitInsert("não encontrou documentos anexados!");
                                        }

                                    } else if (envio.getFase()==2) {
                                        List<DocumentoEditalHomologacao> ldocs =  documentoEditalHomologacaoRepository.buscarDocumentosEditalHomologacao("'XI','XII','XIII','XIV','XV',''",envio.getEdital().getId());
                                        if(ldocs.size()>0){
                                            for(DocumentoEditalHomologacao  doc: ldocs){
                                                Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(doc.getInciso(),envio.getFase());
                                                if (Arquivo != null){
                                                    concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocument,Arquivo,doc.getIdCastorFile());
                                                } else
                                                {
                                                    throw new Exception("não encontrou descrição do arquiva no inciso e fase!");
                                                }
                                            }
                                        }
                                        else{
                                            throw  new InvalitInsert("não encontrou documentos anexados!");
                                        }
                                    }
                                    //atualiza o campo processo no envio com o numero e ano do processo econtas
                                    envio.setProcesso(procnumero+"/"+ano);
                                    envio.setStatus(ConcursoEnvio.Status.Finalizado.getValor());
                                    concursoEnvioRepository.update(envio);
                                } else {
                                    throw new Exception("id do protocolo não foi gerado!");
                                }
                            }
                            else{
                                throw new Exception("id do envio não encontrado!");
                            }
//
                    }
                }
                //throw new SQLException("Test erro handling");
            } else {
                System.out.println("não encontrou usuario logado!!");
            }

       // }catch(Exception e){
        //    System.out.println("[falha]: " + e.toString());
        //    e.printStackTrace();
        //    return ResponseEntity.badRequest().body(e.getMessage());
        //}

        return ResponseEntity.ok().body("OK");
    }

    @CrossOrigin
    @PostMapping(path="/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash  ){
        String respostaIniciarAssinatura=new String();
        try {
            User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());

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
