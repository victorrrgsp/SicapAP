package com.example.sicapweb.repository;
import br.gov.to.tce.model.adm.AdmAutenticacao;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class ChavesRepository extends DefaultRepository<AdmAutenticacao, BigInteger> {

}
