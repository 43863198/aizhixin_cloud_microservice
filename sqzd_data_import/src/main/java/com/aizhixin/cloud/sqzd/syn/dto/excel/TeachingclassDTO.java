package com.aizhixin.cloud.sqzd.syn.dto.excel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class TeachingclassDTO {
    @Getter @Setter private String skbj;//上课班级
    @Getter @Setter private String bjmc;//班级名称
    @Getter @Setter private String jszgh;  //教师
    @Getter @Setter private String jsxm;  //教师
    @Getter @Setter private String kcdm;  //课程代码
    @Getter @Setter private String kcmc;  //课程名称
    @Getter @Setter private String xn;    //学年
    @Getter @Setter private String xq;    //学期
}
