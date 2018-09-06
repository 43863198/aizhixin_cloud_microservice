package com.aizhixin.cloud.data.syn.dto.excel;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class TeachingclassDTO {
    @Getter @Setter private String skbj;//上课班级
//    @Getter @Setter private String bjmc;//班级名称
//    @Getter @Setter private String jszgh;  //教师工号
//    @Getter @Setter private String jsxm;  //教师
    @Getter @Setter private String kcdm;  //课程代码
    @Getter @Setter private String kcmc;  //课程名称
    @Getter @Setter private String xn;    //学年
    @Getter @Setter private String xq;    //学期
    @Getter @Setter private Set<String> jszgh = new HashSet<>();  //教师工号

    public boolean containJs(String js) {
        return jszgh.contains(js);
    }

    public void addJs(String js) {
    	jszgh.add(js);
    }
}
