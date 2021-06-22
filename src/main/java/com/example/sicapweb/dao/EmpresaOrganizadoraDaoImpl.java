package com.example.sicapweb.dao;

import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class EmpresaOrganizadoraDaoImpl extends DefaultDao<EmpresaOrganizadora, BigInteger> implements EmpresaOrganizadoraDao {
}
