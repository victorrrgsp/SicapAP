package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.PensaoDTO;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.DocumentoPensaoRepository;
import com.example.sicapweb.repository.concessao.PensaoRepository;
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
@RequestMapping("/documentoConcessaoRevisaoPensao")
public class ConcessaoRevisaoPensaoController extends DefaultController<DocumentoPensao> {

    @Autowired
    private PensaoRepository pensaoRepository;

    @Autowired
    private DocumentoPensaoRepository documentoPensaoRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> pensao = new HashMap<String, Object>();

    public class PensaoDocumento {
        private Pensao pensao;

        private String situacao;

        public Pensao getPensao() {
            return pensao;
        }

        public void setPensao(Pensao pensao) {
            this.pensao = pensao;
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
    public ResponseEntity<PaginacaoUtil<PensaoDTO>> listRevisaoPensao(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<PensaoDTO> paginacaoUtil = pensaoRepository.buscaPaginadaPensaoRevisao(pageable, searchParams, tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Pensao list = pensaoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id, @RequestParam(value = "descricao", required = false) String descricao) throws UnknownHostException {
        DocumentoPensao documentoPensao = new DocumentoPensao();
        documentoPensao.setPensao(pensaoRepository.findById(id));
        documentoPensao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "PensaoRevisao");
        documentoPensao.setIdCastorFile(idCastor);
        documentoPensao.setStatus(DocumentoPensao.Status.Informado.getValor());
        documentoPensao.setRevisao("S");
        documentoPensao.setDescricao(descricao);
        documentoPensao.setIdCargo(User.getUser(pensaoRepository.getRequest()).getCargo().getValor());
        documentoPensao.setCpfUsuario(User.getUser(pensaoRepository.getRequest()).getCpf());
        documentoPensao.setIpUsuario(InetAddress.getLocalHost().getHostAddress());
        documentoPensao.setDataUpload(new Date());
        documentoPensaoRepository.save(documentoPensao);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Pensao> list = pensaoRepository.buscarPensaoRevisao();
        PensaoDocumento situacao = new PensaoDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoPensaoRepository.findSituacao("documentoPensao", "idPensao", list.get(i).getId(), "'I', 'II', 'III', 'IV', 'V', 'VI'");
            if (quantidadeDocumentos == 0) {
                situacao.setPensao(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 6) {
                situacao.setPensao(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setPensao(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            pensao.put("Pensao", situacao);
        }

        return ResponseEntity.ok().body(pensao);
    }


    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        Integer situacao = documentoPensaoRepository.findSituacao("documentoPensao", "idPensao", id, "'I', 'II', 'III', 'IV', 'V', 'VI'");
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício da autoridade competente",
                "Ofício da autoridade competente", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de aposentadoria",
                "Requerimento de aposentadoria", "", "Sim"));
        list.add(new Inciso("III", "Certidão comprobatória de preenchimento de requisitos",
                "Certidão comprobatória de preenchimento de requisitos para a percepção dos proventos e/ou espécies remuneratórias previstos na revisão pretendida", "", "Sim"));
        list.add(new Inciso("IV", "Demonstrativo de cálculo da revisão dos proventos",
                "Demonstrativo de cálculo da revisão dos proventos", "", "Sim"));
        list.add(new Inciso("V", "Parecer jurídico atestando a legalidade da concessão do benefício",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
        list.add(new Inciso("VI", "Ato de concessão da revisão de proventos",
                "Ato de concessão da revisão de proventos constando o documento revisado, o nome do servidor e a fundamentação legal, acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++) {
            Integer existeArquivo = documentoPensaoRepository.findAllInciso("documentoPensao", "idPensao", id, list.get(i).getInciso());
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
        return ResponseEntity.ok().body(documentoPensaoRepository.buscarDocumentoPensaoRevisao(inciso, id));
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        documentoPensaoRepository.delete(id); 
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id,@RequestParam(value = "Ug", required = false) String ug) {
        AdmEnvio admEnvio = preencherEnvio(id,ug);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id, String ug) {
        Pensao pensao = pensaoRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.REVISAOPENSAO.getValor());
        admEnvio.setUnidadeGestora(pensao.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        if (ug != null && !ug.equals(""))
            admEnvio.setOrgaoOrigem(ug);
        else
            admEnvio.setOrgaoOrigem(pensao.getCnpjUnidadeGestoraOrigem());
        admEnvio.setIdMovimentacao(id);
        admEnvio.setAdmissao(pensao.getAdmissao());
        admEnvio.setNumeroAto(pensao.getAto().getNumeroAto());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = df.format(pensao.getAto().getDataPublicacao());
        admEnvio.setComplemento("Conforme PORTARIA De: " + dataFormatada);
        return admEnvio;
    }
}
