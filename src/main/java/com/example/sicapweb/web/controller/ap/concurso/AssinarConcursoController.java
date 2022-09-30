package com.example.sicapweb.web.controller.ap.concurso;


import br.gov.to.tce.model.ap.concurso.documento.DocumentoEdital;
import br.gov.to.tce.model.ap.concurso.documento.DocumentoEditalHomologacao;
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
import org.springframework.beans.factory.annotation.Value;
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
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/assinarConcurso")
public class AssinarConcursoController {

    @Autowired
    private ConcursoEnvioRepository concursoEnvioRepository;


    @Autowired
    private ConcursoEnvioAssinaturaRepository concursoEnvioAssinaturaRepository;

    @Autowired
    private DocumentoEditalRepository documentoEditalRepository;

    @Autowired
    private DocumentoEditalHomologacaoRepository documentoEditalHomologacaoRepository;

    private final String matriculaSicapApNoEcontas = "000003";
    private Integer idAssunto;
    private Integer assuntoCodigo;
    private Integer classeAssunto;
    private String deptoAutuacao = "";
    private String tipoDocumento = "";

    private final Integer relatorio = 70;
    private String complemento;
    private final Integer eventoProcesso = 1;

    @CrossOrigin
    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ConcursoEnvioAssRetorno>> listaAEnviosAguardandoAss(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        if (User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor() != 4) {
            List<ConcursoEnvioAssRetorno> listavazia = new ArrayList<>();
            PaginacaoUtil<ConcursoEnvioAssRetorno> paginacaoUtilvazia = new PaginacaoUtil<>(0, 1, 1, 0, listavazia);
            return ResponseEntity.ok().body(paginacaoUtilvazia);
        }
        PaginacaoUtil<ConcursoEnvioAssRetorno> paginacaoUtil = concursoEnvioRepository.buscarEnviosAguardandoAss(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> AssinarConcurso(@RequestBody String hashassinante_hashAssinado) throws JsonProcessingException, Exception {
        validaUsuarioAssinante();
        JsonNode requestJson = new ObjectMapper().readTree(hashassinante_hashAssinado);
        String hashassinante = URLDecoder.decode(requestJson.get("hashassinante").asText(), StandardCharsets.UTF_8);
        String hashassinado = URLDecoder.decode(requestJson.get("hashassinado").asText(), StandardCharsets.UTF_8);
        String processosBase64Decoded = new String(Base64.getDecoder().decode(hashassinante.getBytes()));
        ArrayNode arrayNodeproc = (ArrayNode) new ObjectMapper().readTree(processosBase64Decoded);
        Iterator<JsonNode> itrproc = arrayNodeproc.elements();
        //itera sobre os varios envios que foram escolhidos no front para assinar de uma so vez
        if (arrayNodeproc.isArray()) {
            while (itrproc.hasNext()) {
                JsonNode aux = itrproc.next();

                // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante
                BigInteger idenvio = aux.get("id").bigIntegerValue();
                ConcursoEnvio envio = concursoEnvioRepository.findById(idenvio);
                ValidaEnvio(envio);

                //gera assinatura do envio
                ConcursoEnvioAssinatura novaAssinaturaConcurso = new ConcursoEnvioAssinatura();
                novaAssinaturaConcurso.setIdCargo(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCargo().getValor());
                novaAssinaturaConcurso.setCpf(User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getCpf());
                novaAssinaturaConcurso.setIp(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr());
                novaAssinaturaConcurso.setConcursoEnvio(envio);
                novaAssinaturaConcurso.setData_Assinatura(LocalDateTime.now());
                novaAssinaturaConcurso.setHashAssinante(hashassinante);
                novaAssinaturaConcurso.setHashAssinado(hashassinado);
                concursoEnvioAssinaturaRepository.save(novaAssinaturaConcurso);

                GerarProcesso(envio);
                envio.setStatus(ConcursoEnvio.Status.Concluido.getValor());
                concursoEnvioRepository.update(envio);
            }
        }

        return ResponseEntity.ok().body("OK");
    }

    @CrossOrigin
    @PostMapping(path = "/iniciarAssinatura")
    public ResponseEntity<?> iniciarAssinatura(@RequestBody String certificado_mensagem_hash) {
        try {
            User userlogado = User.getUser(concursoEnvioAssinaturaRepository.getRequest());
            JsonNode respostaJson = new ObjectMapper().readTree(certificado_mensagem_hash);
            String certificado = respostaJson.get("certificado").asText();
            String Original = respostaJson.get("original").asText();
            String hash = URLDecoder.decode(respostaJson.get("hashcertificado").asText(), StandardCharsets.UTF_8);
            if (userlogado == null) {
                throw new Exception("nao encontrou usuario logado!!");
            } else if (!userlogado.getHashCertificado().equals(hash)) {
                throw new Exception("nao é o mesmo  certificado que esta logado!!");
            } else {
                return ResponseEntity.ok().body(AssinarCertificadoDigital.inicializarAssinatura(certificado, Original));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


    @CrossOrigin
    @PostMapping(path = "/finalizarAssinatura")
    public ResponseEntity<?> finalizarAssinatura(@RequestBody String desafio_assinatura_mensagem) {
        try {
            JsonNode respostaJson = new ObjectMapper().readTree(desafio_assinatura_mensagem);
            String desafio = respostaJson.get("desafio").asText();
            String assinatura = respostaJson.get("assinatura").asText();
            String mensagem = respostaJson.get("original").asText();
            return ResponseEntity.ok().body(AssinarCertificadoDigital.FinalizarAssinatura(desafio, assinatura, mensagem));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    private void GerarProcesso(ConcursoEnvio envio) throws NoSuchAlgorithmException {
        //coleta dados do cadun sobre o id  do responsavel da ug e o id da pessoa juridica
        String Cnpj = User.getUser(concursoEnvioAssinaturaRepository.getRequest()).getUnidadeGestora().getId();
        Integer origem = concursoEnvioAssinaturaRepository.getidCADUNPJ(Cnpj);
        Integer responsavel = concursoEnvioAssinaturaRepository.getidCADUNPF(Cnpj);
        Integer id_entidade_vinculada = null;
        if (envio.getOrgaoorigem() != null) {
            id_entidade_vinculada = concursoEnvioAssinaturaRepository.getidCADUNPJ(envio.getOrgaoorigem());
        }
        SetaParametrosDoProcessoPelaFaseDoConcurso(envio.getFase().intValue());
        LocalDateTime dataHoraProtocolo = LocalDateTime.now();
        Integer idProtocolo = concursoEnvioAssinaturaRepository.insertProtocolo(matriculaSicapApNoEcontas, dataHoraProtocolo.getYear(), dataHoraProtocolo, origem);

        //prepara  variaveis de processo
        Integer numeroProcesso = idProtocolo;
        Integer anoProcesso = dataHoraProtocolo.getYear();
        Integer numeroProcessoPai = null;
        Integer anoProcessoPai = null;
        if (envio.getProcessoPai() != null) {
            String[] processo = envio.getProcessoPai().split("/");
            numeroProcessoPai = Integer.valueOf(processo[0]);
            anoProcessoPai = Integer.valueOf(processo[1]);
        }

        complemento = envio.getComplemento();
        Integer numeroEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(0, envio.getEdital().getNumeroEdital().length() - 4));
        Integer anoEdital = Integer.valueOf(envio.getEdital().getNumeroEdital().substring(envio.getEdital().getNumeroEdital().length() - 4));
        ValidaEdital(numeroEdital, anoEdital);

        //inseri informaçoes principaldo envio no econtas
        concursoEnvioAssinaturaRepository.insertProcesso(numeroProcesso, anoProcesso, anoEdital, numeroProcessoPai, anoProcessoPai, relatorio, complemento, assuntoCodigo, classeAssunto, idProtocolo, origem, id_entidade_vinculada, idAssunto);
        concursoEnvioAssinaturaRepository.insertAndamentoProcesso(numeroProcesso, anoProcesso);
        concursoEnvioAssinaturaRepository.insertProcEdital(numeroProcesso, anoProcesso, numeroEdital, anoEdital);
        concursoEnvioAssinaturaRepository.insertPessoaInteressada(numeroProcesso, anoProcesso, responsavel, 1, 4);
        concursoEnvioAssinaturaRepository.insertHist(numeroProcesso, anoProcesso, deptoAutuacao);
        BigDecimal idDocumento = concursoEnvioAssinaturaRepository.insertDocument(tipoDocumento, numeroProcesso, anoProcesso, eventoProcesso);
        GravaDocumentos(envio,idDocumento);
        envio.setProcesso(numeroProcesso + "/" + anoProcesso);
    }

    private void GravaDocumentos(ConcursoEnvio envio,BigDecimal idDocumento){
        if (envio.getFase() == ConcursoEnvio.Fase.Edital.getValor()) {
            GravaDocumentosFaseEdital(envio,idDocumento);
        } else if (envio.getFase() == ConcursoEnvio.Fase.Homologacao.getValor()) {
            GravaDocumentosFaseHomologacao(envio,idDocumento);
        }
    }

    private void validaUsuarioAssinante() {
        User userlogado = User.getUser(concursoEnvioRepository.getRequest());
        if (userlogado != null) {
            if (userlogado.getCargo().getValor() != 4)
                throw new RuntimeException("Apenas o gestor da unidade gestora pode assinar envios!!");
            if (userlogado.getUnidadeGestora().getId().equals("00000000000000"))
                throw new RuntimeException("Não assina envios na ug de teste!!");
        } else {
            throw new RuntimeException("não encontrou usuario logado!!");
        }
    }

    private void ValidaEdital(Integer numEdital, Integer anoEdital) {
        if (numEdital == null || numEdital == 0) throw new InvalitInsert("numero do edital não esta no formato certo!");
        if (anoEdital == null || anoEdital < 1990)
            throw new InvalitInsert("numero do edital não esta no formato certo!");
    }

    private void SetaParametrosDoProcessoPelaFaseDoConcurso(int fase) {
        if (fase == ConcursoEnvio.Fase.Edital.getValor().intValue()) {
            idAssunto = 64;
            assuntoCodigo = 6;
            classeAssunto = 8;
            deptoAutuacao = "COCAP";
            tipoDocumento = "TA";
        } else if (fase == ConcursoEnvio.Fase.Homologacao.getValor().intValue()) {
            idAssunto = 161;
            assuntoCodigo = 1;
            classeAssunto = 15;
            deptoAutuacao = "COREA";
            tipoDocumento = "HOMOL";
        }
    }

    private void GravaDocumentosFaseEdital(ConcursoEnvio envio, BigDecimal idDocumento) {
        List<DocumentoEdital> listaDeDocumentosEdital = documentoEditalRepository.buscarDocumentosEdital("'I','II','III','IV','V','VI','VII','VIII','IX','IX.I','X','sem'", envio.getEdital().getId());
        if (listaDeDocumentosEdital.stream().filter(documentoEdital -> !documentoEdital.getInciso().equals("sem")).collect(Collectors.toList()).size() < 11)
            throw new InvalitInsert("não encontrou documentos todos documentos obrigatorio anexados!!");
        String Arquivo = "";
        for (DocumentoEdital documentoEdital : listaDeDocumentosEdital) {
            Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(documentoEdital.getInciso(), envio.getFase());
            concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocumento, Arquivo, documentoEdital.getIdCastorFile());
        }
    }

    private void GravaDocumentosFaseHomologacao(ConcursoEnvio envio, BigDecimal idDocumento){
        List<DocumentoEditalHomologacao> listaDeDocumentosHomologacaoConcurso = documentoEditalHomologacaoRepository.buscarDocumentosEditalHomologacao("'XI','XII','XIII','XIV','XV','sem'", envio.getEdital().getId());
        if (listaDeDocumentosHomologacaoConcurso.stream().filter(documentohomologacao -> !documentohomologacao.getInciso().equals("sem")).collect(Collectors.toList()).size() < 5)
            throw new InvalitInsert("não encontrou documentos todos documentos obrigatorio anexados!!");
        String Arquivo = "";
        for (DocumentoEditalHomologacao doc : listaDeDocumentosHomologacaoConcurso) {
            Arquivo = concursoEnvioAssinaturaRepository.GetDescricaoArquivoEdital(doc.getInciso(), envio.getFase());
            concursoEnvioAssinaturaRepository.insertArquivoDocument(idDocumento, Arquivo, doc.getIdCastorFile());
        }
    }

    private void ValidaEnvio(ConcursoEnvio envio ){
        if (envio ==null ) throw new InvalitInsert("Envio não encontrado!!");
        if (envio.getStatus() == ConcursoEnvio.Status.Concluido.getValor())
            throw new InvalitInsert("Envio ja Assinado!!");
    }



}
