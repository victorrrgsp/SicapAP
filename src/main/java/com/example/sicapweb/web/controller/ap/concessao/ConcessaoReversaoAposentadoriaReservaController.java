package com.example.sicapweb.web.controller.ap.concessao;

import br.gov.to.tce.model.adm.AdmEnvio;
import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.util.Date;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.model.dto.AposentadoriaDTO;
import com.example.sicapweb.repository.concessao.AdmEnvioRepository;
import com.example.sicapweb.repository.concessao.AposentadoriaRepository;
import com.example.sicapweb.repository.concessao.DocumentoAposentadoriaRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoReversaoAposentadoriaReserva")
public class ConcessaoReversaoAposentadoriaReservaController extends DefaultController<DocumentoAposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> aposentadoria = new HashMap<String, Object>();

    public class AposentadoriaDocumento {
        private Aposentadoria aposentadoria;

        private String situacao;

        public Aposentadoria getAposentadoria() {
            return aposentadoria;
        }

        public void setAposentadoria(Aposentadoria aposentadoria) {
            this.aposentadoria = aposentadoria;
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
    public ResponseEntity<PaginacaoUtil<AposentadoriaDTO>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        return ResponseEntity.ok().body(aposentadoriaRepository.buscaPaginadaReversaoAposentadoriaReserva(pageable, searchParams, tipoParams));
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(aposentadoriaRepository.findById(id));
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id, @RequestParam(value = "descricao", required = false) String descricao) throws UnknownHostException {
        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "AposentadoriaReversao");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoria.setReversao("S");
        documentoAposentadoria.setDescricao(descricao);
        documentoAposentadoria.setIdCargo(User.getUser(aposentadoriaRepository.getRequest()).getCargo().getValor());
        documentoAposentadoria.setCpfUsuario(User.getUser(aposentadoriaRepository.getRequest()).getCpf());
        documentoAposentadoria.setIpUsuario(InetAddress.getLocalHost().getHostAddress());
        documentoAposentadoria.setDataUpload(new Date());
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }

    @CrossOrigin
    @GetMapping(path = {"getDocumentos"})
    public ResponseEntity<?> findAllDocumentos() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarReversaoAposentadoriaReserva();
        AposentadoriaDocumento situacao = new AposentadoriaDocumento();
        for (Integer i = 0; i < list.size(); i++) {
            Integer quantidadeDocumentos = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria", "idAposentadoria", list.get(i).getId(), "'I - Seção V', 'II - Seção V', 'VI - Seção V', 'IV', 'VII - Seção V', 'VIII - Seção V'", "N", "N", "S", "N");
            if (quantidadeDocumentos == 0) {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Pendente");
            } else if (quantidadeDocumentos == 6) {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Concluído");
            } else {
                situacao.setAposentadoria(list.get(i));
                situacao.setSituacao("Aguardando verificação");
            }
            aposentadoria.put("Aposentadoria", situacao);
        }

        return ResponseEntity.ok().body(aposentadoria);
    }


    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(documentoAposentadoriaRepository.findSituacao("documentoAposentadoria", "idAposentadoria", id, "'I - Seção V', 'II - Seção V', 'VI - Seção V', 'IV', 'VII - Seção V', 'VIII - Seção V'", "N", "N", "S", "N"));
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I - Seção V", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II - Seção V", "Ato da concessão acompanhado da respectiva publicação",
                "Ato da concessão acompanhado da respectiva publicação", "", "Sim"));
        list.add(new Inciso("VI - Seção V", "Parecer jurídico atestando a legalidade do ato",
                "Parecer jurídico atestando a legalidade do ato", "", "Sim"));
        list.add(new Inciso("IV", "Último contracheque",
                "Último contracheque", "", "Sim"));
        list.add(new Inciso("VII - Seção V", "Laudo médico pericial",
                "Laudo médico pericial declarando a habilitação do aposentado para o exercício do cargo anteriormente ocupado", "", "Sim"));
        list.add(new Inciso("VIII - Seção V", "Documento de comprovação de idade do servidor",
                "Documento de comprovação de idade do servidor", "", "Sim"));
        list.add(new Inciso("Outros", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++) {
            Integer existeArquivo = documentoAposentadoriaRepository.findAllInciso("documentoAposentadoria", "idAposentadoria", id, list.get(i).getInciso());
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
        return ResponseEntity.ok().body(documentoAposentadoriaRepository.buscarDocumentoAposentadoriaReversao(inciso, id));
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        documentoAposentadoriaRepository.delete(id); 
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id,@RequestParam(value = "Ug", required = false) String ug) {
        AdmEnvio admEnvio = preencherEnvio(id,ug);
        admEnvioRepository.save(admEnvio);
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id, String ug) {
        Aposentadoria aposentadoria = aposentadoriaRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.REVERSAO.getValor());
        admEnvio.setUnidadeGestora(aposentadoria.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        if (ug != null && !ug.equals(""))
            admEnvio.setOrgaoOrigem(ug);
        admEnvio.setIdMovimentacao(id);
        admEnvio.setComplemento("Conforme PORTARIA: " + aposentadoria.getAto().getNumeroAto() + " De: " + aposentadoria.getAto().getDataPublicacao());
        admEnvio.setAdmissao(aposentadoria.getAdmissao());
        return admEnvio;
    }
}
