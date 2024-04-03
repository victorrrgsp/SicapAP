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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            "                  join Cadun.dbo.PessoaJuridica pj on upc.CodigoPessoaJuridica = pj.Codigo" +
                            "                  join SICAPAP21..AdmAssinatura ad on ad.idAssinatura = b.OID " +
                            "                  join SICAPAP21..InfoRemessa i on i.chave = ad.chave" +
                            "         where upc.CodigoCargo in (:tipo)" +
                            "           and (dataInicio <= :date and (datafim is null or datafim >= :date))" +
                            "           and i.idUnidadeGestora = :unidade" +
                            "           and pj.CNPJ = :unidade" +
                            "           and c.Exercicio = :exercicio" +
                            "           and c.Bimestre = :remessa" +
                            "           and ua.Aplicacao = 29" +
                            "     ) as v " +
                            "group by nome, cpf, CodigoCargo;");
            query.setParameter("tipo", tipoCargo);
            query.setParameter("date", infoRemessa.getAdmFilaRecebimento().getDataProcessamento());
            query.setParameter("unidade", user.getUser(super.request).getUnidadeGestora().getId());
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
          " and i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "'", InfoRemessa.class).getResultList();
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
          " and i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "'", InfoRemessa.class).getResultList();
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
        " and i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "' "+search +"ORDER BY i.exercicio, i.remessa desc", InfoRemessa.class)
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
      " and i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "' " );
    return (Integer) query.getSingleResult();
  }

    public List<Map<String,Object>> buscarAcompanhamentoRemessa(Integer exercicio, Integer remessa, String chave) {

        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery("with ugsAptas as (" +
                    "select *" +
                    " from cadun.dbo.vwUnidadeGestora" +
                    " where  cadun.dbo.EhVigente(" + exercicio + ", " + remessa + ",CNPJ,  29) = 1" +
                    ")," +
                    " ugsAssinantes as (" +
                    " SELECT COUNT(DISTINCT idCargo) AS contAssinaturas, IDUNIDADEGESTORA , i.EXERCICIO, i.remessa, i.chave, max(i.relatoria) relatoria, max(f.dataEnvio) dataEntrega, max(dataAssinatura) dataAssinatura" +
                    " FROM ugsAptas ug left join" +
                    " inforemessa i on i.IDUNIDADEGESTORA = ug.CNPJ and i.EXERCICIO = " + exercicio + " and i.REMESSA = " + remessa + " and i.status = 1 left join" +
                    " admassinatura  a on a.chave = i.CHAVE  left join" +
                    " AutenticacaoAssinatura..Assinatura  ass on ass.oid = a.idAssinatura left join" +
                    " admfilarecebimento f on f.id = i.idfilarecebimento" +
                    " group by IDUNIDADEGESTORA, i.EXERCICIO, i.REMESSA, i.CHAVE" +
                    ")" +
                    " select  cnpj, NomeMunicipio + ' - '+ nomeEntidade nomeEntidade, case when b.relatoria is null then a.numeroRelatoria else b.relatoria end relatoria," +
                    " dataEntrega, dataAssinatura, contAssinaturas, case when  contAssinaturas >= 3 then 1 else 0 end mostraRecibo, exercicio, remessa, chave" +
                    " from ugsAptas a left join" +
                    " ugsAssinantes b on a.cnpj = idUnidadeGestora" +
                    " where CNPJ <> '00000000000000'" +
                    " and chave = '" + chave + "' " +
                    " order by NomeMunicipio, nomeEntidade").getResultList();

            //list.stream().forEach((record) -> {

            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("cnpj", (String) obj[0]);
                mapa.put("nomeEntidade", (String) obj[1]);
                mapa.put("relatoria", (Integer) obj[2]);
                mapa.put("dataEntrega", (String) obj[3]);
                mapa.put("dataAssinatura", (String) obj[4]);
                mapa.put("contAssinaturas", (Integer) obj[5]);
                mapa.put("mostraRecibo", (Integer) obj[6]);
                mapa.put("exercicio", (Integer) obj[7]);
                mapa.put("remessa", (Integer) obj[8]);
                mapa.put("chave", (String) obj[9]);
                retorno.add(mapa);

            }
            ;

            return retorno;


        } catch (Exception e) {
            return null;
        }
    }





    public PaginacaoUtil<Object> buscaPaginadaAcompanhamento(Pageable pageable, String searchParams, Integer tipoParams) {
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


        List<Object> list = getEntityManager()
                .createNativeQuery("SELECT COUNT(DISTINCT idCargo) AS contAssinaturas," +
                                        "IDUNIDADEGESTORA, i.EXERCICIO, i.remessa, i.chave," +
                                        " max(i.relatoria)           relatoria, " +
                                        "max(f.dataEnvio)           dataEntrega, " +
                                        "max(dataAssinatura)        dataAssinatura, i.nomeUnidade " +
                                        "FROM inforemessa i " +
                                        "left join admassinatura a on a.chave = i.CHAVE " +
                                        "left join AutenticacaoAssinatura..Assinatura ass on ass.oid = a.idAssinatura " +
                                        "left join admfilarecebimento f on f.id = i.idfilarecebimento " +
                        "where IDUNIDADEGESTORA = '"+user.getUser(super.request).getUnidadeGestora().getId()+"' " +
                                        "group by IDUNIDADEGESTORA, i.EXERCICIO, i.REMESSA, i.CHAVE, i.nomeUnidade " +
                        "   order by i.EXERCICIO desc, i.REMESSA desc")
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = counts();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        return new PaginacaoUtil<Object>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }


}
