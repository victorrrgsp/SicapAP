package com.example.sicapweb.web.controller;

import br.gov.to.tce.util.JayReflection;
import com.example.sicapweb.repository.UnidadeGestoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public abstract class DefaultController<T> {
    public String clazz;

    {
        try {
            clazz = "com.example.sicapweb.repository."+
                    Class.forName( ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName()).getSimpleName()+"Repository";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UnidadeGestoraRepository repository;

    @Value("${upload.path}")
    public String path;

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }

        return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.CONFLICT);
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
                    Arrays.asList(object.getClass()), object);
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
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        try {
            JayReflection.executeMethod(clazz,
                    Arrays.asList(EntityManager.class),
                    Arrays.asList(repository.getEntityManager()),
                    "delete",
                    Arrays.asList(BigInteger.class), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    public String setCastorFile(MultipartFile file, String origem) {
        String idCastor = "";
        File fileTemp = new File(path+file.hashCode()+"."+
                FilenameUtils.getExtension(file.getOriginalFilename()));

        CastorController castor = new CastorController();
        ObjetoCastor objeto = new ObjetoCastor();

        try {
            file.transferTo(fileTemp);

            objeto = castor.ArquivoParaObjetoCastor(fileTemp);
            objeto.setNomeArquivo(file.getOriginalFilename());
            objeto = castor.gravarArquivo(objeto);
            idCastor = objeto.getUUID();

            new CastorFileRepository(repository.getEntityManager()).save(new CastorFile(idCastor));
            fileTemp.delete();
        } catch (Exception e) {
            e.printStackTrace();
            idCastor = "";
            fileTemp.delete();
            try {
                castor.deletar(objeto);
            } catch (ApplicationException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return idCastor;
    }
    /*
        List<Error> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new Error(violation.getMessage()));
        }

        return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.CONFLICT);
    * */
}
