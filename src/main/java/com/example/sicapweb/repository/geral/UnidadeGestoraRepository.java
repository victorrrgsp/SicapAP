package com.example.sicapweb.repository.geral;
import br.gov.to.tce.model.UnidadeGestora;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

  public List<Object> buscaNomeCnpjUnidadeGestora() {
    List<Object> list = entityManager.createNativeQuery("select id , nome From UnidadeGestora  "
      ).getResultList();
    return list;
  }

    public List<Integer> buscaVigenciaUnidadeGestoraPorCnpj(String Cnpj, Integer Exercicio, Integer Remessa) {

        return entityManager.createNativeQuery("select 1 as resposta where EXISTS("+
                "SELECT vug.id AS codvigencia, pj.codunidadegestora, vug.remessaini, vug.remessafim, vug.exercicioini, vug.exerciciofim " +
                "FROM cadun.dbo.VigenciaUnidadeGestora AS vug INNER JOIN cadun.dbo.vwUnidadeGestora AS pj ON vug.CodigoPessoaJuridica = pj.idPessoa " +
                "WHERE (vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '"+ Cnpj +"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim = "+Exercicio+") AND (pj.codunidadegestora = '"+ Cnpj +"') AND (vug.idSistema = 29) AND " +
                "(vug.remessafim >= "+Remessa+") OR (vug.exercicioini = "+Exercicio+") AND (vug.remessaini <= "+Remessa+") AND (vug.exerciciofim > "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim IS NULL) AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim = "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29) AND (vug.remessafim >= "+Remessa+" ) OR " +
                "(vug.exercicioini < "+Exercicio+") AND (vug.exerciciofim > "+Exercicio+") AND (pj.codunidadegestora = '"+Cnpj+"') AND (vug.idSistema = 29))").getResultList();
    }


    public PaginacaoUtil<UnidadeGestora> buscaPaginadaUnidadeGestora(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search= "";

        //monta pesquisa search
        if(searchParams.length() > 3){

            if(tipoParams==0){ //entra para tratar a string
                String arrayOfStrings[]  = searchParams.split("=");
                search = " WHERE " +arrayOfStrings[0] + " LIKE  '%"+arrayOfStrings[1]+"%'  "  ;
            }

            else{//entra caso for um Integer
                search = " WHERE " + searchParams + "    " ;
            }

        }

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<UnidadeGestora> list = getEntityManager()
                .createNativeQuery("select * from UnidadeGestora  " +search+"    ORDER BY " + campo, UnidadeGestora.class)
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


}
