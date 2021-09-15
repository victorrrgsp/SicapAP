package com.example.sicapweb.web.converter;

import br.gov.to.tce.model.ap.concurso.Edital;
import com.example.sicapweb.repository.concurso.EditalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToEditalConverter implements Converter<String, Edital> {

    @Autowired
    private EditalRepository editalRepository;


    @Override
    public Edital convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return editalRepository.findById(id);
    }
}
