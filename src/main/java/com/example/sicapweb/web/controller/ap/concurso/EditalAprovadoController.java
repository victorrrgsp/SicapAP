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
        associateVagaToEditalAprovado(editalAprovado);

        EditalAprovado mesmocpf = editalAprovadoRepository.buscarAprovadoPorCpf(editalAprovado.getCpf());
        EditalAprovado mesmoinscricao = editalAprovadoRepository.buscarAprovadoPorInscricao(editalAprovado.getNumeroInscricao());
        EditalAprovado mesmaclassifmesmavaga = editalAprovadoRepository.buscarAprovadoPorClassificacaoConc(editalAprovado.getEditalVaga().getId(),editalAprovado.getClassificacao());
      //  if  (!editalAprovado.getEditalVaga().getEdital().getNumeroEdital().equals(editalAprovado.getNumeroEdital()) ) throw new InvalitInsert("O edital do aprovado dever o mesmo da vaga!"); ;
        if (mesmocpf!=null && mesmocpf.getEditalVaga().getCodigoVaga() == editalAprovado.getEditalVaga().getCodigoVaga()) throw new InvalitInsert("Cpf ja Cadastrado!");
        else if (mesmoinscricao!=null){
            String numeroAto = editalAprovado.getEditalVaga().getEdital().getNumeroEdital();
            if (Integer.parseInt(numeroAto.substring(numeroAto.length()-4)) > 2016 ){
                throw new InvalitInsert("Outro aprovado com o mesmo numero de inscrição!");
            }
        }
        else if (mesmaclassifmesmavaga!=null){throw new InvalitInsert("Outro aprovado ja se encontra na mesma classificação e tipo de concorrencia para mesma vaga!");}
        editalAprovado.setCodigoVaga(editalAprovado.getEditalVaga().getCodigoVaga());
        editalAprovadoRepository.save(editalAprovado);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(editalAprovado.getId()).toUri();
        return ResponseEntity.created(uri).body(editalAprovado);
    }

    private void associateVagaToEditalAprovado(EditalAprovado editalAprovado) {
        //String codigoVaga = editalAprovado.getCodigoVaga();
        //String numeroEdital = editalAprovado.getNumeroEdital();
        //EditalVaga vagaPorCodigoEEdital = editalVagaRepository.buscarVagasPorCodigoEEdital(codigoVaga,numeroEdital);
        EditalVaga vagaPorIdEditalVaga = editalVagaRepository.findById(editalAprovado.getEditalVaga().getId());
        if (vagaPorIdEditalVaga == null)
            throw  new InvalidParameterException("Não encontrou vaga especificada");
        editalAprovado.setEditalVaga(vagaPorIdEditalVaga);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public EditalAprovado update(@RequestBody EditalAprovado editalAprovado, @PathVariable BigInteger id){
        associateVagaToEditalAprovado(editalAprovado);
        editalAprovado.setId(id);
        editalAprovado.setChave(editalAprovadoRepository.buscarPrimeiraRemessa());

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
            } 
            if (mesmoinscricao!=null){
                if (! id.equals(mesmoinscricao.getId())  ) throw new InvalitInsert("Outro aprovado com o mesmo numero de inscrição!");
            }
            if (mesmaclassifmesmavaga!=null)
            {
                if (! id.equals(mesmaclassifmesmavaga.getId())  ) throw new InvalitInsert("outro aprovado ja se encontra na mesma classificação para mesma vaga!");
            }
        }
        editalAprovadoRepository.update(editalAprovado);
        return editalAprovadoRepository.findById(editalAprovado.getId());
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        if (documentoAdmissaoRepository.getDocumenttosAdmissaoByIdAprovado(id).size()>0) throw new RuntimeException("Aprovado Vinculado a um documento. Excluia o documento antes de Excluir o aprovado!");
        editalAprovadoRepository.delete(id);
    }

}
