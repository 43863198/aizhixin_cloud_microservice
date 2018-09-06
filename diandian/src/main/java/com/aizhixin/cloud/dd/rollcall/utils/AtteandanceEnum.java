package com.aizhixin.cloud.dd.rollcall.utils;

/**
 * Created by LIMH on 2017/9/16.
 */
public enum AtteandanceEnum {
    A("(已到 / 应到 ) * 100% ", 10),
    B("( （已到 + 请假） / 应到 ) * 100% ", 20),
    C("( （已到 + 迟到） / 应到 ) * 100% ", 30),
    D("( （已到 + 早退） / 应到 ) * 100% ", 40),
    E("( （已到 + 迟到 + 早退 ） / 应到 ) * 100% ", 50),
    f("( （已到 + 请假 + 迟到 + 早退） / 应到 ) * 100% ", 60);

    private String name;
    private int index;

    AtteandanceEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
