package com.example.sicapweb.web.converter;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.geral.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToCargoConverter implements Converter<String, Cargo> {

    @Autowired
    private CargoRepository cargoRepository;


    @Override
    public Cargo convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return cargoRepository.findById(id);
    }
}
