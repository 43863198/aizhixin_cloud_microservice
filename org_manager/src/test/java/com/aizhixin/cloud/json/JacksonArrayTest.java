package com.aizhixin.cloud.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JacksonArrayTest {
    public static void main(String[] args) {
        String json = "[{\"key\":\"test01\"},{\"key\":\"test02\"}]";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(json);
            if (null != rootNode) {
                int n = rootNode.size();
                for (int i = 0; i < n; i++) {
                    JsonNode sub = rootNode.get(i);
                    JsonNode keyNode = sub.path("key");
                    System.out.println(keyNode.asText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Test.class);
        try {
            List<Test> userList = mapper.readValue(json, listType);
            for (Test t : userList) {
                System.out.println(t.getKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Test> ts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ts.add(new Test("t" + i));
        }
        try {
            System.out.println(mapper.writeValueAsString(ts));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@NoArgsConstructor
@ToString
class Test {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Test (String key) {
        this.key = key;
    }
}