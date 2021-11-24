package com.example.sicapweb.web.controller.ap.concurso;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.concurso.EmpresaOrganizadoraRepository;
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

@RestController
@RequestMapping({"/concursoEmpresaOrganizadora"})
public class EmpresaOrganizadoraController extends DefaultController<EmpresaOrganizadora> {

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @CrossOrigin
    @GetMapping(path="/{searchParams}/{tipoParams}/pagination")
    public ResponseEntity<PaginacaoUtil<EmpresaOrganizadora>> listChaves(Pageable pageable, @PathVariable String searchParams, @PathVariable Integer tipoParams) {
        PaginacaoUtil<EmpresaOrganizadora> paginacaoUtil = empresaOrganizadoraRepository.buscaPaginada(pageable,searchParams,tipoParams);
        return ResponseEntity.ok().body(paginacaoUtil);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<EmpresaOrganizadora>> findAll() {
        List<EmpresaOrganizadora> list = empresaOrganizadoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<EmpresaOrganizadora> create(@RequestBody EmpresaOrganizadora empresaOrganizadora) {
        empresaOrganizadora.setChave(empresaOrganizadoraRepository.buscarPrimeiraRemessa());
        empresaOrganizadora.setCnpjEmpresaOrganizadora(empresaOrganizadora.getCnpjEmpresaOrganizadora().replace(".", "").replace("-", "").replace("/", ""));
        empresaOrganizadoraRepository.save(empresaOrganizadora);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(empresaOrganizadora.getId()).toUri();
        return ResponseEntity.created(uri).body(empresaOrganizadora);
    }

    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public ResponseEntity<EmpresaOrganizadora> update(@RequestBody EmpresaOrganizadora empresaOrganizadora, @PathVariable BigInteger id){
        empresaOrganizadora.setId(id);
        empresaOrganizadora.setCnpjEmpresaOrganizadora(empresaOrganizadora.getCnpjEmpresaOrganizadora().replace(".", "").replace("-", "").replace("/", ""));
        empresaOrganizadora.setChave(empresaOrganizadoraRepository.buscarPrimeiraRemessa());
        empresaOrganizadoraRepository.update(empresaOrganizadora);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin
    @Transactional
    @PostMapping("/upload")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok().body(super.setCastorFile(file, "EmpresaOrganizadora"));
    }
}

