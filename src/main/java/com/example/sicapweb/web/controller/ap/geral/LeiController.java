package com.example.sicapweb.web.controller.ap.geral;

import br.gov.to.tce.model.CastorFile;
import br.gov.to.tce.model.ap.relacional.Lei;

import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.geral.AtoRepository;
import com.example.sicapweb.repository.geral.LeiRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import com.example.sicapweb.web.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lei")
public class LeiController extends DefaultController<Lei> {
    @Autowired
    LeiController(LeiRepository leiRepository,AtoRepository atoRepository){
        this.leiRepository = leiRepository;
        this.atoRepository = atoRepository;
    }

    private LeiRepository leiRepository; 

    private AtoRepository atoRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<Lei>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<Lei> paginacaoUtil = leiRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Lei>> findAll() {
        return ResponseEntity.ok().body(leiRepository.findAll());
    }


    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        Lei list = leiRepository.findById(id);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @GetMapping(path = {"/findallLei/{ug}"})
    public ResponseEntity<?> findallLei(@PathVariable String ug) {
        List<Lei> list = leiRepository.findAllLei(ug);
        return ResponseEntity.ok().body(list);
    }
    @CrossOrigin
    @PostMapping
    public ResponseEntity<Lei> create(@RequestBody Lei lei) {
        try {
            
            lei.setChave(leiRepository.buscarPrimeiraRemessa());
            
            leiRepository.save(lei);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lei.getId()).toUri();
            return ResponseEntity.created(uri).body(lei);
        }  catch (Exception e) {
            throw new InvalitInsert("Erro na insersao de dados, por favor cheque os canpos enviados ");
            //TODO: handle exception
        }
    }
        
    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<Lei> update(@RequestBody Lei lei, @PathVariable BigInteger id) {
        try {
            
            lei.setChave(atoRepository.buscarPrimeiraRemessa());
            lei.setAto(atoRepository.findById(lei.getAto().getId()));
            lei.setId(id);
            
            leiRepository.update(lei);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new InvalitInsert("Erro na insersao de dados, por favor cheque os campos enviados ");
            //TODO: handle exception
        }
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        leiRepository.delete(id);
        return ResponseEntity.noContent().build();
    }


    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable BigInteger id) {
        Lei lei = new Lei();
        lei = leiRepository.findById(id);
        CastorFile castorFile = super.getCastorFile(file, "Lei");
        lei.setCastorFile(castorFile);
        leiRepository.update(lei);
        return ResponseEntity.ok().body(castorFile.getId());
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable BigInteger id) {
        Lei list = leiRepository.buscarDocumentoLei(id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
