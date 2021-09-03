package com.example.sicapweb.web.controller;

import br.gov.to.tce.model.ap.concessoes.DocumentoPensao;
import br.gov.to.tce.model.ap.pessoal.Aposentadoria;
import br.gov.to.tce.model.ap.pessoal.Pensao;
import com.example.sicapweb.model.Inciso;
import com.example.sicapweb.repository.DocumentoPensaoRepository;
import com.example.sicapweb.repository.PensaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/documentoConcessaoPensao")
public class ConcessaoPensaoController extends DefaultController<Pensao>{

    @Autowired
    private PensaoRepository pensaoRepository;

    @Autowired
    private DocumentoPensaoRepository documentoPensaoRepository;

    @CrossOrigin
    @GetMapping()
    @Override
    public ResponseEntity<List<Pensao>> findAll() {
        List<Pensao> list = pensaoRepository.buscarPensao();
        return ResponseEntity.ok().body(list);
    }

//    @CrossOrigin
//    @GetMapping(path = {"/{id}"})
//    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
//        Pensao list = pensaoRepository.findById(id);
//        return ResponseEntity.ok().body(list);
//    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{inciso}/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable String inciso, @PathVariable BigInteger id) {
        DocumentoPensao documentoPensao = new DocumentoPensao();
        documentoPensao.setPensao(pensaoRepository.findById(id));
        documentoPensao.setInciso(inciso);
        String idCastor = super.setCastorFile(file, "Pensao");
        documentoPensao.setIdCastorFile(idCastor);
        documentoPensao.setStatus(DocumentoPensao.Status.Informado.getValor());
        documentoPensaoRepository.save(documentoPensao);
        return ResponseEntity.ok().body(idCastor);
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
        list.add(new Inciso("IV", "Certidão de casamento ou documento de união estável",
                "Certidão de casamento ou documento de união estável", "", "Sim"));
        list.add(new Inciso("V", "Certidão de nascimento dos filhos ou dependentes legais",
                "Certidão de nascimento dos filhos ou dependentes legais", "", "Não"));
        list.add(new Inciso("VI", "Comprovação de dependência econômica do beneficiário",
                "Comprovação de dependência econômica do beneficiário, caso não se enquadre na dependência direta", "", "Não"));
        list.add(new Inciso("VII", "Certidão de tempo de contribuição do servidor falecido",
                "Certidão de tempo de contribuição do servidor falecido, no caso de este se encontrar em atividade quando do falecimento", "", "Não"));
        list.add(new Inciso("VIII", "Cálculo dos proventos da pensão",
                "Cálculo dos proventos da pensão concedida nos termos do art. 40, § 2 º ou do § 7º, incisos I e II da Constituição Federal, com a indicação dos beneficiários e percentuais atribuídos a cada um deles", "", "Sim"));
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
        list.add(new Inciso("", "Outros",
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
        DocumentoPensao list = documentoPensaoRepository.buscarDocumentoPensao(inciso, id).get(0);
        return ResponseEntity.ok().body(list);
    }
}