package com.example.sicapweb.web.converter;

import br.gov.to.tce.model.ap.relacional.UnidadeAdministrativa;
import com.example.sicapweb.repository.geral.UnidadeAdministrativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToUnidadeAdministrativaConverter implements Converter<String, UnidadeAdministrativa> {

    @Autowired
    private UnidadeAdministrativaRepository unidadeAdministrativaRepository;


    @Override
    public UnidadeAdministrativa convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return unidadeAdministrativaRepository.findById(id);
    }
}
