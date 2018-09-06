package com.aizhixin.cloud.cqaq.syn.dto.excel;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class TeachingclassClassesDTO {
    @Getter @Setter private String skbj;//上课班级
    @Getter @Setter private Set<String> bjdm = new HashSet<>();  //班级名称

    public boolean containBj(String bj) {
        return bjdm.contains(bj);
    }

    public void addBj(String bj) {
        bjdm.add(bj);
    }
}
