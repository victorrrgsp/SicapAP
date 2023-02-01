package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.adm.AdmEnvioAssinatura;
import com.example.sicapweb.model.dto.AssuntoProcessoDTO;
import com.example.sicapweb.repository.concessao.*;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import com.example.sicapweb.repository.movimentacaoDePessoal.PensionistaRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.service.AssinarCertificado;
import com.example.sicapweb.service.ChampionRequest;
import com.example.sicapweb.service.SHA2;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/assinarConcessao")
public class AssinarConcessaoController extends DefaultController<AdmEnvio> {

    @Value("${sso.oauth2.client_id}")
    private String sso_client_id;

    @Value("${sso.oauth2.client_secret}")
    private String sso_client_secret;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    @Autowired
    private AdmEnvioAssinaturaRepository admEnvioAssinaturaRepository;

    @Autowired
    private UnidadeGestoraRepository unidadeGestoraRepository;

    @Autowired
    private PensionistaRepository pensionistaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @Autowired
    private DocumentoPensaoRepository documentoPensaoRepository;

    @Autowired
    private DocumentoReintegracaoRepository documentoReintegracaoRepository;

    @Autowired
    private DocumentoReconducaoRepository documentoReconducaoRepository;

    @Autowired
    private DocumentoReadaptacaoRepository documentoReadaptacaoRepository;

    @Autowired
    private DocumentoAproveitamentoRepository documentoAproveitamentoRepository;


    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<AdmEnvio>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        if (User.getUser(admEnvioRepository.getRequest()).getCargo().getValor() != 4) {
            return ResponseEntity.ok().body(null);
        } else {
            PaginacaoUtil<AdmEnvio> paginacaoUtil = admEnvioRepository.buscaPaginada(pageable, searchParams, tipoParams);
            return ResponseEntity.ok().body(paginacaoUtil);
        }
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
            List<BigInteger> listaIds = new ArrayList<>();

