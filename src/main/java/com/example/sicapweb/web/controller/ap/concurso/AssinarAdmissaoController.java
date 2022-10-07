package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.AdmissaoEnvio;
import br.gov.to.tce.model.ap.concurso.AdmissaoEnvioAssinatura;
import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoAdmissao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.model.AdmissaoEnvioAssRetorno;
import com.example.sicapweb.repository.concurso.AdmissaoEnvioAssinaturaRepository;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
import com.example.sicapweb.repository.concurso.AdmissaoEnvioRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.service.AssinarCertificadoDigital;
import com.example.sicapweb.util.PaginacaoUtil;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/assinarAdmissao")
public class AssinarAdmissaoController {
    @Autowired
    private AdmissaoEnvioRepository admissaoEnvioRepository;

    @Autowired
    private AdmissaoEnvioAssinaturaRepository admissaoEnvioAssinaturaRepository;

    @Autowired
    private DocumentoAdmissaoRepository documentoAdmissaoRepository;

    private final Integer arquivosPorDoc =100;

    private final Integer relatorio=70;

    private final String  deptoAutuacao = "COCAP";

    private final String matricula = "000003";
    private final Integer  idAssunto = 8;
    private final Integer assuntoCodigo = 1;
    private final Integer classeAssunto  = 8;

