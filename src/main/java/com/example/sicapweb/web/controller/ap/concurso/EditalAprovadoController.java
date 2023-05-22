package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.concurso.DocumentoAdmissaoRepository;
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
import java.security.InvalidParameterException;
import java.util.List;

@RestController
@RequestMapping({"/concursoAprovado"})
public class EditalAprovadoController extends DefaultController<EditalAprovado> {

    @Autowired
    private EditalAprovadoRepository editalAprovadoRepository;

    @Autowired
    private EditalVagaRepository editalVagaRepository;

    @Autowired
    private DocumentoAdmissaoRepository documentoAdmissaoRepository;


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
        if (editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()) ==null)
            throw  new InvalidParameterException("Não encontrou vaga com esse codigo");
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
    public void update(@RequestBody EditalAprovado editalAprovado, @PathVariable BigInteger id){
        editalAprovado.setId(id);
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());
        editalAprovado.setEditalVaga(editalVagaRepository.buscarVagasPorCodigo(editalAprovado.getCodigoVaga()));

        EditalAprovado versaoAnterior  = editalAprovadoRepository.findById(id);
        if(
            !(
            versaoAnterior.getCpf().equals(editalAprovado.getCpf())&&
            versaoAnterior.getNumeroInscricao().equals(editalAprovado.getNumeroInscricao())&&
            versaoAnterior.getClassificacao().equals(editalAprovado.getClassificacao())&&
            versaoAnterior.getEditalVaga().getEdital().getNumeroEdital().equals(editalAprovado.getNumeroEdital())&&
            versaoAnterior.getEditalVaga().getCodigoVaga().equals(editalAprovado.getEditalVaga().getCodigoVaga())
            )
        ){

            EditalAprovado mesmocpf = editalAprovadoRepository.buscarAprovadoPorCpf(editalAprovado.getCpf());
            EditalAprovado mesmoinscricao = editalAprovadoRepository.buscarAprovadoPorInscricao(editalAprovado.getNumeroInscricao());
            EditalAprovado mesmaclassifmesmavaga = editalAprovadoRepository.buscarAprovadoPorClassificacaoConc(editalAprovado.getEditalVaga().getId(),editalAprovado.getClassificacao());
            //  if  (!editalAprovado.getEditalVaga().getEdital().getNumeroEdital().equals(editalAprovado.getNumeroEdital()) ) throw new InvalitInsert("O edital do aprovado dever o mesmo da vaga!"); ;
            if (mesmocpf!=null ) {
                if (!id.equals(mesmocpf.getId())) throw new InvalitInsert("Cpf ja Cadastrado!");
            } else if (mesmoinscricao!=null){
                if (! id.equals(mesmoinscricao.getId())  ) throw new InvalitInsert("Outro aprovado com o mesmo numero de inscrição!");
            }
            else if (mesmaclassifmesmavaga!=null)
            {
                if (! id.equals(mesmaclassifmesmavaga.getId())  ) throw new InvalitInsert("outro aprovado ja se encontra na mesma classificação para mesma vaga!");
            }
            
        }
        editalAprovadoRepository.update(editalAprovado);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        if (documentoAdmissaoRepository.getDocumenttosAdmissaoByIdAprovado(id).size()>0) throw new RuntimeException("Aprovado Vinculado a um documento. Excluia o documento antes de Excluir o aprovado!");
        editalAprovadoRepository.delete(id);
    }
}
