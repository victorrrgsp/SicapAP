package com.example.sicapweb.repository.remessa;

import br.gov.to.tce.model.InfoRemessa;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class AcompanhamentoDeRemessasRepository extends DefaultRepository<String, String> {
    @PersistenceContext
    private EntityManager entityManager;

    public AcompanhamentoDeRemessasRepository(EntityManager em) {
        super(em);
    }

    public AcompanhamentoDeRemessasRepository() {

    }

    public Object buscarResponsavelAssinatura(Integer tipoCargo, InfoRemessa infoRemessa) {
        try {
            Query query = entityManager.createNativeQuery(
                    "select  nome, cpf, max(status) status, max(data) data, CodigoCargo " +
                            "from (" +
                            "         select distinct pf.nome, pf.cpf, null status, null data, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade and ua.Aplicacao = 29" +
                            "         union" +
                            "         select distinct pf.nome, pf.cpf, b.DataAssinatura, b.DataAssinatura, upc.CodigoCargo" +
                            "         from cadun.dbo.UnidadePessoaCargo upc" +
                            "                  join cadun.dbo.vwpessoa pf on pf.id = upc.CodigoPessoaFisica" +
                            "                  join AutenticacaoAssinatura..UsuarioAplicacao ua on ua.Usuario = pf.cpf" +
                            "                  join AutenticacaoAssinatura..Assinatura b on ua.Usuario = b.Usuario" +
                            "                  join AutenticacaoAssinatura..InfoAssinatura c on c.Assinatura = b.OID and ua.Aplicacao = c.Aplicacao" +
                            "         join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and pj.CNPJ = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", new Date());
            query.setParameter("unidade", User.getUser().getUnidadeGestora().getId());
            query.setParameter("exercicio", infoRemessa.getExercicio());
            query.setParameter("remessa", infoRemessa.getRemessa());
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


  public List<InfoRemessa> buscarRemessaFechada() {
    try {
      return  entityManager.createNativeQuery(
        "select * from InfoRemessa i" +
          " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) > 0" +
          " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", InfoRemessa.class).getResultList();
    } catch (Exception e) {
      return null;
    }
  }

  public InfoRemessa findRemessaFechada(String chave) {
    try {
      return (InfoRemessa) entityManager.createNativeQuery(
        "select * from InfoRemessa i" +
          " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) = 3" +
          " and i.chave = '" + chave + "'", InfoRemessa.class).getSingleResult();
    } catch (Exception e) {
      return null;
    }
  }
  public List<InfoRemessa> filtroRemessaFechada(String exercicio){
    try {
      return  entityManager.createNativeQuery(
        "select * from InfoRemessa i" +
          " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) > 0" +
          " and i.exercicio = '" + exercicio + "'"+
          " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "'", InfoRemessa.class).getResultList();
    } catch (Exception e) {
      return null;
    }
  }


  public PaginacaoUtil<InfoRemessa> buscaPaginadaHistorico(Pageable pageable, String searchParams, Integer tipoParams) {
    int pagina = Integer.valueOf(pageable.getPageNumber());
    int tamanho = Integer.valueOf(pageable.getPageSize());
    String search = "";
    //monta pesquisa search
    if (searchParams.length() > 3) {
      if (tipoParams == 0) { //entra para tratar a string
        String arrayOfStrings[] = searchParams.split("=");

          search = " and i." + arrayOfStrings[0] + "=" + arrayOfStrings[1] ;
      }else {
        search = " and " + searchParams + "   ";
      }
    }
    //retirar os : do Sort pageable
    String campo = String.valueOf(pageable.getSort()).replace(":", "");


    List<InfoRemessa> list = getEntityManager()
      .createNativeQuery("select * from InfoRemessa i" +
        " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) > 0" +
        " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' "+search +"ORDER BY i.exercicio, i.remessa desc", InfoRemessa.class)
      .setFirstResult(pagina)
      .setMaxResults(tamanho)
      .getResultList();
    long totalRegistros = counts();
    long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
    return new PaginacaoUtil<InfoRemessa>(tamanho, pagina, totalPaginas, totalRegistros, list);
  }

  public Integer counts() {
    Query query = getEntityManager().createNativeQuery("select count(*) from InfoRemessa i" +
      " where (select count(DISTINCT a.idCargo) from AdmAssinatura a where a.chave = i.chave) > 0" +
      " and i.idUnidadeGestora = '" + User.getUser().getUnidadeGestora().getId() + "' " );
    return (Integer) query.getSingleResult();
  }

}
