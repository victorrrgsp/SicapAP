package com.example.sicapweb.web.controller.ap.concessao;


import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoReintegracao;
import br.gov.to.tce.model.ap.pessoal.Reintegracao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.ReintegracaoDTO;
import com.example.sicapweb.repository.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.DocumentoReintegracaoRepository;
import com.example.sicapweb.repository.concessao.ReintegracaoRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReintegracao")
public class ConcessaoReintegracaoController extends DefaultController<DocumentoReintegracao> {

    @Autowired
    private ReintegracaoRepository reintegracaoRepository;

    @Autowired
    private DocumentoReintegracaoRepository documentoReintegracaoRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> reintegracao = new HashMap<String, Object>();

    public class ReintegracaoDocumento{
        private Reintegracao reintegracao;

        private String situacao;

        public Reintegracao getReintegracao() {
            return reintegracao;
        }

        public void setReintegracao(Reintegracao reintegracao) {
            this.reintegracao = reintegracao;
        }

        public String getSituacao() {
            return situacao;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }
    }

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ReintegracaoDTO>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ReintegracaoDTO> paginacaoUtil = reintegracaoRepository.buscaPaginadaReintegracao(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reintegracao list = reintegracaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReintegracao documentoReintegracao = new DocumentoReintegracao();
        documentoReintegracao.setReintegracao(reintegracaoRepository.findById(id));
        documentoReintegracao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Reintegracao");
        documentoReintegracao.setIdCastorFile(idCastor);
        documentoReintegracao.setStatus(DocumentoReintegracao.Status.Informado.getValor());
        documentoReintegracaoRepository.save(documentoReintegracao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Reintegracao> list = reintegracaoRepository.buscarReintegracao();
        ReintegracaoDocumento situacao = new ReintegracaoDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoReintegracaoRepository.findSituacao("documentoReintegracao", "idReintegracao", list.get(i).getId(), "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'");
            if (quantidadeDocumentos == 0) {
                situacao.setReintegracao(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 4) {
                situacao.setReintegracao(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setReintegracao(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            reintegracao.put("Reintegracao", situacao);
        }

        return ResponseEntity.ok().body(reintegracao);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoReintegracaoRepository.findSituacao("documentoReintegracao", "idReintegracao", id, "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I - Seção V", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II - Seção V", "Ato da concessão acompanhado da respectiva publicação",
                "Ato da concessão acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("III - Seção V", "Cópia autêntica da decisão judicial",
                "Cópia autêntica da decisão judicial, se dela decorrer a motivação, acompanhada da respectiva certidão de trânsito em julgado", "", "Não"));
        list.add(new Inciso("IV - Seção V", "Justificativa para a reintegração que se der em razão de processo administrativo",
                "Justificativa para a reintegração que se der em razão de processo administrativo", "", "Não"));
        list.add(new Inciso("V - Seção V", "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração",
                "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração", "", "Sim"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoReintegracaoRepository.findAllInciso("documentoReintegracao","idReintegracao",id, list.get(i).getInciso());
            if (existeArquivo > 0){
                list.get(i).setStatus("Informado");
            }else{
                list.get(i).setStatus("Não informado");
            }
        }
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReintegracao list = documentoReintegracaoRepository.buscarDocumentoReintegracao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        documentoReintegracaoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id,@RequestParam(value = "Ug", required = false) String ug) {
        AdmEnvio admEnvio = preencherEnvio(id,ug);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id) {
        return preencherEnvio(id,null);
    }
    private AdmEnvio preencherEnvio(BigInteger id,String ug) {
        Reintegracao reintegracao = reintegracaoRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.REINTEGRACAO.getValor());
        admEnvio.setUnidadeGestora(reintegracao.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        if(ug != null && !ug.equals("")){
            admEnvio.setOrgaoOrigem(ug);
        }
        admEnvio.setIdMovimentacao(id);
        admEnvio.setComplemento("Conforme PORTARIA: " + reintegracao.getAto().getNumeroAto() + " De: " + reintegracao.getAto().getDataPublicacao());
        admEnvio.setAdmissao(reintegracao.getAdmissao());
        return admEnvio;
    }
}
