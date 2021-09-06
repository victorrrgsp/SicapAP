package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoAposentadoria;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.AposentadoriaRepository;
import com.example.sicapweb.repository.DocumentoAposentadoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoAposentadoria")
public class ConcessaoAposentadoriaController extends DefaultController<Aposentadoria> {

    @Autowired
    private AposentadoriaRepository aposentadoriaRepository;

    @Autowired
    private DocumentoAposentadoriaRepository documentoAposentadoriaRepository;

    @CrossOrigin
    @GetMapping
    @Override
    public ResponseEntity<List<Aposentadoria>> findAll() {
        List<Aposentadoria> list = aposentadoriaRepository.buscarAposentadorias();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoAposentadoria documentoAposentadoria = new DocumentoAposentadoria();
        documentoAposentadoria.setAposentadoria(aposentadoriaRepository.findById(id));
        documentoAposentadoria.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Aposentadoria");
        documentoAposentadoria.setIdCastorFile(idCastor);
        documentoAposentadoria.setStatus(DocumentoAposentadoria.Status.Informado.getValor());
        documentoAposentadoriaRepository.save(documentoAposentadoria);
        return ResponseEntity.ok().body(idCastor);
    }


    @CrossOrigin
    @GetMapping(path = {"getSituacao/{id}"})
    public ResponseEntity<?> findSituacao(@PathVariable BigInteger id) {
        String situacao = documentoAposentadoriaRepository.findSituacao("documentoAposentadoria",12,"idAposentadoria",id);
        return ResponseEntity.ok().body(situacao);
    }

    @CrossOrigin
    @GetMapping(path = {"getInciso/{id}"})
    public ResponseEntity<?> findInciso(@PathVariable BigInteger id) {
        List<Inciso> list = new ArrayList<>();
        list.add(new Inciso("I", "Ofício subscrito pela autoridade competente",
                "Ofício subscrito pela autoridade competente dirigido ao Presidente do TCE/TO dando ciência do fato", "", "Sim"));
        list.add(new Inciso("II", "Requerimento de aposentadoria",
                "Requerimento de aposentadoria indicando a permanência do servidor na atividade até a publicação do ato ou da data do afastamento preliminar", "", "Sim"));
        list.add(new Inciso("III", "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas - CPF/MF",
                "Documento de identidade e de inscrição no Cadastro de Pessoas Físicas - CPF/MF", "", "Sim"));
        list.add(new Inciso("IV", "Ato de concessão do benefício",
                "Ato de concessão do benefício firmado na forma da lei de refência, acompanhado da respectiva publicação, constando o nome do servidor, o cargo até então ocupado, o valor dos proventos e da fundamentação legal para a concessão, bem como a data a partir da qual o servidor será considerado aposentado", "", "Sim"));
        list.add(new Inciso("V", "Certidão de tempo de contribuição",
                "Certidão de tempo de contribuição expedida pelo Regime Geral de Previdência Social (RGPS) ou pelo Regime Próprio de Previdência Social (RPPS), conforme o caso, detalhando o vínculo previdenciário do beneficiário antes do ingresso no cargo em que se der a aposentadoria.", "", "Sim"));
        list.add(new Inciso("VI", "Último contracheque do servidor",
                "Último contracheque do servidor", "", "Sim"));
        list.add(new Inciso("VII", "Demonstrativo dos cálculos de proventos",
                "Demonstrativo dos cálculos de proventos com base na remuneração do cargo efetivo, discriminando as verbas percebidas, inclusive as vantagens de caráter pessoal com fundamento legal para a incorporação, quando for o caso, informando o total mensal e especificando se os proventos são integrais ou proporcionais, devendo, neste último caso, informar a proporcionalidade adotada", "", "Sim"));
        list.add(new Inciso("VIII", "Declaração ou histórico funcional",
                "Declaração ou histórico funcional discriminando o tempo de efetivo exercício no serviço público, o tempo de exercício na carreira e no cargo efetivo em que se deu a aposentadoria, nos casos daquelas concedidas com base no disposto nos arts. 2º e 3º da EC nº 41/2003, arts. 2º e 3º da EC nº 47/2005, ou nas hipóteses de aposentadorias embasadas na EC nº 20/1998", "", "Sim"));
        list.add(new Inciso("IX", "Laudo pericial atestando a incapacidade definitiva do servidor",
                "Laudo pericial atestando a incapacidade definitiva do servidor, com a indicação da moléstia que o tornou inabilitado para a vida laboral, nos casos de aposentadoria por invalidez", "", "Não"));
        list.add(new Inciso("X", "Declaração firmada pelo servidor de não acúmulo de proventos de aposentadoria",
                "Declaração firmada pelo servidor de não acúmulo de proventos de aposentadoria por parte de qualquer ente público da Federação, ressalvados os cargos, empregos e funções públicas acumuláveis por permissivos constitucionais", "", "Não"));
        list.add(new Inciso("XI", "Termo de opção",
                "Termo de opção em sendo o caso de acúmulo de cargo, na conformidade das exigências legais", "", "Não"));
        list.add(new Inciso("XII", "Informação emitida pelo instituto previdenciário",
                "Informação emitida pelo instituto previdenciário ao qual o beneficiário esteja vinculado constando o demonstrativo de apuração do tempo de contribuição e de cálculo do benefício", "", "Sim"));
        list.add(new Inciso("XIII", "Parecer jurídico atestando a legalidade da concessão do benefício",
                "Parecer jurídico atestando a legalidade da concessão do benefício", "", "Sim"));
        list.add(new Inciso("", "Outros",
                "Outros", "", "Não"));

        for (int i = 0; i < list.size(); i++){
            Integer existeArquivo = documentoAposentadoriaRepository.findAllInciso("documentoAposentadoria","idAposentadoria",id, list.get(i).getInciso());
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
        DocumentoAposentadoria list = documentoAposentadoriaRepository.buscarDocumentoAposentadoria(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
