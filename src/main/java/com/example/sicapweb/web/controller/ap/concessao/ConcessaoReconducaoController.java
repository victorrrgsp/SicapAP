package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoReconducao;
import br.gov.to.tce.model.ap.pessoal.Reconducao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.ReconducaoDTO;
import com.example.sicapweb.repository.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.DocumentoReconducaoRepository;
import com.example.sicapweb.repository.concessao.ReconducaoRepository;
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
@RequestMapping("/documentoConcessaoReconducao")
public class ConcessaoReconducaoController extends DefaultController<DocumentoReconducao> {

    @Autowired
    private ReconducaoRepository reconducaoRepository;

    @Autowired
    private DocumentoReconducaoRepository documentoReconducaoRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> reconducao = new HashMap<String, Object>();

    public class ReconducaoDocumento {
        private Reconducao reconducao;

        private String situacao;

        public Reconducao getReconducao() {
            return reconducao;
        }

        public void setReconducao(Reconducao reintegracao) {
            this.reconducao = reconducao;
        }

        public String getSituacao() {
            return situacao;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }
    }

    @CrossOrigin
    @GetMapping(path = "/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<ReconducaoDTO>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<ReconducaoDTO> paginacaoUtil = reconducaoRepository.buscaPaginadaReconducao(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Reconducao list = reconducaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReconducao documentoReconducao = new DocumentoReconducao();
        documentoReconducao.setReconducao(reconducaoRepository.findById(id));
        documentoReconducao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Reconducao");
        documentoReconducao.setIdCastorFile(idCastor);
        documentoReconducao.setStatus(DocumentoReconducao.Status.Informado.getValor());
        documentoReconducaoRepository.save(documentoReconducao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Reconducao> list = reconducaoRepository.buscarReconducao();
        ReconducaoDocumento situacao = new ReconducaoDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoReconducaoRepository.findSituacao("documentoReconducao", "idReconducao", list.get(i).getId(), "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'");
            if (quantidadeDocumentos == 0) {
                situacao.setReconducao(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 4) {
                situacao.setReconducao(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setReconducao(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            reconducao.put("Reconducao", situacao);
        }
        return ResponseEntity.ok().body(reconducao);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoReconducaoRepository.findSituacao("documentoReconducao", "idReconducao", id, "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'");
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
        list.add(new Inciso("V - Seção V", "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração",
                "Declaração do órgão competente da existência de vaga no cargo em que se der a reintegração", "", "Sim"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("IX - Seção V", "Cópia dos atos que declararam os resultados da avaliação de desempenho",
                "Cópia dos atos que declararam os resultados da avaliação de desempenho, nos casos de recondução por inabilitação em estágio probatório", "", "Não"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++) {
            Integer existeArquivo = documentoReconducaoRepository.findAllInciso("documentoReconducao", "idReconducao", id, list.get(i).getInciso());
            if (existeArquivo > 0) {
                list.get(i).setStatus("Informado");
            } else {
                list.get(i).setStatus("Não informado");
            }
        }
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{inciso}/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoReconducao list = documentoReconducaoRepository.buscarDocumentooReconducao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        documentoReconducaoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id) {
        AdmEnvio admEnvio = preencherEnvio(id);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id) {
        Reconducao reconducao = reconducaoRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.RECONDUCAO.getValor());
        admEnvio.setUnidadeGestora(reconducao.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
//        admEnvio.setOrgaoOrigem(reconducao.getCnpjUnidadeGestoraOrigem());
        admEnvio.setIdMovimentacao(id);
        admEnvio.setComplemento("Conforme PORTARIA: " + reconducao.getAto().getNumeroAto() + " De: " + reconducao.getAto().getDataPublicacao());
        admEnvio.setAdmissao(reconducao.getAdmissao());
        return admEnvio;
    }
}
