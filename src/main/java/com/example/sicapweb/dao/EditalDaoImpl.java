package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.Edital;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class EditalDaoImpl extends DefaultDao<Edital, BigInteger> implements EditalDao {
}
