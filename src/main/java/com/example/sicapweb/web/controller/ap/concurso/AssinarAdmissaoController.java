package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.ProcessoAdmissao;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.AdmissaoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.ProcessoAdmissaoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.AssinarCertificadoDigital;
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
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

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


    @CrossOrigin
    @Transactional( rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> AssinarAdmissao(@RequestBody String hashassinante_hashAssinado )  throws JsonProcessingException,Exception {
        User userlogado = User.getUser(processoAdmissaoRepository.getRequest());
        // try {
//        if (userlogado != null) {
//            JsonNode requestJson = new ObjectMapper().readTree(hashassinante_hashAssinado);
//            String hashassinante =  URLDecoder.decode(requestJson.get("hashassinante").asText(), StandardCharsets.UTF_8);
//            String hashassinado =  URLDecoder.decode(requestJson.get("hashassinado").asText(), StandardCharsets.UTF_8);
//            String processosBase64Decoded = new String(Base64.getDecoder().decode(hashassinante.getBytes()));
//            ArrayNode arrayNodeproc = (ArrayNode) new ObjectMapper().readTree(processosBase64Decoded);
//            Iterator<JsonNode> itrproc = arrayNodeproc.elements();
//            System.out.println("hashassinante:"+hashassinante);
//            System.out.println("hashassinado:"+hashassinado);
//
//
//
//            if (arrayNodeproc.isArray()) {
//                while (itrproc.hasNext()) {
//                    JsonNode aux = itrproc.next();
//                    System.out.println("id: " + aux.get("id").asText());
//                    System.out.println("edital.id: " + aux.get("edital").get("id").asText());
//
//                    // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante
//
//                    //  Concurso
//                    BigInteger idenvio = (BigInteger) aux.get("id").bigIntegerValue();
//                    System.out.println("idenvio: " + idenvio);
//                    ProcessoAdmissao envio = (ProcessoAdmissao) processoAdmissaoRepository.findById(idenvio);
//                    if (envio!=null ){
//                        AdmissaoEnvioAssinatura  novo = new AdmissaoEnvioAssinatura();
//                        novo.setIdCargo(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
//                        novo.setCpf(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCpf());
//                        ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//                        novo.setIp(getIp.getRequest().getRemoteAddr());
//                        novo.set(envio);
//                        novo.setData_Assinatura(new Date());
//                        novo.setHashAssinante(hashassinante);
//                        novo.setHashAssinado(hashassinado);
//                        concursoEnvioAssinaturaRepository.save(novo);
//                        //coleta dados do cadun sobre o id  do responsavel da ug e o id da pessoa juridica
//                        String Cnpj = User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getUnidadeGestora().getId();
//                        Integer origem =   concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
//
//                        Integer vinculada = concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
//                        Integer responsavel = concursoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
//                        Integer responsavel_solicitante = concursoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
//                        Integer solicitante = concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
//                        LocalDateTime dh_protocolo= LocalDateTime.now();
//                        String matricula = "000003";
//                        Integer idassunto = null ;
//                        Integer assuntocodigo = null ;
//                        Integer classeassunto  = null;
//                        String deptoAutuacao = "";
//                        String tipodocumento = "";
//                        Integer id_entidade_vinculada = null;
//                        if (envio.getOrgaoorigem() != null) {
//                            id_entidade_vinculada = concursoEnvioAssinaturaRepository.getidCADUNPJ(envio.getOrgaoorigem());
//                        }
//                        switch (envio.getFase()){
//                            case 1:// fase edital
//                                idassunto = 64;
//                                assuntocodigo = 6;
//                                classeassunto = 8;
//                                deptoAutuacao = "COCAP";
//                                tipodocumento = "TA";
//                                break;
//
//                            case 2: // fase homologacao
//                                idassunto = 64;
//                                assuntocodigo = 6;
//                                classeassunto = 8;
//                                deptoAutuacao = "COREA";
//                                tipodocumento = "HOMOL";
//                                break;
//                        }
//                        Integer idprotocolo =concursoEnvioAssinaturaRepository.insertProtocolo(matricula,dh_protocolo.getYear(),dh_protocolo,origem);
//                        if (idprotocolo != null )
//                        {
//                            //prepara  variaveis de processo
//                            Integer procnumero = idprotocolo;
//                            Integer ProcessoNpai= null;
//                            Integer ProcessoApai= null;
//                            if (envio.getProcessoPai() != null) {
//                                String[] pc =envio.getProcessoPai().split("/");
//                                ProcessoNpai = Integer.valueOf(pc[0]) ;
//                                ProcessoApai = Integer.valueOf(pc[1]) ;
//                            }
//                            Integer ano = dh_protocolo.getYear();
//                            Integer anoreferencia = envio.getEdital().getDataPublicacao().getYear();
//                            Integer relatorio =70;
//                            String complemento=envio.getComplemento();
//                            Integer evento = 1;
//                            Integer numEdital=null;
//                            Integer anoEdital=null;
//                            numEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(0, envio.getEdital().getNumeroEdital().length() - 4));
//                            anoEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
//                            concursoEnvioAssinaturaRepository.insertProcesso(procnumero,ano,anoEdital,ProcessoNpai , ProcessoApai ,relatorio, complemento ,assuntocodigo ,classeassunto , idprotocolo , origem, id_entidade_vinculada,idassunto );
//                            concursoEnvioAssinaturaRepository.insertAndamentoProcesso(procnumero,ano);
//                            concursoEnvioAssinaturaRepository.insertProcEdital(procnumero,ano,numEdital,anoEdital);
//                            concursoEnvioAssinaturaRepository.insertPessoaInteressada(procnumero,ano, responsavel , 1,4  );
//                            concursoEnvioAssinaturaRepository.insertHist(procnumero,ano,deptoAutuacao);
//                            BigDecimal idDocument =  concursoEnvioAssinaturaRepository.insertDocument(tipodocumento,procnumero,ano,evento);
//                            String Arquivo=null;
//                            if (envio.getFase()==1){
//                                List<DocumentoEdital> ldocs =  documentoEditalRepository.buscarDocumentosEdital("'I','II','III','IV','V','VI','VII','VIII','IX','IX.I','X'",envio.getEdital().getId());
//                                if(ldocs.size()>0){
//                                    for(DocumentoEdital  doc: ldocs){
//                                        Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(doc.getInciso(),envio.getFase());
//                                        if (Arquivo != null){
//                                            concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocument,Arquivo,doc.getIdCastorFile());
//                                        } else
//                                        {
//                                            throw new Exception("erro:não encontrou descrição do arquiva no inciso e fase!");
//                                        }
//                                    }
//                                }
//
//                            } else if (envio.getFase()==2) {
//                                List<DocumentoEditalHomologacao> ldocs =  documentoEditalHomologacaoRepository.buscarDocumentosEditalHomologacao("'XII','XIII','XIV','XV'",envio.getEdital().getId());
//                                if(ldocs.size()>0){
//                                    for(DocumentoEditalHomologacao  doc: ldocs){
//                                        Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(doc.getInciso(),envio.getFase());
//                                        if (Arquivo != null){
//                                            concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocument,Arquivo,doc.getIdCastorFile());
//                                        } else
//                                        {
//                                            throw new Exception("erro:não encontrou descrição do arquiva no inciso e fase!");
//                                        }
//                                    }
//                                }
//                            }
//                            //atualiza o campo processo no envio com o numero e ano do processo econtas
//                            envio.setProcesso(procnumero+"/"+ano);
//                            envio.setStatus(ConcursoEnvio.Status.Finalizado.getValor());
//                            concursoEnvioRepository.update(envio);
//                        } else {
//                            throw new Exception("erro:id do protocolo não foi gerado!");
//                        }
//                    }
//                    else{
//                        throw new Exception("erro:id do envio não encontrado!");
//                    }
////
//                }
//            }
//            //throw new SQLException("Test erro handling");
//        } else {
//            System.out.println("erro:não encontrou usuario logado!!");
//        }

        // }catch(Exception e){
        //    System.out.println("[falha]: " + e.toString());
        //    e.printStackTrace();
        //    return ResponseEntity.badRequest().body(e.getMessage());
        //}

        return ResponseEntity.ok().body("OK");
    }




}