    private final  String tipoDocumento="TA";

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmissaoEnvioAssRetorno>> listaAProcessosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        if (User.getUser(admissaoEnvioRepository.getRequest()).getCargo().getValor()!=4 ){
            List<AdmissaoEnvioAssRetorno> listavazia= new ArrayList<>() ;
            PaginacaoUtil<AdmissaoEnvioAssRetorno> paginacaoUtilvazia= new PaginacaoUtil<AdmissaoEnvioAssRetorno>(0, 1, 1, 0, listavazia);
            return ResponseEntity.ok().body(paginacaoUtilvazia);
        }
        return ResponseEntity.ok().body(admissaoEnvioRepository.buscarProcessosAguardandoAss(pageable,searchParams,tipoParams));
    }

    @CrossOrigin
    @PostMapping(path="/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash  ){
        try {
            User userlogado = User.getUser(admissaoEnvioAssinaturaRepository.getRequest());
            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagem_hash);
            JsonNode certificadoJson = new ObjectMapper().readTree(userlogado.getCertificado());
            String certificado = respostaJson.get("certificado").asText();
            String Original =  respostaJson.get("original").asText();
            String hash = URLDecoder.decode(respostaJson.get("hashcertificado").asText(), StandardCharsets.UTF_8)  ;
            if(!userlogado.getHashCertificado().equals(hash) )  throw new Exception("nao é o mesmo  certificado que esta logado!!");
            return ResponseEntity.ok().body(AssinarCertificadoDigital.inicializarAssinatura(certificado,Original));
        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @CrossOrigin
    @PostMapping(path="/finalizarAssinatura")
    public ResponseEntity<?> finalizarAssinatura(@RequestBody String desafio_assinatura_mensagem  ){
        try {
            JsonNode respostaJson = new ObjectMapper().readTree(desafio_assinatura_mensagem);
            String desafio=respostaJson.get("desafio").asText();
            String assinatura=respostaJson.get("assinatura").asText();
            String mensagem=respostaJson.get("original").asText();
            return ResponseEntity.ok().body(AssinarCertificadoDigital.FinalizarAssinatura(desafio,assinatura,mensagem));
        } catch(Exception e ){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @CrossOrigin
    @Transactional( rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> AssinarAdmissao(@RequestBody String hashassinante_hashAssinado )  throws Exception {
        validaUsuarioAssinante();
        JsonNode requestJson = new ObjectMapper().readTree(hashassinante_hashAssinado);
        String hashassinante =  URLDecoder.decode(requestJson.get("hashassinante").asText(), StandardCharsets.UTF_8);
        String hashassinado =  URLDecoder.decode(requestJson.get("hashassinado").asText(), StandardCharsets.UTF_8);
        String processosBase64Decoded = new String(Base64.getDecoder().decode(hashassinante.getBytes()));
        ArrayNode arrayNodeprocesso = (ArrayNode) new ObjectMapper().readTree(processosBase64Decoded);
        Iterator<JsonNode> iteradorDosProcessosEnviados = arrayNodeprocesso.elements();

        if (arrayNodeprocesso.isArray()) {
            while (iteradorDosProcessosEnviados.hasNext()) {
                JsonNode currentEnvioJsonNode = iteradorDosProcessosEnviados.next();
                // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante
                BigInteger idenvio =  currentEnvioJsonNode.get("id").bigIntegerValue();
                AdmissaoEnvio envio =  admissaoEnvioRepository.findById(idenvio);
                ValidaEnvio(envio);
                AdmissaoEnvioAssinatura  novoAssinaturaAdmissao = new AdmissaoEnvioAssinatura();
                novoAssinaturaAdmissao.setIdCargo(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
                novoAssinaturaAdmissao.setCpf(User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getCpf());
                ServletRequestAttributes getIp = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                novoAssinaturaAdmissao.setIp(getIp.getRequest().getRemoteAddr());
                novoAssinaturaAdmissao.setAdmissaoEnvio(envio);
                LocalDateTime dataHoraAssinatura =LocalDateTime.now();
                novoAssinaturaAdmissao.setData_Assinatura(Date.from(dataHoraAssinatura.atZone(ZoneId.systemDefault()).toInstant()));
                novoAssinaturaAdmissao.setHashAssinante(hashassinante);
                novoAssinaturaAdmissao.setHashAssinado(hashassinado);
                admissaoEnvioAssinaturaRepository.save(novoAssinaturaAdmissao);

                GerarProcesso(envio);
                //atualiza o campo processo no envio com o numero e ano do processo econtas
                envio.setStatus(AdmissaoEnvio.Status.concluido.getValor());
                admissaoEnvioRepository.update(envio);
            }
        }

        return ResponseEntity.ok().body("OK");
    }



    private void GravarArquivosDeAprovadosNoProcesso(List<DocumentoAdmissao> listaDeDocumentosAprovados, Integer numeroProcesso,Integer anoProcesso ) throws IOException, URISyntaxException {
        String responseCadunsalvasimples;
        Integer codigoPessoa;
        Integer contadorArquivosGravadosPorDocumento=1;
        Integer ContadorEvento =1;
        BigDecimal idDocument=null;
        for (DocumentoAdmissao documentoAdmissao :  listaDeDocumentosAprovados ){
            EditalAprovado aprovado = documentoAdmissao.getEditalAprovado();
            String cpfAprovado = aprovado.getCpf();
            String nomeAprovado = aprovado.getNome()+( (documentoAdmissao.getOpcaoDesistencia() ==null) ? "" :  Arrays.stream(DocumentoAdmissao.opcaoDesistencia.values()).filter(opcaoDesistencia1 -> opcaoDesistencia1.getValor().intValue()==documentoAdmissao.getOpcaoDesistencia().intValue() ).collect(Collectors.toList()).get(0).getLabel());
            //sera necessario gerar a chave atravez do metodo id_Document 'exec cadun.dbo.obterCodigoNovaPessoa'
            responseCadunsalvasimples = admissaoEnvioAssinaturaRepository.insertCadunPessoaInterressada(cpfAprovado,nomeAprovado);
            codigoPessoa=ValidaPessoaRetornadaCadun(responseCadunsalvasimples,cpfAprovado,nomeAprovado);
            if ( contadorArquivosGravadosPorDocumento ==1){
                idDocument = admissaoEnvioAssinaturaRepository.insertDocument(tipoDocumento,numeroProcesso,anoProcesso, ContadorEvento );
            }
            admissaoEnvioAssinaturaRepository.insertPessoaInteressada(numeroProcesso,anoProcesso, codigoPessoa , 2,0  );
            admissaoEnvioAssinaturaRepository.insertArquivoDocument(  idDocument , nomeAprovado, documentoAdmissao.getDocumentoCastorId() ) ;
            if (contadorArquivosGravadosPorDocumento ==  arquivosPorDoc){
                contadorArquivosGravadosPorDocumento= 0;
                ContadorEvento++;
            }
            contadorArquivosGravadosPorDocumento++;
        }
    }

    private Integer ValidaPessoaRetornadaCadun(String StringResponseCadunsalvasimples,String cpfAprovado,String nomeAprovado) throws JsonProcessingException {
        JsonNode JsonesponseCadunsalvasimples = new ObjectMapper().readTree(StringResponseCadunsalvasimples);
        Integer codigoPessoa =  JsonesponseCadunsalvasimples.get("id").asInt();
        String mensagemPessoa = JsonesponseCadunsalvasimples.get("msg").asText();
        if (codigoPessoa.equals(0)) throw new InvalitInsert("erro:"+mensagemPessoa+", cpf: "+cpfAprovado+" nome: "+nomeAprovado);
        if (codigoPessoa ==null) throw new InvalitInsert("Não gerou codigo de pessoa fisica do interessado "+nomeAprovado+" no cadun!");
        return codigoPessoa;
    }

    private void validaUsuarioAssinante(){
        User userlogado = User.getUser(admissaoEnvioRepository.getRequest());
        if (userlogado != null) {
            if (userlogado.getCargo().getValor() !=4 ) throw new RuntimeException("Apenas o gestor da unidade gestora pode assinar envios!!");
            if (userlogado.getUnidadeGestora().getId().equals("00000000000000") ) throw new RuntimeException("Não assina envios na ug de teste!!");
        }else{
            throw new RuntimeException("não encontrou usuario logado!!");
        }
    }


    private void GerarProcesso(AdmissaoEnvio envio) throws IOException, URISyntaxException {
        //coleta dados do cadun sobre o id  do responsavel da ug e o id da pessoa juridica
        String Cnpj = User.getUser(admissaoEnvioAssinaturaRepository.getRequest()).getUnidadeGestora().getId();
        Integer origem =   admissaoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
        Integer responsavel = admissaoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
        LocalDateTime dataHoraDoprotocolo= LocalDateTime.now();
        Integer idprotocolo =admissaoEnvioAssinaturaRepository.insertProtocolo(matricula,dataHoraDoprotocolo.getYear(),dataHoraDoprotocolo,origem);
        //prepara  variaveis de processo
        Integer numeroProcesso = idprotocolo;
        Integer anoProcesso = dataHoraDoprotocolo.getYear();
        Integer numeroProcessoPai= null;
        Integer AnoProcessoPai= null;
        if (envio.getProcessoPai() != null) {
            String[] pc =envio.getProcessoPai().split("/");
            numeroProcessoPai = Integer.valueOf(pc[0]) ;
            AnoProcessoPai = Integer.valueOf(pc[1]) ;
        } else{
            throw new RuntimeException("não encontrou registro do processo do edital no envio!!");
        }
        Integer anoReferencia = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
        admissaoEnvioAssinaturaRepository.insertProcesso(numeroProcesso,anoProcesso,anoReferencia,numeroProcessoPai , AnoProcessoPai ,relatorio, "" ,assuntoCodigo ,classeAssunto , idprotocolo , origem, null,idAssunto );
        admissaoEnvioAssinaturaRepository.insertAndamentoProcesso(numeroProcesso,anoProcesso);
        admissaoEnvioAssinaturaRepository.insertPessoaInteressada(numeroProcesso,anoProcesso, responsavel , 1,4  );
        admissaoEnvioAssinaturaRepository.insertHist(numeroProcesso,anoProcesso,deptoAutuacao);
        // inserir os documentos dos aprovados emposados
        List<DocumentoAdmissao> listaDeDocumentosAprovadosComNomeacao  = documentoAdmissaoRepository.getAprovadosComAdmissao(envio.getId());
        GravarArquivosDeAprovadosNoProcesso(listaDeDocumentosAprovadosComNomeacao,numeroProcesso,anoProcesso);

        //  inserir os documentos dos aprovados nao emposados
        List<DocumentoAdmissao> listaDeDocumentosAprovadosSemNomeacao  = documentoAdmissaoRepository.getAprovadosSemAdmissao(envio.getId());
        GravarArquivosDeAprovadosNoProcesso(listaDeDocumentosAprovadosSemNomeacao,numeroProcesso,anoProcesso);
        envio.setProcesso(numeroProcesso+"/"+anoProcesso);
    }


    private void ValidaEnvio(AdmissaoEnvio envio ){
        if (envio ==null )
            throw new InvalitInsert("Envio não encontrado!!");
        else if (envio.getStatus() == AdmissaoEnvio.Status.concluido.getValor())
            throw new InvalitInsert("Envio ja Assinado!!");
        else if (documentoAdmissaoRepository.getDocumenttosAdmissaoByIdEnvio(envio.getId()).size()==0 ) {
            throw new InvalitInsert("Envio sem aprovados!!");
        }

    }

}
