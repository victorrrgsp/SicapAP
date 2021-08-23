package com.example.sicapweb.web.controller;

import br.gov.to.tce.util.JayReflection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public abstract class DefaultController<T> {
    public String clazz = "com.example.sicapweb.repository.";

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
            list = (List<T>) JayReflection.executeMethod(clazz, "findAll");
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
            obj = (T) JayReflection.executeMethod(clazz,  "findById",
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
            obj = (T) JayReflection.executeMethod(clazz,  "save",
                    Arrays.asList(object.getClass()), object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
                buildAndExpand(JayReflection.getObject(obj, "id")).toUri();

        return ResponseEntity.created(uri).body(object);
    }

    @CrossOrigin
    @Transactional
    @DeleteMapping(value = {"/{id}"})
    public ResponseEntity<?> delete(@PathVariable BigInteger id) {
        try {
            JayReflection.executeMethod(clazz,  "delete",
                    Arrays.asList(BigInteger.class), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    /*
        List<Error> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(new Error(violation.getMessage()));
        }

        return new ResponseEntity<Object>(errors, new HttpHeaders(), HttpStatus.CONFLICT);
    * */
}
