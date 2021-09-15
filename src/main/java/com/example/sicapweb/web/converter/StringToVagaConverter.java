package com.example.sicapweb.web.converter;

import br.gov.to.tce.model.ap.concurso.EditalVaga;
import com.example.sicapweb.repository.concurso.EditalVagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToVagaConverter implements Converter<String, EditalVaga> {

    @Autowired
    private EditalVagaRepository editalVagaRepository;


    @Override
    public EditalVaga convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return editalVagaRepository.findById(id);
    }
}
