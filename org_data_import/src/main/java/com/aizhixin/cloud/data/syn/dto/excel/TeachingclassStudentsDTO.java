package com.aizhixin.cloud.data.syn.dto.excel;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class TeachingclassStudentsDTO {
    @Getter @Setter private String skbj;//上课班级
    @Getter @Setter private Set<String> xh = new HashSet<>();  //学生学号

    public boolean containXh(String s) {
        return xh.contains(s);
    }

    public void addXh(String s) {
    	xh.add(s);
    }
}
