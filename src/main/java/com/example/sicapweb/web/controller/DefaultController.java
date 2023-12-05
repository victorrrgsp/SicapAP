package com.example.sicapweb.web.controller;

import br.gov.to.tce.castor.application.ApplicationException;
import br.gov.to.tce.castor.arquivo.CastorController;
import br.gov.to.tce.castor.arquivo.ObjetoCastor;
import br.gov.to.tce.model.CastorFile;
import br.gov.to.tce.util.JayReflection;

import com.example.sicapweb.exception.InvalitInsert;
import com.example.sicapweb.repository.geral.CastorFileRepository;
import com.example.sicapweb.repository.geral.UnidadeGestoraRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Transactional
@Controller
@RequestMapping
public abstract class DefaultController<T> {
    public String clazz;
    {
        try {
            clazz = JayReflection.getClassNameByPackage(
                    "com.example.sicapweb",
                    Class.forName(((ParameterizedType) this.getClass().getGenericSuperclass())
                            .getActualTypeArguments()[0].getTypeName()).getSimpleName()+"Repository"
                    );

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UnidadeGestoraRepository repository;

    public String path = System.getProperty("user.home") + File.separator + "spring" + File.separator;

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity handleValidationException(ValidationException ex) {
        return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<T>> findAll() {
        List<T> list = null;
        try {
            list = (List<T>) JayReflection.executeMethod(clazz,
                    Arrays.asList(EntityManager.class),
                    Arrays.asList(repository.getEntityManager()),
                    "findAll");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //empresaOrganizadoraRepository.findAll();
        return ResponseEntity.ok().body(list);
    }

    @CrossOrigin
    @GetMapping(path = {"/{id}"})
    public ResponseEntity<?> findById(@PathVariable BigInteger id) {
        T obj = null;
        try {
            obj = (T) JayReflection.executeMethod(clazz,
                    Arrays.asList(EntityManager.class),
                    Arrays.asList(repository.getEntityManager()),
                    "findById",
                    Arrays.asList(Serializable.class), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(obj);
    }

    @CrossOrigin
    @Transactional
    @PostMapping
    public ResponseEntity<T> create(@RequestBody T object) {
        T obj = null;
        try {
            JayReflection.executeMethod(clazz,
                    Arrays.asList(EntityManager.class),
                    Arrays.asList(repository.getEntityManager()),
                    "save",
                    Arrays.asList(Object.class), object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
                buildAndExpand(JayReflection.getObject(object, "id")).toUri();

        return ResponseEntity.created(uri).body(object);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public void delete(@PathVariable BigInteger id) {
        try {
            JayReflection.executeMethod(clazz,
                    Arrays.asList(EntityManager.class),
                    Arrays.asList(repository.getEntityManager()),
                    "delete",
                    Arrays.asList(BigInteger.class), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin
    @PostMapping(value = {"/getFileName"})
    public ResponseEntity<?> getCastorFileName(@RequestBody CastorFile castorFile) {
        new File(path).mkdirs();
        CastorController castor = new CastorController();
        ObjetoCastor objeto = new ObjetoCastor(castorFile.id);
        if (castorFile.id != null) {
            try {
                objeto = castor.carregar(objeto);
                if (objeto.getBytes().length <= 0) return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<String> header = objeto.getHeaders().getHeaderValues("x-TCE-meta-Nome");
            return ResponseEntity.ok().body(Objects.requireNonNullElse(objeto.getNomeArquivo(), header!=null?header.get(0): null  ));
        }
        return null;
    }

    @CrossOrigin
    @PostMapping(value = {"/downloadCastor"})
    public ResponseEntity<?> getCastorFile(@RequestBody CastorFile castorFile) {
        if (castorFile.id == null) return ResponseEntity.ok().body(null);

        new File(path).mkdirs();

        CastorController castor = new CastorController();
        ObjetoCastor objeto = new ObjetoCastor(castorFile.id);
        try {
            objeto = castor.carregar(objeto);
            if (objeto.getBytes().length <= 0) return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("filename", objeto.getNomeArquivo());

        List<String> header = objeto.getHeaders().getHeaderValues("x-TCE-meta-Nome");

        MimeType contentType = null;
        try {
            contentType = MimeTypeUtils.parseMimeType(new File(Objects.requireNonNullElse(objeto.getNomeArquivo(), header!=null?header.get(0): null   ) ).toURL().openConnection().getContentType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().contentType(MediaType.asMediaType(contentType)).
                headers(headers).
                body(new ByteArrayResource(objeto.getBytes()));
    }

    @CrossOrigin
    @GetMapping(value = {"/file"})
//    public ResponseEntity<?> getCastorFile(@PathVariable BigInteger hash){
    public ResponseEntity<?> getCastorFile() {
        String hash = "006a96a25fd86176eaf78382bba2a496";
        new File(path).mkdirs();
        CastorController castor = new CastorController();
        ObjetoCastor objeto = new ObjetoCastor(hash);
        if (hash != null) {
            try {
                objeto = castor.carregar(objeto);

                if (objeto.getBytes().length <= 0) return null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("filename", objeto.getNomeArquivo());

            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + objeto.getNomeArquivo() + "\"").
                    body(new ByteArrayResource(objeto.getBytes()));
        }
        return null;
    }

    public void getFileType(MultipartFile file) {
        String fileType = file.getContentType();
        if (!(fileType.equals("application/pdf"))) {
            throw new InvalitInsert("Tipo de arquivo inválido envie um arquivo pdf ");
            // return ResponseEntity.badRequest().body("Tipo de arquivo inválido envie um arquivo pdf ");
        }
    }
    public String setCastorFile(MultipartFile file, String origem) {
        CastorFile castorFile = getCastorFile(file, origem);
        return castorFile != null ? castorFile.getId() : "";
    }

    public CastorFile getCastorFile(MultipartFile file, String origem) {
        new File(path).mkdirs();
        String idCastor = "";
        File fileTemp = new File(path + file.hashCode() + "." +
                FilenameUtils.getExtension(file.getOriginalFilename()));

        CastorController castor = new CastorController();
        ObjetoCastor objeto = new ObjetoCastor();

        CastorFile castorFile = null;
        try {
            file.transferTo(fileTemp);

            objeto = castor.ArquivoParaObjetoCastor(fileTemp);
            objeto.setNomeArquivo(file.getOriginalFilename());
            objeto = castor.gravarArquivo(objeto);

            castorFile = new CastorFile(objeto.getUUID(), origem);
            new CastorFileRepository(repository.getEntityManager()).save(castorFile);
            fileTemp.delete();
        } catch (Exception e) {
            e.printStackTrace();
            castorFile = null;
            fileTemp.delete();
            try {
                castor.deletar(objeto);
            } catch (ApplicationException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return castorFile;
    }
    /*
        List<Error> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new Error(violation.getMessage()));
        }

        return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.CONFLICT);
    * */
}
