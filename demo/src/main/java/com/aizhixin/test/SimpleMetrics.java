package com.aizhixin.test;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class SimpleMetrics {
    @Getter @Setter private String name;
    @Getter @Setter private long starttime;
    @Getter @Setter private long endtime;
    @Getter @Setter private long time;

    public SimpleMetrics (String name, long starttime) {
        this.name = name;
        this.starttime = starttime;
    }

    public void executeTime() {
        this.time = this.endtime - this.starttime;
    }
}
