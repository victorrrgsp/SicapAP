package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoAproveitamento;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aproveitamento;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.AproveitamentoDTO;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.AproveitamentoRepository;
import com.example.sicapweb.repository.concessao.DocumentoAproveitamentoRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAproveitamento")
public class ConcessaoAproveitamentoController extends DefaultController<DocumentoAproveitamento> {

    @Autowired
    private AproveitamentoRepository aproveitamentoRepository;

    @Autowired
    private DocumentoAproveitamentoRepository documentoAproveitamentoRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> aproveitamento = new HashMap<String, Object>();

    public class AproveitamentoDocumento {
        private Aproveitamento aproveitamento;

        private String situacao;

        public Aproveitamento getAproveitamento() {
            return aproveitamento;
        }

        public void setAproveitamento(Aproveitamento aproveitamento) {
            this.aproveitamento = aproveitamento;
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
    public ResponseEntity<PaginacaoUtil<AproveitamentoDTO>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<AproveitamentoDTO> paginacaoUtil = aproveitamentoRepository.buscaPaginadaAproveitamento(pageable, searchParams, tipoParams);
        paginacaoUtil.getRegistros().forEach(registro -> {
            if (registro.getStatus() == 1 && getProcessoAmbiguo(registro.getId()).getBody() != null)
                registro.setStatus(0);
        });
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(aproveitamentoRepository.findById(id));
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id, @RequestParam(value = "descricao", required = false) String descricao) throws UnknownHostException {
        
        // Verificar o tipo de arquivo
        getFileType(file);

        DocumentoAproveitamento documentoAproveitamento = new DocumentoAproveitamento();
        documentoAproveitamento.setAproveitamento(aproveitamentoRepository.findById(id));
        documentoAproveitamento.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Aproveitamento");
        documentoAproveitamento.setIdCastorFile(idCastor);
        documentoAproveitamento.setStatus(DocumentoAproveitamento.Status.Informado.getValor());
        documentoAproveitamento.setDescricao(descricao);
        documentoAproveitamento.setIdCargo(User.getUser(aproveitamentoRepository.getRequest()).getCargo().getValor());
        documentoAproveitamento.setCpfUsuario(User.getUser(aproveitamentoRepository.getRequest()).getCpf());
        documentoAproveitamento.setIpUsuario(InetAddress.getLocalHost().getHostAddress());
        documentoAproveitamento.setDataUpload(new Date());
        documentoAproveitamentoRepository.save(documentoAproveitamento);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Aproveitamento> list = aproveitamentoRepository.findAll();
        AproveitamentoDocumento situacao = new AproveitamentoDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoAproveitamentoRepository.findSituacao("documentoAproveitamento", "idAproveitamento", list.get(i).getId(), "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'");
            if (quantidadeDocumentos == 0) {
                situacao.setAproveitamento(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 4) {
                situacao.setAproveitamento(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setAproveitamento(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            aproveitamento.put("Aproveitamento", situacao);
        }
        return ResponseEntity.ok().body(aproveitamento);
    }

    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(documentoAproveitamentoRepository.findSituacao("documentoAproveitamento", "idAproveitamento", id, "'I - Seção V', 'II - Seção V', 'V - Seção V', 'VI - Seção V'"));
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
        list.add(new Inciso("X - Seção V", "Cópia devidamente publicada da lei que o extinguiu",
                "Cópia devidamente publicada da lei que o extinguiu, no caso de aproveitamento decorrente de extinção de cargo", "", "Não"));
        list.add(new Inciso("XI - Seção V", "A comprovação do cumprimento dos requisitos exigidos para o desempenho das atividades",
                "A comprovação do cumprimento dos requisitos exigidos para o desempenho das atividades", "", "Não"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++) {
            Integer existeArquivo = documentoAproveitamentoRepository.findAllInciso("documentoAproveitamento", "idAproveitamento", id, list.get(i).getInciso());
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
        return ResponseEntity.ok().body(documentoAproveitamentoRepository.buscarDocumentoAproveitamento(inciso, id));
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        documentoAproveitamentoRepository.delete(id); 
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id, @RequestParam(value = "Ug", required = false) String ug) {
        admEnvioRepository.save(preencherEnvio(id, ug));
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id, String ug) {
        Aproveitamento aproveitamento = aproveitamentoRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.APROVEITAMENTO.getValor());
        admEnvio.setUnidadeGestora(aproveitamento.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        if (ug != null && !ug.equals(""))
            admEnvio.setOrgaoOrigem(ug);

        admEnvio.setIdMovimentacao(id);
        admEnvio.setAdmissao(aproveitamento.getAdmissao());
        admEnvio.setNumeroAto(aproveitamento.getAto().getNumeroAto());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = df.format(aproveitamento.getAto().getDataPublicacao());
        admEnvio.setComplemento("Conforme PORTARIA De: " + dataFormatada);
        return admEnvio;
    }

    @CrossOrigin
    @PostMapping("/vincularProcesso/{id}/{numero}/{ano}")
    public ResponseEntity<?> vincularProcesso(@PathVariable BigInteger id, @PathVariable String numero, @PathVariable String ano) {
        String processo = numero + "/" + ano;
        AdmEnvio admEnvio = preencherEnvio(id, null);
        admEnvio.setStatus(AdmEnvio.Status.CONCLUIDO.getValor());
        admEnvio.setProcesso(processo);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    @CrossOrigin
    @PostMapping("/buscarProcesso/{id}")
    public ResponseEntity<?> getProcessoAmbiguo(@PathVariable BigInteger id) {
        Aproveitamento aproveitamento = aproveitamentoRepository.findById(id);
        List<Object> processos = aproveitamentoRepository.getProcessoApEcontas("176",
                aproveitamento.getAdmissao().getServidor().getCpfServidor(),
                aproveitamento.getChave().getIdUnidadeGestora());
        return ResponseEntity.ok().body(processos);
    }
}
