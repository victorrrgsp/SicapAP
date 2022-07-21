package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.*;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.AdmissaoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
import com.example.sicapweb.repository.concurso.ProcessoAdmissaoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.service.AssinarCertificadoDigital;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private DocumentoAdmissaoRepository documentoAdmissaoRepository;


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
                    ProcessoAdmissao envio = (ProcessoAdmissao) processoAdmissaoRepository.findById(idenvio);
                    if (envio!=null ){
                        AdmissaoEnvioAssinatura  novo = new AdmissaoEnvioAssinatura();
                        novo.setIdCargo(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
                        novo.setCpf(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCpf());
                        ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                        novo.setIp(getIp.getRequest().getRemoteAddr());
                        novo.setProcessoAdmissao(envio);
                        LocalDateTime dt =LocalDateTime.now();
                        novo.setData_Assinatura(Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()));
                        novo.setHashAssinante(hashassinante);
                        novo.setHashAssinado(hashassinado);
                        admissaoEnvioAssinaturaRepository.save(novo);
                        //coleta dados do cadun sobre o id  do responsavel da ug e o id da pessoa juridica
                        String Cnpj = User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getUnidadeGestora().getId();
                        Integer origem =   admissaoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);

                        Integer vinculada = admissaoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
                        Integer responsavel = admissaoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
                        Integer responsavel_solicitante = admissaoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
                        Integer solicitante = admissaoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
                        LocalDateTime dh_protocolo= LocalDateTime.now();
                        String matricula = "000003";
                        Integer idassunto = null ;
                        Integer assuntocodigo = null ;
                        Integer classeassunto  = null;
                        String deptoAutuacao = "";
                        String tipodocumento = "";
                        Integer id_entidade_vinculada = null;
                       // if (envio.getOrgaoorigem() != null) {
                            id_entidade_vinculada = null;
                                    //concursoEnvioAssinaturaRepository.getidCADUNPJ(envio.getOrgaoorigem());
                        //}

                        idassunto = 8;
                        assuntocodigo = 1;
                        classeassunto = 8;
                        deptoAutuacao = "COCAP";
                        tipodocumento = "TA";
                        Integer idprotocolo =admissaoEnvioAssinaturaRepository.insertProtocolo(matricula,dh_protocolo.getYear(),dh_protocolo,origem);
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
                            Integer anoreferencia = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
                            Integer relatorio =70;
                            String complemento=null;
                            Integer evento = 1;
                            Integer numEdital=null;
                            Integer anoEdital=null;
                            numEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(0, envio.getEdital().getNumeroEdital().length() - 4));
                            anoEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
                            admissaoEnvioAssinaturaRepository.insertProcesso(procnumero,ano,anoEdital,ProcessoNpai , ProcessoApai ,relatorio, complemento ,assuntocodigo ,classeassunto , idprotocolo , origem, id_entidade_vinculada,idassunto );
                            admissaoEnvioAssinaturaRepository.insertAndamentoProcesso(procnumero,ano);
                            admissaoEnvioAssinaturaRepository.insertPessoaInteressada(procnumero,ano, responsavel , 1,4  );
                            admissaoEnvioAssinaturaRepository.insertHist(procnumero,ano,deptoAutuacao);
                            // começa a inserir os documentos dos aprovados emposados
                            List<DocumentoAdmissao> lcomadm  = documentoAdmissaoRepository.getAprovadosComAdmissao(envio.getId());
                            Integer contador = 1;
                            Integer ArquivosPorDocs =100;
                            Integer ContadorEvento =1;
                            BigDecimal id_Document = null;
                            for (DocumentoAdmissao doc :  lcomadm ){
                                EditalAprovado aprov = doc.getEditalAprovado();
                                String cpfAprovado = aprov.getCpf();
                                String nomeAprovado = aprov.getNome();
                                //sera necessario gerar a chave atravez do metodo id_Document 'exec cadun.dbo.obterCodigoNovaPessoa'
                                Integer codigopessoa = admissaoEnvioAssinaturaRepository.insertCadunPessoaInterressada(cpfAprovado,nomeAprovado, novo.getIp(),userlogado.getUserName() );
                                if (codigopessoa ==null) throw new InvalitInsert("Não gerou codigo de pessoa fisica do interessado "+nomeAprovado+" no cadun!");
                                if ( contador ==1){

                                    id_Document = admissaoEnvioAssinaturaRepository.insertDocument(tipodocumento,procnumero,ano, ContadorEvento );
                                    if (id_Document==null ) throw new InvalitInsert("Nao gerou documento tipo:"+tipodocumento+" processo:"+procnumero+'/'+ano);
                                }
                                admissaoEnvioAssinaturaRepository.insertPessoaInteressada(procnumero,ano, codigopessoa , 2,0  );
                                admissaoEnvioAssinaturaRepository.insertArquivoDocument(  id_Document , nomeAprovado, doc.getDocumentoCastorId() ) ;

                                if (contador ==  ArquivosPorDocs){
                                    contador= 0;
                                    ContadorEvento++;
                                }
                                contador++;
                            }


                            // começa a inserir os documentos dos aprovados nao emposados
                             lcomadm  = documentoAdmissaoRepository.getAprovadosSemAdmissao(envio.getId());
                             contador = 1;
                             ArquivosPorDocs =100;
                             if (!ContadorEvento.equals(1)){
                                 ContadorEvento =ContadorEvento+1;
                             }
                            for (DocumentoAdmissao doc :  lcomadm ){
                                EditalAprovado aprov = doc.getEditalAprovado();
                                String Descricao="";
                                switch (doc.getOpcaoDesistencia().intValue() ){
                                    case  1:
                                        Descricao="Desistência";
                                        break;
                                    case 2:
                                        Descricao="Não comparecimento";
                                        break;
                                    case 3:
                                        Descricao="Pediu prorrogação";
                                        break;
                                    case 4:
                                        Descricao="Documentação insatisfatória";
                                        break;
                                }


                                String cpfAprovado = aprov.getCpf();
                                String nomeAprovado = aprov.getNome();
                                //sera necessario gerar a chave atravez do metodo id_Document 'exec cadun.dbo.obterCodigoNovaPessoa'
                                //Integer codigopessoa = admissaoEnvioAssinaturaRepository.insertCadunPessoaInterressada(cpfAprovado,nomeAprovado, novo.getIp(),userlogado.getUserName() );
                                Integer codigopessoa = 1;
                                if (codigopessoa ==null) throw new InvalitInsert("Não gerou codigo de pessoa fisica do interessado "+nomeAprovado+" no cadun!");
                                nomeAprovado = aprov.getNome()+"-"+Descricao;
                                if ( contador ==1){
                                    id_Document = admissaoEnvioAssinaturaRepository.insertDocument(tipodocumento,procnumero,ano, ContadorEvento );
                                    if (id_Document==null ) throw new InvalitInsert("Nao gerou documento tipo:"+tipodocumento+" processo:"+procnumero+'/'+ano);
                                }
                                admissaoEnvioAssinaturaRepository.insertPessoaInteressada(procnumero,ano, codigopessoa , 2,0  );
                                admissaoEnvioAssinaturaRepository.insertArquivoDocument(  id_Document , nomeAprovado, doc.getDocumentoCastorId() ) ;

                                if (contador ==  ArquivosPorDocs){
                                    contador= 0;
                                    ContadorEvento++;
                                }
                                contador++;
                            }




                            //atualiza o campo processo no envio com o numero e ano do processo econtas
                            envio.setProcesso(procnumero+"/"+ano);
                            envio.setStatus(ProcessoAdmissao.Status.concluido.getValor());
                            processoAdmissaoRepository.update(envio);
                        } else {
                            throw new Exception("erro:id do protocolo não foi gerado!");
                        }
                   }
                    else{
                        throw new Exception("erro:id do envio não encontrado!");
                    }

                }
            }
            //throw new SQLException("Test erro handling");
        } else {
            System.out.println("erro:não encontrou usuario logado!!");
        }

        // }catch(Exception e){
        //    System.out.println("[falha]: " + e.toString());
        //    e.printStackTrace();
        //    return ResponseEntity.badRequest().body(e.getMessage());
        //}

        return ResponseEntity.ok().body("OK");
    }

    @CrossOrigin
    @PostMapping(path="/getPessoaByCPF")
    public static String getPessoaBycpf(@RequestBody String cpf) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType,"cpf="+cpf);
        Request request = new Request.Builder()
                .url("https://dev2.tce.to.gov.br/cadun/app/controllers/?&c=TCE_CADUN_PessoaFisica&m=getPessoaByCPF")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Referer", "https://app.tce.to.gov.br/")
                .build();

        Response response = client.newCall(request).execute();
        String resposta = response.body().string();
        return resposta;
    }


}
