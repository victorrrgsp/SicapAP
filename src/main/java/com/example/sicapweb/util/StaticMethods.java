package com.example.sicapweb.util;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.Query;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public  class StaticMethods {

    // metodo usado para transformar o resultado de uma query de list<Objects[]> para List<Hashmap<String,Object>>, util para retornar uma tabela dinamica para o front
    public static List<HashMap<String,Object>> getMapListObjectToHashmap(Query query) {
        return   ( (NativeQueryImpl) query
        ).setResultTransformer(new ResultTransformer(){
                    @Override
                    public Object transformTuple(Object[] tuples, String[] aliases) {
                        Map result = new LinkedHashMap(tuples.length);

                        for (int i = 0; i < tuples.length; ++i) {
                            String alias = aliases[i];
                            if (alias != null) {
                                result.put(alias, tuples[i]);
                            }
                        }

                        return result;
                    }
                    @Override
                    public List transformList(List list) {
                        return list;
                    }
                })
                .getResultList();
    }
}