            if (arrayNodeproc.isArray()) {
                while (itrproc.hasNext()) {
                    JsonNode aux = itrproc.next();
                    listaIds.add(aux.get("id").bigIntegerValue());
                }

                List<BigInteger> newIds = listaIds.stream().distinct().collect(Collectors.toList());

                for (BigInteger id : newIds) {
                    // para cada envio adiciona uma linha na tabela de assinatura com o mesmo hash assinado e assinante
                    AdmEnvio envio = admEnvioRepository.findById(id);
                    if (envio != null) {
                        AdmEnvioAssinatura admEnvioAssinatura = new AdmEnvioAssinatura();
                        admEnvioAssinatura.setIdCargo(User.getUser(admEnvioRepository.getRequest()).getCargo().getValor());
                        admEnvioAssinatura.setCpf(User.getUser(admEnvioRepository.getRequest()).getCpf());
                        admEnvioAssinatura.setIpAssinante(InetAddress.getLocalHost().getHostAddress());
                        admEnvioAssinatura.setAdmEnvio(envio);
                        admEnvioAssinatura.setData_assinatura(new Date());
                        admEnvioAssinatura.setHash_assinante(hashassinante);
                        admEnvioAssinatura.setHash_assinado(hashassinado);

                        try {
                            gerarProcesso(envio.getId());

                            admEnvioAssinaturaRepository.save(admEnvioAssinatura);
                            envio.setStatus(4);
                            admEnvioRepository.update(envio);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.ok().body("falha");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body("falha");
        }
        return ResponseEntity.ok().body("sucesso");
    }

    public void gerarProcesso(BigInteger idAdmEnvio) {
        try {
            AdmEnvio admEnvio = admEnvioRepository.findById(idAdmEnvio);
            AssuntoProcessoDTO assunto = getAssuntoProcesso(admEnvio.getTipoRegistro());
            Map<String, Object> entidade = getUnidadeOrigemVinculada(admEnvio.getUnidadeGestora(), admEnvio.getOrgaoOrigem());

            Integer numprotocolo = admEnvioAssinaturaRepository.gerarProtocolo();

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/Y HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Araguaina"));
            String timestamp = dateFormat.format(date.getTime());

            MessageDigest md = MessageDigest.getInstance("MD5");
            String value = "^TC3TO" + timestamp + numprotocolo + "*000003*^";
            md.update(value.getBytes());
            String hash = SHA2.stringHexa(md.digest());
            hash = hash.substring(17, 32).toUpperCase();
            String matricula = "000003"; //matricula do usuario do SICAP -- quando o sistema gera o processo
            Integer entidadeOrigem = (Integer) entidade.get("origem");
            Integer entidadeVinculada = (Integer) entidade.get("vinculada");
            Integer cargoGestor = 4;


            Map<String, Object> protocolo = new HashMap<>();
            protocolo.put("matricula", matricula);
            protocolo.put("entidadeorigem", entidadeOrigem);
            protocolo.put("hash", hash);
            protocolo.put("idprotocolo", numprotocolo);

            Integer gestorResponsavel = (Integer) entidade.get("responsavel");

            //para os casos de Readaptacao, Reconducao, Reintegracao, Aproveitamento e Reversao
            //a origem deve ser quem fez o pedido
            if (assunto.getAssuntocodigo().equals("18") || assunto.getAssuntocodigo().equals("19") ||
                    assunto.getAssuntocodigo().equals("20") || assunto.getAssuntocodigo().equals("21") ||
                    assunto.getAssuntocodigo().equals("22")) {
                entidadeOrigem = (Integer) entidade.get("solicitante");
                protocolo.put("entidadeorigem", entidade.get("solicitante"));
                gestorResponsavel = (Integer) entidade.get("responsavel_solicitante");
            }

//        admEnvioAssinaturaRepository.salvarProtocolo(protocolo);

            /* configuracoes do processo */
            Integer ano = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> processo = new HashMap<>();
            processo.put("procnumero", numprotocolo);
            processo.put("ano", ano);
            processo.put("anoreferencia", ano);
            processo.put("relatoria", 70); //distribuicao para o corpo especial de auditores
            processo.put("complemento", admEnvio.getComplemento());
            processo.put("assuntocodigo", assunto.getAssuntocodigo());
            processo.put("classeassunto", assunto.getClasseassunto());
            processo.put("idprotocolo", numprotocolo);
            processo.put("entidadeorigem", entidadeOrigem);
            processo.put("entidadevinculada", entidadeVinculada);
            processo.put("idassunto", assunto.getIdassunto());
            processo.put("processoNpai", 0);
            processo.put("processoApai", 0);

//        admEnvioAssinaturaRepository.salvarProcesso(processo);
//        admEnvioAssinaturaRepository.salvarAndamentoProcesso(processo);

            /* configuracoes do Gestor */
            Map<String, Object> interessado = new HashMap<>();
            List<Map> interessados = new ArrayList<>();
            interessado.put("procnumero", processo.get("procnumero"));
            interessado.put("ano", processo.get("ano"));
            interessado.put("idpessoa", gestorResponsavel);
            interessado.put("papel", 1);
            interessado.put("idcargo", cargoGestor);
            interessados.add(interessado);

//        admEnvioAssinaturaRepository.salvarPessoasInteressadas(interessado);

            /* configuracoes do Interessado */
            //verifica se e pensao ou revisao de pensao para salvar os interessados
            Map<String, Object> outrosInteressados = new HashMap<>();
            if (assunto.getAssuntocodigo().equals("8") || assunto.getAssuntocodigo().equals("12")) {
                List<Object> dependentes = pensionistaRepository.getDependentesPensao(admEnvio.getAdmissao().getServidor().getCpfServidor());

                if (dependentes != null) {
                    for (Object obj : dependentes) {
                        ResponseEntity<String> pessoaInteressada = ChampionRequest.salvarSimples(((HashMap) obj).get("cpf").toString(), ((HashMap) obj).get("nome").toString(), "SICAPAP", sso_client_id, sso_client_secret);
                        JsonNode respostaJson = new ObjectMapper().readTree(pessoaInteressada.getBody());
                        outrosInteressados.put("procnumero", processo.get("procnumero"));
                        outrosInteressados.put("ano", processo.get("ano"));
                        outrosInteressados.put("idpessoa", respostaJson.get("id").asInt());
                        outrosInteressados.put("papel", 2);
                        outrosInteressados.put("idcargo", 0);
                        interessados.add(outrosInteressados);

//                    admEnvioAssinaturaRepository.salvarPessoasInteressadas(interessado);
                    }
                }

                //salva o instituidor 14
                ResponseEntity<String> pessoaInteressada = ChampionRequest.salvarSimples(admEnvio.getAdmissao().getServidor().getCpfServidor(), admEnvio.getAdmissao().getServidor().getNome(), "SICAPAP", sso_client_id, sso_client_secret);
                JsonNode respostaJson = new ObjectMapper().readTree(pessoaInteressada.getBody());
                outrosInteressados.put("procnumero", processo.get("procnumero"));
                outrosInteressados.put("ano", processo.get("ano"));
                outrosInteressados.put("idpessoa", respostaJson.get("id").asInt());
                outrosInteressados.put("papel", 14);
                outrosInteressados.put("idcargo", 0);
                interessados.add(outrosInteressados);

//                admEnvioAssinaturaRepository.salvarPessoasInteressadas(interessado);
            } else {
                ResponseEntity<String> pessoaInteressada = ChampionRequest.salvarSimples(admEnvio.getAdmissao().getServidor().getCpfServidor(), admEnvio.getAdmissao().getServidor().getNome(), "SICAPAP", sso_client_id, sso_client_secret);
                JsonNode respostaJson = new ObjectMapper().readTree(pessoaInteressada.getBody());
                outrosInteressados.put("procnumero", processo.get("procnumero"));
                outrosInteressados.put("ano", processo.get("ano"));
                outrosInteressados.put("idpessoa", respostaJson.get("id").asInt());
                outrosInteressados.put("papel", 2);
                outrosInteressados.put("idcargo", 0);
                interessados.add(outrosInteressados);

//                admEnvioAssinaturaRepository.salvarPessoasInteressadas(interessado);
            }
            List<Object> arquivos = consultarDocumentosAEnviar(admEnvio);

            admEnvioAssinaturaRepository.salvarProtocolo(protocolo);
            admEnvioAssinaturaRepository.salvarProcesso(processo);
            admEnvioAssinaturaRepository.salvarAndamentoProcesso(processo);

            for (Map mapa : interessados) {
                admEnvioAssinaturaRepository.salvarPessoasInteressadas(mapa);
            }

            admEnvioAssinaturaRepository.salvarHistorico1(processo);
            admEnvioAssinaturaRepository.salvarHistorico2(processo);

            //salva o documento e retorna o id
            admEnvioAssinaturaRepository.salvarDocumento(processo);
            Integer id_documento = admEnvioAssinaturaRepository.buscarUltimoIdDocumento();


            for (Object arquivo : arquivos) {
                admEnvioAssinaturaRepository.salvarArquivosDocumentos((Map<String, Object>) arquivo, id_documento);
            }

            admEnvio.setProcesso(processo.get("procnumero") + "/" + processo.get("ano"));
            admEnvioRepository.update(admEnvio);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AssuntoProcessoDTO getAssuntoProcesso(Integer tipoRegistro) {
        AssuntoProcessoDTO dto = new AssuntoProcessoDTO();
        switch (tipoRegistro) {
            case 1: //Aposentadoria
                dto.setIdassunto("71");
                dto.setAssuntocodigo("7");
                dto.setClasseassunto("8");
                break;
            case 2: //Pensao
                dto.setIdassunto("77");
                dto.setAssuntocodigo("8");
                dto.setClasseassunto("8");
                break;
            case 3: //Reserva
                dto.setIdassunto("83");
                dto.setAssuntocodigo("9");
                dto.setClasseassunto("8");
                break;
            case 4: //Reforma
                dto.setIdassunto("89");
                dto.setAssuntocodigo("10");
                dto.setClasseassunto("8");
                break;
            case 5: //Reintegracao
                dto.setIdassunto("148");
                dto.setAssuntocodigo("19");
                dto.setClasseassunto("8");
                break;
            case 6: //Reconducao
                dto.setIdassunto("112");
                dto.setAssuntocodigo("18");
                dto.setClasseassunto("8");
                break;
            case 7: // Readaptacao
                dto.setIdassunto("175");
                dto.setAssuntocodigo("21");
                dto.setClasseassunto("8");
                break;
            case 8: //Aproveitamento
                dto.setIdassunto("176");
                dto.setAssuntocodigo("22");
                dto.setClasseassunto("8");
                break;
            case 9: //Revisao de Aposentadoria
                dto.setIdassunto("94");
                dto.setAssuntocodigo("11");
                dto.setClasseassunto("8");
                break;
            case 10: //Revisao de pensao
                dto.setIdassunto("98");
                dto.setAssuntocodigo("12");
                dto.setClasseassunto("8");
                break;
            case 11: //Revisao de reserva
                dto.setIdassunto("101");
                dto.setAssuntocodigo("13");
                dto.setClasseassunto("8");
                break;
            case 12: //Revisao de reforma
                dto.setIdassunto("104");
                dto.setAssuntocodigo("14");
                dto.setClasseassunto("8");
                break;
            case 13: //Reversao de Aposentadoria/ Reserva
                dto.setIdassunto("172");
                dto.setAssuntocodigo("20");
                dto.setClasseassunto("8");
                break;
        }
        return dto;
    }

    public List<Object> consultarDocumentosAEnviar(AdmEnvio admEnvio) {
        try {
            if (admEnvio != null) {
                List<Object> documentos;
                switch (admEnvio.getTipoRegistro()) {
                    case 1:
                        documentos = documentoAposentadoriaRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 2:
                        documentos = documentoPensaoRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 3:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosReserva(admEnvio.getId());
                        return documentos;
                    case 4:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosReforma(admEnvio.getId());
                        return documentos;
                    case 5:
                        documentos = documentoReintegracaoRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 6:
                        documentos = documentoReconducaoRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 7:
                        documentos = documentoReadaptacaoRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 8:
                        documentos = documentoAproveitamentoRepository.buscarDocumentos(admEnvio.getId());
                        return documentos;
                    case 9:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosRevisao(admEnvio.getId());
                        return documentos;
                    case 10:
                        documentos = documentoPensaoRepository.buscarDocumentosRevisao(admEnvio.getId());
                        return documentos;
                    case 11:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosRevisaoReserva(admEnvio.getId());
                        return documentos;
                    case 12:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosRevisaoReforma(admEnvio.getId());
                        return documentos;
                    case 13:
                        documentos = documentoAposentadoriaRepository.buscarDocumentosReversao(admEnvio.getId());
                        return documentos;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping(path = "/gerar")
    public void teste() throws ApplicationException, IOException, URISyntaxException, NoSuchAlgorithmException {
//        ResponseEntity<String> result = ChampionRequest.salvarSimples("06562780136", "LARA FLAVIA DE ALMEIDA LIMA", "SICAPAP", sso_client_id, sso_client_secret);
//        JsonNode respostaJson = new ObjectMapper().readTree(result.getBody());
//        respostaJson.get("id");

        Map<String, String> teste = new HashMap<>();
        teste.put("1", "1");
        teste.put("2", "2");

        List<Map> coiso = new ArrayList<>();
        coiso.add(teste);
        System.out.println(coiso);
    }


    public Map<String, Object> getUnidadeOrigemVinculada(String cnpj, String orgaoOrigem) throws Exception {
        try {
            Object ug = unidadeGestoraRepository.buscarDadosUnidadeGestora(cnpj);
            Object ugOrgaoOrigem = unidadeGestoraRepository.buscarDadosUnidadeGestora(orgaoOrigem);
            Map<String, Object> theMap = new HashMap<>();

            if (Objects.equals(((HashMap) ug).get("Divisao_id"), 5) || Objects.equals(((HashMap) ug).get("Divisao_id"), 12)) {
                Object instituto = unidadeGestoraRepository.buscarDadosFundoOuInstituto(cnpj);
                theMap.put("temInstituto", true);
                theMap.put("origem", ((HashMap) instituto).get("idPessoaJuridica"));
                theMap.put("vinculada", ((HashMap) ugOrgaoOrigem).get("idPessoaJuridica"));
                theMap.put("solicitante", ((HashMap) ug).get("idPessoaJuridica"));
                theMap.put("responsavel", ((HashMap) instituto).get("idPessoaFisica"));
                theMap.put("responsavel_solicitante", ((HashMap) ug).get("idPessoaFisica"));
            } else {
                Object instituto = unidadeGestoraRepository.buscarDadosFundoOuInstituto(cnpj);
                if (instituto == null) {
                    theMap.put("temInstituto", false);
                    theMap.put("origem", ((HashMap) ug).get("idPessoaJuridica"));
                    theMap.put("vinculada", ((HashMap) ugOrgaoOrigem).get("idPessoaJuridica"));
                    theMap.put("solicitante", ((HashMap) ug).get("idPessoaJuridica"));
                    theMap.put("responsavel", ((HashMap) ug).get("idPessoaFisica"));
                    theMap.put("responsavel_solicitante", ((HashMap) ug).get("idPessoaFisica"));
                } else {
                    theMap.put("temInstituto", true);
                    theMap.put("origem", ((HashMap) instituto).get("idPessoaJuridica"));
                    theMap.put("vinculada", ((HashMap) ugOrgaoOrigem).get("idPessoaJuridica"));
                    theMap.put("solicitante", ((HashMap) ug).get("idPessoaJuridica"));
                    theMap.put("responsavel", ((HashMap) instituto).get("idPessoaFisica"));
                    theMap.put("responsavel_solicitante", ((HashMap) ug).get("idPessoaFisica"));
                }
            }
            return theMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                respostaIniciarAssinatura = AssinarCertificado.inicializarAssinatura(certificado, Original);
            }
        } catch (Exception e) {
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
            respostaFinalizarAssinatura = AssinarCertificado.FinalizarAssinatura(desafio, assinatura, mensagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(respostaFinalizarAssinatura);
    }

}
