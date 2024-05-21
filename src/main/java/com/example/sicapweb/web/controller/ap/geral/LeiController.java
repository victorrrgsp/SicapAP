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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            if (lei.getAto() != null) {
                lei.setNumeroAto(lei.getAto().getNumeroAto());
                lei.setTipoAto(lei.getAto().getTipoAto());
            }
            if (leiRepository.ExistLeiIqual(lei.getNumeroLei(),lei.getNumeroAto(),lei.getTipoAto(),lei.getVeiculoPublicacao(),lei.getDataPublicacao()) )  {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(lei);
              //  throw new InvalitInsert(" já existe registro da lei gravado");
            }
            else {
                leiRepository.save(lei);
                URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lei.getId()).toUri();
                return ResponseEntity.created(uri).body(lei);
            }
        }  catch (Exception e) {
            throw new InvalitInsert("Erro na insersao de dados, por favor cheque os canpos enviados ");
            //TODO: handle exception
        }
    }
        
    @CrossOrigin
    @Transactional
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public void update(@RequestBody Lei lei, @PathVariable BigInteger id) {
        try {
            
            lei.setChave(atoRepository.buscarPrimeiraRemessa());
            lei.setAto(atoRepository.findById(lei.getAto().getId()));
            lei.setId(id);
            
            leiRepository.update(lei);
        } catch (Exception e) {
            throw new InvalitInsert("Erro na insersao de dados, por favor cheque os campos enviados ");
            //TODO: handle exception
        }
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        leiRepository.deletDuplicatedLeiById(id);
    }


    @CrossOrigin
    @Transactional
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, @PathVariable BigInteger id) {

        // Verificar o tipo de arquivo
        getFileType(file);

        Lei lei = new Lei();
        lei = leiRepository.findById(id);
        CastorFile castorFile = super.getCastorFile(file, "Lei");
        lei.setCastorFile(castorFile);
        leiRepository.update(lei);
        return ResponseEntity.ok().body(castorFile.getId());
    }


    @CrossOrigin
    @Transactional
    @PostMapping(value = "/upload2/{id}", consumes = "application/pdf")
    public ResponseEntity<?> addFile2(@RequestBody byte[] fileContent, 
                                        @PathVariable BigInteger id,
                                        @RequestParam("name") String fileName
                                        ) throws IOException {
        File file = saveBytesToFile(fileContent, fileName);
        // Verificar o tipo de arquivo
        //getFileType(file);

        Lei lei = new Lei();
        lei = leiRepository.findById(id);
        CastorFile castorFile = super.getCastorFile(file, "Lei");
        lei.setCastorFile(castorFile);
        leiRepository.update(lei);
        return ResponseEntity.ok().body(castorFile.getId());
    }
    private File saveBytesToFile(byte[] content, String filename) throws IOException {
        // Determinar um diretório para salvar o arquivo
        Path path = Paths.get("uploads/" + filename);
        // Garantir que os diretórios existam
        Files.createDirectories(path.getParent());

        // Criar um objeto File com base no path
        File file = path.toFile();
        // Usar FileOutputStream para escrever os bytes no arquivo
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }
        return file;
    }

    @CrossOrigin
    @GetMapping(path = {"anexos/{id}"})
    public ResponseEntity<?> findByDocumento(@PathVariable BigInteger id) {
        Lei list = leiRepository.buscarDocumentoLei(id).get(0);
        return ResponseEntity.ok().body(list);
    }
}
