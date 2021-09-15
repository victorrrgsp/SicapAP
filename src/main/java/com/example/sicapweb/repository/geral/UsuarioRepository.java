package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;

@Repository
public class UsuarioRepository extends DefaultRepository<Cargo, BigInteger> {

    public UsuarioRepository(EntityManager em) {
        super(em);
    }

    public List<Object> getUser(String codigo, Integer sistema) {
        return getEntityManager().createNativeQuery("  \n" +
                "select cpf, unidadeGestora, nomeEntidade, codpoder, Cargo \n" +
                "from AutenticacaoAssinatura..usuario a \n" +
                "\tjoin AutenticacaoAssinatura..UsuarioAplicacao b on a.CPF = b.Usuario\n" +
                "\tjoin UnidadeGestoraCadun c on UnidadeGestora = cnpj\n" +
                "where cpf=  '"+codigo+"' and Aplicacao = "+ sistema).getResultList();
    }
}
