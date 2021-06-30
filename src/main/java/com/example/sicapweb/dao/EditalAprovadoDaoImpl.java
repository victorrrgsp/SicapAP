package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class EditalAprovadoDaoImpl extends DefaultDao<EditalAprovado, BigInteger> implements EditalAprovadoDao {
}
