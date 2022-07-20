package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.EditalAprovadoRepository;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping({"/concursoAprovado"})
public class EditalAprovadoController extends DefaultController<EditalAprovado> {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EditalAprovado>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EditalAprovado> paginacaoUtil = editalAprovadoRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        EditalAprovado list = editalAprovadoRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }


    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EditalAprovado> create(@RequestBody EditalAprovado editalAprovado) {
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
        editalAprovado.setEditalVaga(editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()));

        EditalAprovado mesmocpf = editalAprovadoRepository.buscarAprovadoPorCpf(editalAprovado.getCpf());
        EditalAprovado mesmoinscricao = editalAprovadoRepository.buscarAprovadoPorInscricao(editalAprovado.getNumeroInscricao());
        EditalAprovado mesmaclassifmesmavaga = editalAprovadoRepository.buscarAprovadoPorClassificacaoConc(editalAprovado.getEditalVaga().getId(),editalAprovado.getClassificacao());
      //  if  (!editalAprovado.getEditalVaga().getEdital().getNumeroEdital().equals(editalAprovado.getNumeroEdital()) ) throw new InvalitInsert("O edital do aprovado dever o mesmo da vaga!"); ;
        if (mesmocpf!=null) throw new InvalitInsert("Cpf ja Cadastrado!");
        else if (mesmoinscricao!=null){throw new InvalitInsert("Outro aprovado com o mesmo numero de inscrição!");}
            else if (mesmaclassifmesmavaga!=null){throw new InvalitInsert("Outro aprovado ja se encontra na mesma classificação e tipo de concorrencia para mesma vaga!");}
        editalAprovadoRepository.save(editalAprovado);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalAprovado.getId()).toUri();
        return ResponseEntity.created(uri).body(editalAprovado);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EditalAprovado> update(@RequestBody EditalAprovado editalAprovado, @PathVariable BigInteger id){
        editalAprovado.setId(id);
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
        editalAprovado.setEditalVaga(editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()));
        EditalAprovado mesmocpf = editalAprovadoRepository.buscarAprovadoPorCpf(editalAprovado.getCpf());
        EditalAprovado mesmoinscricao = editalAprovadoRepository.buscarAprovadoPorInscricao(editalAprovado.getNumeroInscricao());
        EditalAprovado mesmaclassifmesmavaga = editalAprovadoRepository.buscarAprovadoPorClassificacaoConc(editalAprovado.getEditalVaga().getId(),editalAprovado.getClassificacao());
        //  if  (!editalAprovado.getEditalVaga().getEdital().getNumeroEdital().equals(editalAprovado.getNumeroEdital()) ) throw new InvalitInsert("O edital do aprovado dever o mesmo da vaga!"); ;
        if (mesmocpf!=null ) {
            if (! id.equals(mesmocpf.getId())  ) throw new InvalitInsert("Cpf ja Cadastrado!");
        } else if (mesmoinscricao!=null){
            if (! id.equals(mesmocpf.getId())  ) throw new InvalitInsert("Outro aprovado com o mesmo numero de inscrição!");
        }
        else if (mesmaclassifmesmavaga!=null)
        {
            if (! id.equals(mesmocpf.getId())  ) throw new InvalitInsert("utro aprovado ja se encontra na mesma classificação e tipo de concorrencia para mesma vaga!");
        }


        editalAprovadoRepository.update(editalAprovado);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        editalAprovadoRepository.delete(id);
        return ResponseEntity.noContent().build();
    }
}
