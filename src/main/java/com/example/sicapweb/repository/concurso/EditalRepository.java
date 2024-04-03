package com.example.sicapweb.repository.concurso;

import br.gov.to.tce.model.ap.concurso.ConcursoEnvio;
import br.gov.to.tce.model.ap.concurso.Edital;
import br.gov.to.tce.model.ap.concurso.EmpresaOrganizadora;
import com.example.sicapweb.model.EditalFinalizado;
import com.example.sicapweb.model.EditalConcurso;
import com.example.sicapweb.repository.DefaultRepository;
import com.example.sicapweb.security.User;
import com.example.sicapweb.util.PaginacaoUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class EditalRepository extends DefaultRepository<Edital, BigInteger> {


    public EditalRepository(EntityManager em) {
        super(em);
    }


    public String getSearch(String searchParams, Integer tipoParams)  {
        String search = "";
        //monta pesquisa search
        if (searchParams.length() > 3) {
            if (tipoParams == 0) { //entra para tratar a string
                String arrayOfStrings[] = searchParams.split("=");
                if (arrayOfStrings[0].equals("numeroEdital"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("veiculoPublicacao"))
                    search = " and a." + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
                else if (arrayOfStrings[0].equals("dataPublicacao")) {
                    LocalDate data = LocalDate.parse((arrayOfStrings[1] ), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    search = " and a." + arrayOfStrings[0] + " = '" + data.format(DateTimeFormatter.ofPattern("d/MM/uuuu"))+ "'  ";
                }
                else
                    search = " and " + arrayOfStrings[0] + " LIKE '%" + arrayOfStrings[1] + "%'  ";
            } else {
                search = " and " + searchParams + "   ";
            }
        }
        return search;
    }
    public PaginacaoUtil<EditalConcurso> buscaPaginadaEditais(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);
        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");


        List<Edital> list = getEntityManager()
                .createNativeQuery("with edt as ( " +
                        "        select dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id,max(a.id)  max_id " +
                        "             from Edital a   join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+user.getUser(super.request).getUnidadeGestora().getId()+"' left  join ConcursoEnvio c on a.id = c.idEdital and c.fase=1     group by " +
                        "                dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id " +
                        "                         ) " +
                        "select   a.* from Edital a join edt b on a.id= b.max_id where 1=1 " + search + " ORDER BY " + campo, Edital.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();
        long totalRegistros = this.countEditais(search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalConcurso> listc= new ArrayList<EditalConcurso>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalConcurso editalConcurso = new EditalConcurso();
            editalConcurso.setId(list.get(i).getId());
            editalConcurso.setTipoEdital(list.get(i).getTipoEdital());
            editalConcurso.setNumeroEdital(list.get(i).getNumeroEdital());
            editalConcurso.setComplementoNumero(list.get(i).getComplementoNumero());
            editalConcurso.setDataPublicacao(list.get(i).getDataPublicacao());
            editalConcurso.setDataInicioInscricoes(list.get(i).getDataInicioInscricoes());
            editalConcurso.setDataFimInscricoes(list.get(i).getDataFimInscricoes());
            editalConcurso.setPrazoValidade(list.get(i).getPrazoValidade());
            editalConcurso.setVeiculoPublicacao(list.get(i).getVeiculoPublicacao());
            editalConcurso.setCnpjEmpresaOrganizadora(list.get(i).getCnpjEmpresaOrganizadora());
           if  ( list.get(i).getCnpjEmpresaOrganizadora()!=null ) {
                   List<EmpresaOrganizadora> leo =  getEntityManager()
                           .createNativeQuery("select a.* from empresaOrganizadora a  join infoRemessa i on a.chave=i.chave and i.idUnidadeGestora = :ug  where  cnpjEmpresaOrganizadora='" + list.get(i).getCnpjEmpresaOrganizadora() + "'", EmpresaOrganizadora.class)
                           .setParameter("ug",user.getUser(super.getRequest()).getUnidadeGestora().getId() )
                           .getResultList();

                   if (leo.size()>0){
                       EmpresaOrganizadora eo =(EmpresaOrganizadora) leo.get(0);
                   editalConcurso.setNomEmpresaOrganizadora(eo.getNome());
                   editalConcurso.setValorContratacao(eo.getValor());
                   }

           }
            listc.add(editalConcurso);
        }
        return new PaginacaoUtil<EditalConcurso>(tamanho, pagina, totalPaginas, totalRegistros, listc);
    }


    public PaginacaoUtil<Edital> buscaPaginada(Pageable pageable, String searchParams, Integer tipoParams) {

        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";

        //monta pesquisa search
        search = getSearch(searchParams, tipoParams);

        //retirar os : do Sort pageable
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Edital> list = getEntityManager()
                .createNativeQuery("with edt as ( " +
                        "        select dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id,max(a.id)  max_id " +
                        "             from Edital a   join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+user.getUser(super.request).getUnidadeGestora().getId()+"' left  join ConcursoEnvio c on a.id = c.idEdital  and c.fase=1   group by " +
                        "                dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id " +
                        "                         ) " +
                        "select   a.* from Edital a join edt b on a.id= b.max_id where 1=1 " + search + " ORDER BY " + campo, Edital.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = countEditais( search);
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;

        return new PaginacaoUtil<Edital>(tamanho, pagina, totalPaginas, totalRegistros, list);
    }

    public Integer countEditais(String search) {
        return (Integer) getEntityManager().createNativeQuery("with edt as (\n" +
                "        select dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id,max(a.id)  max_id\n" +
                "             from Edital a  join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+user.getUser(super.request).getUnidadeGestora().getId()+"'  left  join ConcursoEnvio c on a.id = c.idEdital  and c.fase=1 group by\n" +
                "                dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id " +
                "                         )\n" +
                "select   count(1) from Edital a join edt b on a.id= b.max_id where 1=1 " + search).getSingleResult();
    }
    public Edital buscarEditalPorNumero(String numeroEdital,String complementoNumero) {
        String vcomplemento = (complementoNumero == null || complementoNumero.isEmpty()) ? "001" : complementoNumero;
        try {
            return (Edital) getEntityManager().createNativeQuery(
                    " with edt as (\n" +
                            "        select dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,max(id)  max_id\n" +
                            "             from Edital a  join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "' group by\n" +
                            "                dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora\n" +
                            "                         )\n" +
                            "select a.* from Edital a join edt b on a.id= b.max_id where  a.numeroEdital='" + numeroEdital + "' and  isnull(a.complementoNumero,'001') =  '" + vcomplemento + "'", Edital.class).setMaxResults(1).getSingleResult();
        }catch (NoResultException e){
            return null;
        }

    }

    public List<Edital> buscarEditaisNaoHomologados() {
        return getEntityManager().createNativeQuery("select ed.* from Edital ed where " +
                " not exists(select 1 from EditalHomologacao  eh where ed.id = eh.idEdital   )", Edital.class).getResultList();
    }



    public PaginacaoUtil<EditalFinalizado> buscarEditaiFinalizados(Pageable pageable, String searchParams, Integer tipoParams) {
        int pagina = Integer.valueOf(pageable.getPageNumber());
        int tamanho = Integer.valueOf(pageable.getPageSize());
        String search = "";
        search = getSearch(searchParams, tipoParams);
        String campo = String.valueOf(pageable.getSort()).replace(":", "");

        List<Edital> list = getEntityManager()
                .createNativeQuery("select a.* from Edital a " +
                        "join InfoRemessa i on a.chave = i.chave " +
                        "join ConcursoEnvio e on a.id = e.idEdital and e.fase=1 and e.Status in (2,4)  " +
                        "where i.idUnidadeGestora = '" + user.getUser(super.request).getUnidadeGestora().getId() + "' " + search + " ORDER BY " + campo, Edital.class)
                .setFirstResult(pagina)
                .setMaxResults(tamanho)
                .getResultList();

        long totalRegistros = list.size();
        long totalPaginas = (totalRegistros + (tamanho - 1)) / tamanho;
        List<EditalFinalizado> listaDeEditaisFinalisados= new ArrayList<EditalFinalizado>() ;
        for(Integer i= 0; i < list.size(); i++){
            EditalFinalizado editalFinalizado =new EditalFinalizado();
            editalFinalizado.setNumeroEdital(list.get(i).getNumeroEdital());
            editalFinalizado.setData(list.get(i).getDataPublicacao());
            editalFinalizado.setEdital((Edital)list.get(i));
            List<ConcursoEnvio> listaDeEnviosEdital =   getEntityManager()
                    .createNativeQuery("select E.* from ConcursoEnvio E where  e.fase=1 and e.Status in (2,4) and  idEdital=" + list.get(i).getId() + "", ConcursoEnvio.class).setMaxResults(1) .getResultList();
            editalFinalizado.setProcesso((listaDeEnviosEdital.size()>0) ? listaDeEnviosEdital.get(0).getProcesso() : null );
            listaDeEditaisFinalisados.add(editalFinalizado);
        }
        return new PaginacaoUtil<>(tamanho, pagina, totalPaginas, totalRegistros, listaDeEditaisFinalisados);
    }


    public List<Map<String,Object>> buscarInfoReciboEdital(Integer procnumero,Integer procano) {

        List<Map<String, Object>> retorno = new ArrayList<Map<String, Object>>();

        try {

            List<Object[]> list = entityManager.createNativeQuery(
                    "select p.nome NomeResponsavel,p.cpf CpfResponsavel,a.data_assinatura DataAssinatura, ed.numeroEdital NumeroEdital, i.nomeUnidade , i.idUnidadeGestora , pj.nomeMunicipio from " +
                            "     ConcursoEnvioAssinatura a " +
                            "     inner join ConcursoEnvio env on a.idEnvio= env.id " +
                            "     inner join Edital ed on ed.id = env.idEdital " +
                            "inner join cadun..vwPessoa p on a.cpf=p.cpf " +
                            "     inner join InfoRemessa i on ed.chave=i.chave  " +
                            " inner join cadun..vwPessoaJuridica pj on i.idUnidadeGestora=pj.cnpj" +
                            "" +
                    " where env.processo='"+procnumero+"/"+procano+ "'").getResultList();


            for (Object[] obj : list) {

                Map<String, Object> mapa = new HashMap<String, Object>();

                mapa.put("NomeResponsavel", (String) obj[0]);
                mapa.put("CpfResponsavel", (String) obj[1]);
                mapa.put("DataAssinatura",  (String) obj[2]);
                mapa.put("NumeroEdital", (String) obj[3]);
                mapa.put("nomeUnidade", (String) obj[4]);
                mapa.put("idUnidadeGestora", (String) obj[5]);
                mapa.put("nomeMunicipio", (String) obj[6]);
                retorno.add(mapa);

            }
            ;

            return retorno;


        } catch (Exception e) {
            return null;
        }
    }


    public Integer GetQuantidadePorNumeroEdital(String numeroEdital,String complemento) {

        return (Integer) getEntityManager().createNativeQuery("with edt as ( " +
                "        select dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id,max(a.id)  max_id " +
                "             from Edital a  join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+user.getUser(super.request).getUnidadeGestora().getId()+"' " +
                "  and a.numeroEdital ='"+ numeroEdital+"'  and  isnull(a.complementoNumero,'001') = '"+ ((complemento==null|| complemento.isEmpty() ) ? "001" : complemento)+"'   left  join ConcursoEnvio c on a.id = c.idEdital and c.fase=1  group by " +
                "                dataPublicacao,dataInicioInscricoes,dataFimInscricoes,numeroEdital,complementoNumero,prazoValidade,veiculoPublicacao, cnpjEmpresaOrganizadora,c.id " +
                "                         ) " +
                "select  count(1)  from Edital a join edt b on a.id= b.max_id where 1=1 " )
                .getSingleResult();
    }

    public List<Edital> findAllAHomologar() {
        var query = getEntityManager().createNativeQuery(
                "with edt as (select dataPublicacao,\n" +
                        "                    dataInicioInscricoes,\n" +
                        "                    dataFimInscricoes,\n" +
                        "                    numeroEdital,\n" +
                        "                    complementoNumero,\n" +
                        "                    prazoValidade,\n" +
                        "                    veiculoPublicacao,\n" +
                        "                    cnpjEmpresaOrganizadora,\n" +
                        "                    c.id as concursoID,\n" +
                        "                    max(a.id) max_id\n" +
                        "             from Edital a\n" +
                        "                      left join ConcursoEnvio c on a.id = c.idEdital and c.fase = 1\n" +
                        "                      join infoRemessa i on a.chave = i.chave and i.idUnidadeGestora = '"+user.getUser(super.request).getUnidadeGestora().getId()+"'\n" +
                        "             group by dataPublicacao, dataInicioInscricoes, dataFimInscricoes, numeroEdital, complementoNumero,\n" +
                        "                      prazoValidade, veiculoPublicacao, cnpjEmpresaOrganizadora, c.id)\n" +
                        "select a.*\n" +
                        "from Edital a\n" +
                        "         join edt b on a.id = b.max_id and (b.concursoID is not null or a.tipoEdital =2)", Edital.class);
        return query.getResultList();
    }
}
