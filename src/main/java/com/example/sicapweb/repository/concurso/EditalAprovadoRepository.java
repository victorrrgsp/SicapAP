package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.UnidadeGestora;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EditalAprovado;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.model.EditalAprovadoConcurso;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Repository
public class EditalAprovadoRepository extends DefaultRepository<EditalAprovado, BigInteger> {
    public EditalAprovadoRepository(EntityManager em) {
        super(em);
    }



    public PaginacaoUtil<EditalAprovadoConcurso> buscaPaginadaAprovados(Pageable pageable) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<EditalAprovado> list = getEntityManager()
                .createNativeQuery("select distinct a.* from EditalAprovado  a" +
                        " join  EditalVaga b on a.idEditalVaga = b.id" +
                        "    join InfoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' " +
                        "     left join   Servidor se on a.cpf = se.cpfServidor" +
                        "    left join Admissao ad on se.id = ad.idServidor and   b.idCargo = ad.idCargo  where  i.idUnidadeGestora = '" + User.getUser(super.request).getUnidadeGestora().getId() + "' "  + " ORDER BY " + campo, EditalAprovado.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = countAprovados();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalAprovadoConcurso> listc= new ArrayList<EditalAprovadoConcurso>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalAprovadoConcurso editalAprovadoConcurso = new EditalAprovadoConcurso();
            editalAprovadoConcurso.setEditalVaga(list.get(i).getEditalVaga());
            editalAprovadoConcurso.setNome(list.get(i).getNome());
            editalAprovadoConcurso.setNumeroEdital(list.get(i).getNumeroEdital());
            editalAprovadoConcurso.setClassificacao(list.get(i).getClassificacao());
            editalAprovadoConcurso.setCodigoVaga(list.get(i).getCodigoVaga());
            editalAprovadoConcurso.setCpf(list.get(i).getCpf());
            editalAprovadoConcurso.setEditalaprovado(list.get(i));
            Integer enviado = (Integer) getEntityManager().createNativeQuery("select count(*) from DocumentoAdmissao a " +
                    "where  a.idAprovado = "+list.get(i).getId()+ "").getSingleResult();
            if (enviado > 0 ){
                editalAprovadoConcurso.setSituacao("Aprovado Anexado");
            }
            else
            {
                editalAprovadoConcurso.setSituacao("Apto para Envio!");
            }

            listc.add(editalAprovadoConcurso);
        }
        return new PaginacaoUtil<EditalAprovadoConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }

    public Integer countAprovados() {
        Query query = getEntityManager().createNativeQuery("select count(*) from EditalAprovado a " +
                "join InfoRemessa i on a.chave = i.chave " +
                "where not exists(select 1 from Admissao ad  where ad.numeroInscricao=a.numeroInscricao)  and  i.idUnidadeGestora= '"+ User.getUser(super.request).getUnidadeGestora().getId()+ "'");
        return (Integer) query.getSingleResult();
    }


    public EditalAprovado buscarAprovadoPorCpf(String cpf) {

        var query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT c.* "+
                        "FROM EditalAprovado c "+
                        " join InfoRemessa  i on c.chave=i.chave " +
                        " WHERE i.idUnidadeGestora = '"+User.getUser(super.request).getUnidadeGestora().getId()+ "'  and  c.cpf = '"+cpf+"' "
                        ,EditalAprovado.class);
        List<EditalAprovado> list = query.getResultList();
        if (list.size()>0 ){
            return list.get(0);
        }
        else{
            return null;
        }
    }

    public EditalAprovado buscarAprovadoPorInscricao(String inscricao) {

        var query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT c.* "+
                        "FROM EditalAprovado c "+
                        " join InfoRemessa  i on c.chave=i.chave " +
                        " WHERE i.idUnidadeGestora = '"+User.getUser(super.request).getUnidadeGestora().getId()+ "'  and  c.numeroInscricao = '"+inscricao+"' "
                ,EditalAprovado.class);
        List<EditalAprovado> list = query.getResultList();
        if (list.size()>0 ){
            return list.get(0);
        }
        else{
            return null;
        }
    }

    public EditalAprovado buscarAprovadoPorClassificacaoConc(BigInteger idvaga, String Classificacao  ) {

        var query = getEntityManager().createNativeQuery(
                "SELECT DISTINCT c.* "+
                        "FROM EditalAprovado c "+
                        " join InfoRemessa  i on c.chave=i.chave " +
                        " WHERE idEditalVaga= "+idvaga+" and  c.classificacao =  '"+Classificacao+"' and i.idUnidadeGestora = '"+User.getUser(super.request).getUnidadeGestora().getId()+ "'   "
                ,EditalAprovado.class);
        List<EditalAprovado> list = query.getResultList();
        if (list.size()>0 ){
            return list.get(0);
        }
        else{
            return null;
        }
    }


    public List<Map<String,Object>> buscarInfoReciboAdmissao(Integer procnumero, Integer procano) {

        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery(

                    "select p.nome NomeResponsavel,p.cpf CpfResponsavel,a.data_assinatura DataAssinatura, null NumeroEdital, i.nomeUnidade , i.idUnidadeGestora , pj.nomeMunicipio  , ed.nome , ed.cpf from " +
                            " AdmissaoEnvioAssinatura a" +
                            "  inner join SICAPAP21..ProcessoAdmissao env on a.idProcesso= env.id" +
                            "  inner join SICAPAP21..DocumentoAdmissao docenv on env.id= docenv.idProcessoAdmissao" +
                            "  inner join SICAPAP21..EditalAprovado ed on docenv.idAprovado = ed.id " +
                            "  inner join cadun..vwPessoa p on a.cpf=p.cpf " +
                            "  inner join InfoRemessa i on ed.chave=i.chave  and env.cnpjEmpresaOrganizadora = i.idUnidadeGestora " +
                            " inner join cadun..vwPessoaJuridica pj on i.idUnidadeGestora=pj.cnpj"+
                            " where env.processo='"+procnumero+"/"+procano+ "'").getResultList();


            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("NomeResponsavel", (String) obj[0]);
                mapa.put("CpfResponsavel", (String) obj[1]);
                mapa.put("DataAssinatura", (Date) obj[2]);
                mapa.put("NumeroEdital", (String) obj[3]);
                mapa.put("nomeUnidade", (String) obj[4]);
                mapa.put("idUnidadeGestora", (String) obj[5]);
                mapa.put("nomeMunicipio", (String) obj[6]);
                mapa.put("nomeInteressado", (String) obj[7]);
                mapa.put("cpfInteressado", (String) obj[8]);
                retorno.add(mapa);

            }
            ;

            return retorno;


        } catch (Exception e) {
            return null;
        }
    }

}
