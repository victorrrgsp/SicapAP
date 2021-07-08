package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.Lei;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class LeiRepository extends DefaultRepository<Lei, BigInteger> {
}
