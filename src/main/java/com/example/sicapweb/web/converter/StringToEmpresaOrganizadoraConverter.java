package com.example.sicapweb.web.converter;


import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.repository.EmpresaOrganizadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class StringToEmpresaOrganizadoraConverter implements Converter<String, EmpresaOrganizadora> {

    @Autowired
    private EmpresaOrganizadoraRepository empresaOrganizadoraRepository;

    @Override
    public EmpresaOrganizadora convert(String text) {
        if (text.isEmpty())
            return null;

        BigInteger id = BigInteger.valueOf(Long.parseLong(text));
        return empresaOrganizadoraRepository.findById(id);
    }
}
