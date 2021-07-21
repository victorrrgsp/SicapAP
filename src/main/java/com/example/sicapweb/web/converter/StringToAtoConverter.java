package com.example.sicapweb.web.converter;

import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.AtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToAtoConverter implements Converter<String, Ato> {

    @Autowired
    private AtoRepository atoRepository;


    @Override
    public Ato convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return atoRepository.findById(id);
    }
}
