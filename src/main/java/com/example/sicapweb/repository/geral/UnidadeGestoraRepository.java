package com.example.sicapweb.repository.geral;

import br.gov.to.tce.application.ApplicationException;
import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Repository
public class UnidadeGestoraRepository extends DefaultRepository<UnidadeGestora, String> {

    private final Class<UnidadeGestora> entityClass = (Class<UnidadeGestora>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    @PersistenceContext
    EntityManager entityManager;

    public UnidadeGestoraRepository(EntityManager em) {
        super(em);
    }

    public UnidadeGestora buscaUnidadeGestoraPorCnpj(String Cnpj) {
        List<UnidadeGestora> list = entityManager.createNativeQuery("select * from UnidadeGestora " +
                " where id = '" + Cnpj + "'    ", UnidadeGestora.class).getResultList();
        return list.get(0);
    }

    public List<UnidadeGestora> buscaTodasUnidadeGestora() {
        List<UnidadeGestora> list = entityManager.createNativeQuery("select * from UnidadeGestora ORDER BY nome ASC"
                , UnidadeGestora.class).getResultList();
        return list;
    }

    public List<Object[]> buscaugServidoresComsociedade() {
        List<Object[]> list = entityManager.createNativeQuery("with servidores as(\n" +
                "\tselect\n" +
                "\t\t*\n" +
                "\tfrom sicapap..vwServidoresEmFolha2 s  --WITH (NOEXPAND)\n" +
                "\t\twhere s.ano = 2017 and mes = 9\n" +
                "),\n" +
                "quadro as(\n" +
                "\tselect\n" +
                "\t\t* ,SUBSTRING(q.CpfcnpjSocio,4,14) cpf\n" +
                "\tfrom cadun..vwQuadroSocietario  q\n" +
                "\t\twhere q.ehFisica =1\n" +
                "),\n" +
                "valoresLiquidacoes as(\n" +
                "\t\tselect i.idUnidadeGestora,c.idcredor, SUM(abs(valor)) valor\n" +
                "\t\tfrom Sicap_Contabil_NOVO..liquidacao l inner join\n" +
                "\t\t\tSicap_Contabil_NOVO..inforemessa i on l.chave = i.chave inner join\n" +
                "\t\t\tSicap_Contabil_NOVO..credor c on c.id = l.idcredor\n" +
                "\t\tgroup by i.idUnidadeGestora, c.idcredor\n" +
                "),\n" +
                "valoresLiquidacoesGeral as(\n" +
                "\t\tselect idcredor, SUM(abs(valor)) valor\n" +
                "\t\tfrom valoresLiquidacoes\n" +
                "\t\tgroup by idcredor\n" +
                ")\n" +
                "\n" +
                "select\n" +
                "distinct\n" +
                "\tug.nomeEntidade Entidade,\n" +
                "\tug.cnpj\n" +
                "\n" +
                "from  servidores s\n" +
                "\tleft join quadro q on q.cpf = s.cpf\n" +
                "\tleft join cadun..pessoafisica pf on pf.cpf = s.cpf\n" +
                "\tleft join cadun..vwUnidadeGestora empresa on empresa.cnpj = q.CnpjEmpresa\n" +
                "\tleft join cadun..vwUnidadeGestora ug on ug.cnpj = s.UG\n" +
                "\tleft join valoresLiquidacoes vl on vl.idCredor = q.CnpjEmpresa and vl.idUnidadeGestora = s.UG\n" +
                "\tleft join valoresLiquidacoesGeral vlgeral on vlgeral.idCredor = q.CnpjEmpresa\n" +
                "\tleft join cadun..SituacaoCadastralPessoaJuridica sc on sc.Codigo =cast(empresa.SituacaoCadastral as int)\n" +
                "where q.CnpjEmpresa is not null\n" +
                "order by entidade asc"
        ).getResultList();
        return list;
    }

    public List<Object> buscaNomeCnpjUnidadeGestora() {
        List<Object> list = entityManager.createNativeQuery("select id , nome From UnidadeGestora  "
        ).getResultList();
        return list;
    }

    public List<Integer> buscaVigenciaUnidadeGestoraPorCnpj(String Cnpj, Integer Exercicio, Integer Remessa) {

        return entityManager.createNativeQuery("select 1 as resposta where EXISTS(" +
                "SELECT vug.id AS codvigencia, pj.codunidadegestora, vug.remessaini, vug.remessafim, vug.exercicioini, vug.exerciciofim " +
                "FROM cadun.dbo.VigenciaUnidadeGestora AS vug INNER JOIN cadun.dbo.vwUnidadeGestora AS pj ON vug.CodigoPessoaJuridica = pj.idPessoa " +
                "WHERE (vug.exercicioini = " + Exercicio + ") AND (vug.remessaini <= " + Remessa + ") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini = " + Exercicio + ") AND (vug.remessaini <= " + Remessa + ") AND (vug.exerciciofim = " + Exercicio + ") AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29) AND " +
                "(vug.remessafim >= " + Remessa + ") OR (vug.exercicioini = " + Exercicio + ") AND (vug.remessaini <= " + Remessa + ") AND (vug.exerciciofim > " + Exercicio + ") AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < " + Exercicio + ") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < " + Exercicio + ") AND (vug.exerciciofim = " + Exercicio + ") AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29) AND (vug.remessafim >= " + Remessa + " ) OR " +
                "(vug.exercicioini < " + Exercicio + ") AND (vug.exerciciofim > " + Exercicio + ") AND (pj.codunidadegestora = '" + Cnpj + "') AND (vug.idSistema = 29))").getResultList();
    }


    public PaginacaoUtil<UnidadeGestora> buscaPaginadaUnidadeGestora(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        if (searchParams.length() > 3) {

            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                search = " WHERE " + arrayOfStrings[0] + " LIKE  '%" + arrayOfStrings[1] + "%'  ";
            } else {//entra caso for um Integer
                search = " WHERE " + searchParams + "    ";
            }

        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<UnidadeGestora> list = getEntityManager()
                .createNativeQuery("select * from UnidadeGestora  " + search + "    ORDER BY " + campo, UnidadeGestora.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countUnidadeGestora();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        // System.out.println(list);
        return new PaginacaoUtil<UnidadeGestora>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }


    public long countUnidadeGestora() {
        return getEntityManager().createQuery("select count(*) from " + entityClass.getSimpleName(), Long.class).getSingleResult();
    }

    public UnidadeGestora buscarDadosUnidadeTeste() {
        List<UnidadeGestora> list = getEntityManager().createNativeQuery("select * from UnidadeGestora " +
                "where nome = 'Unidade de teste'", UnidadeGestora.class).getResultList();
        return list.get(0);
    }

    public Object buscarDadosUnidadeGestora(String cnpj) throws Exception {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "select pj.Divisao_id, up.idPessoaFisica, up.idPessoaJuridica " +
                    "from Cadun..PessoaJuridica pj " +
                    "         LEFT JOIN cadun..vwUnidadesPessoasCargos up " +
                    "                   on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                    "WHERE pj.CNPJ = '" + cnpj + "'" +
                    "   or pj.CodigoUnidadeGestora = '" + cnpj + "'");
            List<Object> result = buscarSQL(query, "pj.Divisao_id, up.idPessoaFisica, up.idPessoaJuridica");
            return result.get(0);
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new Exception("Erro inexperado na consulta 'buscarDadosUnidadeGestora'!");
        }
    }

