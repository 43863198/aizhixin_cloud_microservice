package com.aizhixin.cloud.org_manager.company;

import com.aizhixin.cloud.orgmanager.company.domain.BatchAddUserDomain;
import com.aizhixin.cloud.orgmanager.company.domain.excel.BatchAddUserResultDomain;
import com.aizhixin.cloud.rest.RestBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestZhixinApiBatchOperator extends RestBase {
    private String url = "http://dledudev.aizhixin.com/zhixin_api";//直接调用
    @Before
    public void init() {
        super.init();
    }

    @Test
    public void batchQuery() throws IOException {
        List<String>  list = new ArrayList<>();
        list.add("sccj1615050117");
        list.add("sccj1511060141");
        list.add("sccj1603010341");
        list.add("sccj1603010735");
        list.add("sccj1603010536");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(list);
        System.out.println(json);
        postBody(url + "/api/account/batchquery", json);
    }

    @Test
    public void batchAdd() throws IOException {
        List<BatchAddUserDomain>  list = new ArrayList<>();
        list.add(new BatchAddUserDomain ("pztest001", "老潘1", "B"));
        list.add(new BatchAddUserDomain ("pztest002", "老潘2", "B"));
        list.add(new BatchAddUserDomain ("pztest003", "老潘3", "B"));
        list.add(new BatchAddUserDomain ("pztest004", "老潘4", "B"));
        list.add(new BatchAddUserDomain ("pztest005", "老潘5", "B"));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(list);
        System.out.println(json);
        json = postBody(url + "/api/account/batchadd", json);
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, BatchAddUserResultDomain.class);
        List<BatchAddUserResultDomain> userList = mapper.readValue(json, listType);
        for (BatchAddUserResultDomain b : userList) {
            System.out.println(b);
        }
    }

    @Test
    public void noLoginBingdingPhoneAndSetPassword() throws IOException {
        String inJson = "";
        String outJson = putBody("http://dledu.aizhixindev.com/zhixin_api/api/web/v1/users/nologinsendbindingphonecode?id=1&phone=18681861821", inJson);
    }
}
