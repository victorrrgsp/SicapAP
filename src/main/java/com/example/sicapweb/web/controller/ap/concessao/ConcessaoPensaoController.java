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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoPensao")
public class ConcessaoPensaoController extends DefaultController<DocumentoPensao> {

    @Autowired
    private PensaoRepository pensaoRepository;

    @Autowired
    private DocumentoPensaoRepository documentoPensaoRepository;

    @Autowired
    private AdmEnvioRepository admEnvioRepository;

    HashMap<String, Object> pensao = new HashMap<String, Object>();

    public class PensaoDocumento{
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
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<PensaoDTO>> listPensoes(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        return ResponseEntity.ok().body(pensaoRepository.buscaPaginadaPensao(pageable,searchParams,tipoParams));
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        return ResponseEntity.ok().body(pensaoRepository.findById(id));
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id, @RequestParam(value = "descricao", required = false) String descricao) throws UnknownHostException {
        DocumentoPensao documentoPensao = new DocumentoPensao();
        documentoPensao.setPensao(pensaoRepository.findById(id));
        documentoPensao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Pensao");
        documentoPensao.setIdCastorFile(idCastor);
        documentoPensao.setStatus(DocumentoPensao.Status.Informado.getValor());
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
        List<Pensao> list = pensaoRepository.buscarPensao();
        PensaoDocumento situacao = new PensaoDocumento();
        for(Integer i= 0; i < list.size(); i++){
            Integer quantidadeDocumentos = documentoPensaoRepository.findSituacao("documentoPensao","idPensao", list.get(i).getId(), "'I', 'II', 'III', 'IV', 'VIII', 'IX', 'X', 'XI', 'XIII', 'XIV'");
            if(quantidadeDocumentos == 0) {
                situacao.setPensao(list.get(i));
                situacao.setSituacao("Pendente");
            } else if(quantidadeDocumentos == 10){
                situacao.setPensao(list.get(i));
                situacao.setSituacao("Concluído");
            } else{
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
        return ResponseEntity.ok().body(documentoPensaoRepository.findSituacao("documentoPensao","idPensao",id, "'I', 'II', 'III', 'IV', 'VIII', 'IX', 'X', 'XI', 'XIII', 'XIV'"));
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de pensão",
                "Requerimento de pensão devidamente preenchido e assinado pelo beneficiário", "", "Sim"));
        list.add(new Inciso("III", "Certidão de óbito ou declaração judicial",
                "Certidão de óbito ou declaração judicial em caso de morte presumida", "", "Sim"));
        list.add(new Inciso("IV", "Certidão de casamento ou documento probatório de união estável",
                "Certidão de casamento ou documento de união estável", "", "Sim"));
        list.add(new Inciso("V", "Certidão de nascimento dos filhos ou dependentes legais",
                "Certidão de nascimento dos filhos ou dependentes legais", "", "Não"));
        list.add(new Inciso("VI", "Comprovação de dependência econômica do beneficiário",
                "Comprovação de dependência econômica do beneficiário, caso não se enquadre na dependência direta", "", "Não"));
        list.add(new Inciso("VII", "Comprovação de incapacidade física ou mental do beneficiário",
                "Comprovação de incapacidade física ou mental do beneficiário, acompanhada do termo de tutela ou curatela, se for o caso", "", "Não"));
        list.add(new Inciso("VIII", "Certidão do tempo de contribuição do servidor falecido",
                "Certidão do tempo de contribuição do servidor falecido, no caso de este se encontrar em atividade quando do falecimento", "", "Sim"));
        list.add(new Inciso("IX", "Cálculo dos proventos da pensão",
                "Cálculo dos proventos da pensão concedida nos termos do art. 40, § 2 º ou do § 7º, incisos I e II da Constituição Federal, com a indicação dos beneficiários e percentuais atribuídos a cada um deles", "", "Sim"));
        list.add(new Inciso("X", "Último contracheque",
                "Último contracheque recebido pelo servidor antes do falecimento", "", "Sim"));
        list.add(new Inciso("XI", "Ato concessório do benefício de pensão",
                "Ato concessório do benefício de pensão  constando o nome dos beneficiários com as respectivas proporcionalidades e temporariedades, nos termos da lei, o nome do servidor falecido e a devida fundamentação legal, acompanhado de sua publicação", "", "Sim"));
        list.add(new Inciso("XII", "Declaração do órgão competente",
                "Declaração do órgão competente, no caso de as circunstâncias do óbito decorrer de acidente em serviço, moléstia profissional ou doença grave, contagiosa ou incurável reconhecida em lei específica", "", "Não"));
        list.add(new Inciso("XIII", "Informação emitida pela entidade em que o servidor falecido mantém o vínculo previdenciário",
                "Informação emitida pela entidade em que o servidor falecido mantém o vínculo previdenciário, constando o demonstrativo de apuração do tempo de contribuição e de cálculo do benefício", "", "Sim"));
        list.add(new Inciso("XIV", "Parecer jurídico",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
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
        return ResponseEntity.ok().body(documentoPensaoRepository.buscarDocumentoPensao(inciso, id) );
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        documentoPensaoRepository.delete(id); 
    }

    @CrossOrigin
    @PostMapping("/enviarGestor/{id}")
    public ResponseEntity<?> enviarGestorAssinar(@PathVariable BigInteger id) {
        admEnvioRepository.save(preencherEnvio(id));
        return ResponseEntity.ok().body("Ok");
    }

    private AdmEnvio preencherEnvio(BigInteger id) {
        Pensao pensao = pensaoRepository.findById(id);
        AdmEnvio admEnvio = new AdmEnvio();
        admEnvio.setTipoRegistro(AdmEnvio.TipoRegistro.PENSAO.getValor());
        admEnvio.setUnidadeGestora(pensao.getChave().getIdUnidadeGestora());
        admEnvio.setStatus(AdmEnvio.Status.AGUARDANDOASSINATURA.getValor());
        admEnvio.setOrgaoOrigem(pensao.getCnpjUnidadeGestoraOrigem());
        admEnvio.setIdMovimentacao(id);
        admEnvio.setComplemento("Conforme PORTARIA: " + pensao.getAto().getNumeroAto() + " De: " + pensao.getAto().getDataPublicacao());
        admEnvio.setAdmissao(pensao.getAdmissao());
        return admEnvio;
    }
}