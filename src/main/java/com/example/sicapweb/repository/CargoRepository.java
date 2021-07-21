package com.example.sicapweb.repository;

import br.gov.to.tce.model.ap.relacional.Cargo;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class CargoRepository extends DefaultRepository<Cargo, BigInteger> {
}
