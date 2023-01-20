package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Cargo;
import com.example.sicapweb.repository.DefaultRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository extends DefaultRepository<Cargo, BigInteger> {

    public UsuarioRepository(EntityManager em) {
        super(em);
    }

    public List<Object> getUser(String codigo, Integer sistema) {
        var autorizados = List.of("02908453193", "55786553191", "02925533159");

        if (autorizados.contains(codigo)) {
            return getEntityManager().createNativeQuery("   " +
                    "select cpf, unidadeGestora, nomeEntidade, codpoder, Cargo  " +
                    "from AutenticacaoAssinatura..usuario a  " +
                    "join AutenticacaoAssinatura..UsuarioAplicacao b on a.CPF = b.Usuario " +
                    "join UnidadeGestoraCadun c on UnidadeGestora = cnpj " +
                    "where cpf=  '" + codigo + "' and cargo in (4,5,3,32,362) and Aplicacao = " + sistema).getResultList();
        }

        return getEntityManager().createNativeQuery(
                "select distinct pf.cpf, upc.cnpj, upc.nomeEntidade, uni.poder, upc.idCargo " +
                        "      from cadun.dbo.vwUnidadesPessoasCargos upc " +
                        "               join cadun.dbo.vwpessoa pf on pf.id = upc.idPessoaFisica " +
                        "               join Cadun.dbo.PessoaJuridica pj on upc.idPessoaJuridica = pj.Codigo " +
                        "               join SICAPAP21..UnidadeGestora uni on uni.id = upc.cnpj " +
                        "      where upc.idCargo in (3, 4, 32, 362) " +
                        "        and upc.ativo = 1 " +
                        "        and pf.cpf = '" + codigo + "'").getResultList();


    }

    public Integer buscarCargoPorCpf(String cpf) {
        return (Integer) getEntityManager().createNativeQuery(
                "select idCargo " +
                        "from Cadun..vwUnidadesPessoasCargos " +
                        "where cpf = '"+ cpf +"';").getSingleResult();
    }
}