    public Object buscarDadosFundoOuInstituto(String cnpj) throws Exception {
        try {
            Query query = getEntityManager().createNativeQuery("" +
                    "SELECT up.idPessoaFisica, up.idPessoaJuridica " +
                    "FROM Cadun..PessoaJuridica pj " +
                    "         LEFT JOIN cadun..vwUnidadesPessoasCargos up " +
                    "                   on pj.cnpj = up.codunidadegestora and idCargo = 4 and dataFim is null " +
                    "WHERE pj.CNPJ = '" + cnpj + "'" +
                    "   or pj.CodigoUnidadeGestora = '" + cnpj + "'" +
                    "    and pj.Divisao_id in (4, 12, 5)\n" +
                    "    and pj.RazaoSocial like '%prev%'");
            List<Object> result = buscarSQL(query, "up.idPessoaFisica, up.idPessoaJuridica");
            return result.get(0);
        } catch (NoResultException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            throw new Exception("Erro inexperado na consulta 'buscarDadosFundoOuInstituto'!");
        }
    }

    public List<UnidadeGestora> findAllWithConsesorios() {

            var result = getEntityManager().createNativeQuery(
                    "select distinct UG.*\n" +
                            "from AdmEnvio ad\n" +
                            "    join SICAPAP21.dbo.UnidadeGestora UG on UG.id = ad.unidadeGestora\n" +
                            "where UG.id <>'00000000000000'",UnidadeGestora.class).getResultList();
            return result;

    }

    public List<UnidadeGestora> findAllWithEnvioConcursos() {

        var result = getEntityManager().createNativeQuery(
                "select distinct UG.* \n" +
                        " from ConcursoEnvio env \n" +
                        " join Edital ed on env.idEdital= ed.id \n" +
                        " join InfoRemessa inf on ed.chave=inf.chave and inf.idUnidadeGestora <> '00000000000000'  \n" +
                        " join SICAPAP21.dbo.UnidadeGestora UG on UG.id = inf.idUnidadeGestora \n" ,UnidadeGestora.class).getResultList();
        return result;

    }

    public Boolean EhUnidadeGestoraRpps(){
        try {
            return ( getEntityManager().createNativeQuery("select  1 from UnidadeGestoraRpps where cnpjRpps=:cnpj  ").setParameter("cnpj", User.getUser(super.getRequest()).getUnidadeGestora().getId() ).getResultList().size()>0) ;
        }catch (RuntimeException e){
            return false;
        }
    }

}
