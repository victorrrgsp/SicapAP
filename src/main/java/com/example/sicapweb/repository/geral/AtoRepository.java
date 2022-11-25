package com.example.sicapweb.repository.geral;

import br.gov.to.tce.model.ap.relacional.Ato;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
public class AtoRepository extends DefaultRepository<Ato, BigInteger> {

    public AtoRepository(EntityManager em) {
        super(em);
    }

    Integer vinculo=1;



    public String getSearch(String searchParams, Integer tipoParams) {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            List<String> lparam =  new ArrayList(Arrays.asList(searchParams.split("&")));
            for (String param : lparam){
                if (tipoParams == 0) { //entra para tratar a string
                    String arrayOfStrings[] = param.split("=");
                    if (arrayOfStrings[0].equals("numeroAto")){
                        search = search + " AND a." + arrayOfStrings[0]  + " like  '%" + arrayOfStrings[1] + "%'  ";
                    }
                    else if (arrayOfStrings[0].equals("idAto")) {
                        search = search + " AND " + arrayOfStrings[0]  + " = " + arrayOfStrings[1] + "  ";
                    }

                } else {
                    search = " AND " + searchParams + "   ";
                }
            }
        }
        return search;
    }


    public PaginacaoUtil<Ato> buscaPaginadaAtos(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
       search = getSearch(searchParams,tipoParams);

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");
        List<Ato> listfiltrada ;
        long totalPaginas;
        long totalRegistros;
            List<Ato> list = getEntityManager()
                    .createNativeQuery("select a.* from Ato a " +
                            " join InfoRemessa info on info.chave = a.chave and info.idUnidadeGestora = '"
                            + User.getUser(request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Ato.class)
                    .setFirstResult(pagina)
                    .setMaxResults(tamanho)
                    .getResultList();


             totalRegistros = countAtos(search);
             totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
            listfiltrada = list;
        return new PaginacaoUtil<Ato>(tamanho, pagina, totalPaginas, totalRegistros, listfiltrada);
    }


    public Integer countAtos(String search) {
        Query query = getEntityManager().createNativeQuery("select count(1) from Ato a join InfoRemessa i on a.chave = i.chave where i.idUnidadeGestora= '"+ User.getUser(request).getUnidadeGestora().getId()+ "' "+search);
        return (Integer) query.getSingleResult();
    }

    public List<Ato> findAll() {
        return getEntityManager()
                .createQuery("select distinct a from Ato" +
                        " a, InfoRemessa info where a.infoRemessa.chave = info.chave and info.idUnidadeGestora = '"
                        + User.getUser(request).getUnidadeGestora().getId() + "'", Ato.class)
                .getResultList();
    }
    public Ato buscarAtoPorNumero(String numero, int tipoAto) {
        List<Ato> list = getEntityManager().createNativeQuery("select top 1* from Ato a " +
                "where a.numeroAto = '"+numero+"' " +
                "and a.tipoAto = '"+tipoAto+" ' " +
                " order by id desc", Ato.class).getResultList();
        return list.get(0);
    }


    public Ato buscarAtoPorNumeroECnpj(String numero, Integer tipoAto, String idUnidadeGestora) {
        try{
            return getEntityManager().createQuery("select a from Ato a , InfoRemessa info  " +
                    "where a.infoRemessa.chave=info.chave and info.idUnidadeGestora = '"+idUnidadeGestora+"' and  a.numeroAto = '"+numero+"' " +
                    "and a.tipoAto = "+tipoAto+" order by a.id " ,Ato.class).setMaxResults(1).getSingleResult();
        }catch (RuntimeException e){
            return null;
        }
    }

    public List<HashMap<String,Object>> buscaVinculos(BigInteger  idato ,Integer vinculo){
        var queryadm = getEntityManager().createNativeQuery("WITH Adm AS (SELECT * FROM Admissao where idAto =:ato ) " +
                " select distinct 'ADMISSÃO' TABELA,a.matriculaServidor , b.cpfServidor , b.nome  , convert(varchar, a.dataExercicio,103)  dataExercicio  from Adm A JOIN Servidor B  ON A.idServidor = B.id  ").setParameter("ato",idato);


        var querylic = getEntityManager().createNativeQuery(" with  lic as (select * from Licenca where idAto=:ato ) " +
                "(select distinct  'LICENÇA' TABELA,case when licencaMotivo=1 then 'Licença para tratamento de saúde' " +
                "when licencaMotivo=2 then 'Licença maternidade' " +
                "when licencaMotivo=3 then 'Licença por motivo de doença em pessoa da família' " +
                "when licencaMotivo=4 then 'Licença por tutoria ou adoção' " +
                "when licencaMotivo=5 then 'Licença por motivo de afastamento do cônjuge ou companheiro' " +
                "when licencaMotivo=6 then 'Licença para o serviço militar' " +
                "when licencaMotivo=7 then 'Licença para atividade política' " +
                "when licencaMotivo=8 then 'Licença para capacitação' " +
                "when licencaMotivo=9 then 'Licença para desempenho de mandato classista' " +
                "when licencaMotivo=10 then 'Licença para servir a outro órgão ou entidade' " +
                "when licencaMotivo=11 then 'Licença para exercer mandato eletivo' " +
                "when licencaMotivo=12 then 'Licença para estudar no país ou no exterior' " +
                "when licencaMotivo=13 then 'Licença para realizar missão oficial no exterior' " +
                "    when licencaMotivo=14 then 'Licença para tratar de interesses particulares' " +
                "    when licencaMotivo=15 then 'Licença Prêmio' " +
                "end licencaMotivo ,case when remunerado=1 then 'Sin' else 'Não' end remunerado,convert(varchar, a.dataInicio,103) dataInicio, convert(varchar, a.dataFim,103) dataFim " +
                "from lic a  join Admissao b on a.idAdmissao = b.id) ").setParameter("ato",idato);




        var querylei = getEntityManager().createNativeQuery(" with  leis as (select c.* from lei  c  where idAto = :ato) " +
                        " select distinct  'LEI' TABELA,l.numeroLei ,l.veiculoPublicacao , convert(varchar, dataPublicacao,103)  dataPublicacao,ementa   from leis l ").setParameter("ato",idato);



        var querydeslig = getEntityManager().createNativeQuery(" with  deslig as (select * from Desligamento where idAto=:ato) " +
                " select distinct 'DESLIGAMENTO' TABELA, case when tipoDesligamento =1 then 'Exoneração' " +
                "when tipoDesligamento =2 then 'Aposentadoria' " +
                "when tipoDesligamento =3 then 'Posse em outro cargo' " +
                "when tipoDesligamento =4 then 'Falecimento' " +
                "when tipoDesligamento =5 then 'Rescisão de contrato' " +
                "when tipoDesligamento =6 then 'Demissão' " +
                "when tipoDesligamento =7 then 'Reserva/Reforma' " +
                "when tipoDesligamento =8 then 'Disponibilidade' " +
                "end  coluna1,b.matriculaServidor ,c.cpfServidor , c.nome nomeServidor " +
                "from deslig a join Admissao b on a.idAdmissao =b.id join Servidor c on b.idServidor = c.id  ").setParameter("ato",idato);



        var querycargo = getEntityManager().createNativeQuery(" with  leis as (select c.* from lei  c  where idAto = :ato) " +
                " select distinct 'CARGO' TABELA,c.nomeCargo , c.codigoCargo ,CASE WHEN jornadaSemanal=1 then '20' " +
                "WHEN jornadaSemanal=2 then '30' " +
                "WHEN jornadaSemanal=3 then '35' " +
                "WHEN jornadaSemanal=4 then '40' " +
                "WHEN jornadaSemanal=5 then '60' " +
                "WHEN jornadaSemanal=6 then 'outros' " +
                "WHEN jornadaSemanal=7 then 'Regime de Plantão)' " +
                "end jornadaSemanal  " +
                "from Cargo c  join leis l  on c.idLei=l.id ").setParameter("ato",idato);


        var queryreadapt = getEntityManager().createNativeQuery("with  readap  as (select * from Readaptacao where idAto = :ato) " +
                "select distinct 'READAPTAÇÃO' TABELA,b.nomeCargo , " +
                "       b.codigoCargo coluna2,CASE WHEN jornadaSemanal=1 then '20' " +
                "WHEN jornadaSemanal=2 then '30' " +
                "WHEN jornadaSemanal=3 then '35' " +
                "WHEN jornadaSemanal=4 then '40' " +
                "WHEN jornadaSemanal=5 then '60' " +
                "WHEN jornadaSemanal=6 then 'outros' " +
                "WHEN jornadaSemanal=7 then 'Regime de Plantão)' " +
                "end jornadaSemanal " +
                "       , convert(varchar, a.dataInicio,103) dataInicio  from readap a join cargo b on a.idCargo = b.id  ").setParameter("ato",idato);


        var queryaposent = getEntityManager().createNativeQuery("with  aposent as (select * from Aposentadoria where idAto= :ato) " +
                "select distinct  'APOSENTADORIA' TABELA,B.cpfServidor coluna1 , B.nome nomeServidor , case when tipoAposentadoria = 1 then 'Compulsória' " +
                "when tipoAposentadoria = 2 then 'Especial' " +
                "when tipoAposentadoria = 3 then 'Implemento de idade' " +
                "when tipoAposentadoria = 4 then 'Invalidez' " +
                "when tipoAposentadoria = 5 then 'Tempo de contribuição' " +
                "when tipoAposentadoria = 6 then 'Reserva' " +
                "when tipoAposentadoria = 7 then 'Reforma' " +
                "when tipoAposentadoria = 8 then 'Voluntária' " +
                "end tipoAposentadoria,convert(varchar, a.dataAposentadoria,103) coluna4 " +
                "from aposent a inner join Servidor b on a.cpfServidor = b.cpfServidor").setParameter("ato",idato);



        var queryrecond = getEntityManager().createNativeQuery("with recond as (select * from Reconducao where idAto= :ato  ) " +
                "select  distinct 'RECONDUÇÃO' TABELA, b.nomeCargo,  convert(varchar, a.dataExercicio,103)  dataExercicio  from recond a join Cargo b on a.idCargo=b.id").setParameter("ato",idato);




        var queryreintegracao = getEntityManager().createNativeQuery("with Reinteg as (select * from Reintegracao where idAto=:ato  )" +
                "select  distinct 'REINTEGRAÇÃO' TABELA, b.nomeCargo,  convert(varchar, a.dataExercicio,103)  dataExercicio  from Reinteg a join Cargo b on a.idCargo=b.id").setParameter("ato",idato);

        var querypensao = getEntityManager().createNativeQuery("with PENS as (select * from Pensao where idAto=:ato  )" +
                "select  distinct 'PENSÃO' TABELA, b.cpfServidor ,b.nome nomeServidor,  convert(varchar, a.dataObito,103)  dataObito,a.cnpjUnidadeGestoraOrigem   from PENS a join Servidor b on a.cpfServidor=b.cpfServidor ").setParameter("ato",idato);

        var querypensionista = getEntityManager().createNativeQuery("with PENS as (select * from Pensionista where idAto=:ato  )" +
                "select distinct 'PENSIONISTA' TABELA,A.cpfServidor, A.cpfPensionista, A.nome nomePensionist, convert(varchar, a.inicioBeneficio,103) inicioBeneficio, convert(varchar, a.fimBeneficio,103)  fimBeneficio , a.cnpjUnidadeGestoraOrigem  from PENS A").setParameter("ato",idato);

        var querydesinacao = getEntityManager().createNativeQuery("with desig as (select * from DesignacaoFuncao where idAto =:ato )" +
                "select distinct 'DESIGNAÇÃO',B.nomeCargo, CASE WHEN a.recebeComissao =1 then 'Sim' else 'Não' end recebeComissao , c.matriculaServidor, d.cpfServidor, d.nome nomeServidor, convert(varchar, a.dataInicio,103) dataInicio, convert(varchar, a.dataFim,103)  dataFim     from desig a join Cargo b on a.idCargo=b.id  join Admissao c on a.idAdmissao= c.id join Servidor d on c.idServidor = d.id ").setParameter("ato",idato);


        var querycess = getEntityManager().createNativeQuery("with Cess as (select * from Cessao where  idAto=:ato) " +
                "select  distinct 'CESSÃO' TABELA,b.matriculaServidor, c.cpfServidor , c.nome nomeServidor ,convert(varchar, a.dataInicio,103) dataInicio , convert(varchar, a.dataFim,103) dataFim , a.cnpjOrgaoDestino    from Cess a join Admissao b on a.idAdmissao = b.id join Servidor c on b.idServidor = c.id ").setParameter("ato",idato);

        var queryDisp = getEntityManager().createNativeQuery("with disponib as (select * from Disponibilidade where idAto=:ato) " +
                "select  distinct 'DISPONIBILIDADE' TABELA,convert(varchar, a.dataDisponibilidade,103) dataDisponibilidade  from disponib a ").setParameter("ato",idato);


        var queryAprov = getEntityManager().createNativeQuery("with aprov as (select * from Aproveitamento where idAto=:ato) " +
                "select distinct  'DISPONIBILIDADE' TABELA,b.nomeCargo,convert(varchar, a.dataAproveitamento,103) dataAproveitamento  from aprov a join Cargo b on a.idCargo = b.id ").setParameter("ato",idato);



        List<HashMap<String,Object>> retorno = new ArrayList<HashMap<String,Object>>();
        switch (vinculo) {
            case 1:
                retorno.addAll(getMapList(queryadm));
                retorno.addAll(getMapList(querylic));
                retorno.addAll(getMapList(querylei));
                retorno.addAll(getMapList(querycargo));
                retorno.addAll(getMapList(querydeslig));
                retorno.addAll(getMapList(queryaposent));
                retorno.addAll(getMapList(queryreadapt));
                retorno.addAll(getMapList(queryrecond));
                retorno.addAll(getMapList(queryreintegracao));
                retorno.addAll(getMapList(querypensao));
                retorno.addAll(getMapList(querypensionista));
                retorno.addAll(getMapList(querydesinacao));
                retorno.addAll(getMapList(querycess));
                retorno.addAll(getMapList(queryDisp));
                retorno.addAll(getMapList(queryAprov));
                break;
            case 2:
                retorno.addAll(getMapList(queryadm));
                break;
            case 3:
                retorno.addAll(getMapList(querylei));
                break;
            case 4:
                retorno.addAll(getMapList(querycargo));
                break;
            case 5:
                retorno.addAll(getMapList(querylic));
                break;
            case 6:
                retorno.addAll(getMapList(querydeslig));
                break;
            case 7:
                retorno.addAll(getMapList(queryaposent));
                break;
            case 8:
                retorno.addAll(getMapList(queryreadapt));
                break;
            case 9:
                retorno.addAll(getMapList(queryrecond));
                break;
            case 10:
                retorno.addAll(getMapList(queryreintegracao));
                break;
            case 11:
                retorno.addAll(getMapList(querypensao));
                break;
            case 12:
                retorno.addAll(getMapList(querypensionista));
                break;
            case 13:
                retorno.addAll(getMapList(querydesinacao));
                break;
            case 14:
                retorno.addAll(getMapList(querycess));
                break;
            case 15:
                retorno.addAll(getMapList(queryDisp));
                break;
            case 16:
                retorno.addAll(getMapList(queryDisp));
                break;

        }


        return retorno;
    }
    private List<HashMap<String,Object>> getMapList(Query query) {
       return   ( (NativeQueryImpl) query).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).getResultList();
    }



}
