package com.example.sicapweb;

import com.example.sicapweb.repository.externo.AcompanhamentoRemessaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SicapwebApplicationTests {

    @Test
    void contextLoads() {
    }

//    @Test
//    public void getRegras() {
//        List<Map<String, Object>> li =  new AcompanhamentoRemessaRepository().buscarAcompanhamentoRemessa(2021, 1);
//
//        assertEquals (true, li != null && li.size() > 0);
//    }

}
