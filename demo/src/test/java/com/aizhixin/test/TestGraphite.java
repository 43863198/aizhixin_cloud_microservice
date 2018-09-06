package com.aizhixin.test;

import com.googlecode.jmxtrans.model.JmxProcess;
import com.googlecode.jmxtrans.model.Query;
import com.googlecode.jmxtrans.model.Server;
import com.googlecode.jmxtrans.model.output.GraphiteWriter;

public class TestGraphite {
//    private static JsonPrinter printer = new JsonPrinter(System.out);
    public static void main(String[] args) throws Exception {
        JsonPrinter printer = new JsonPrinter(System.out);
        printer.prettyPrint(new JmxProcess(Server.builder()
                .setHost("localhost")
                .setPort("64164")
                .addQuery(Query.builder()
                        .setObj("java.lang:type=GarbageCollector,name=ConcurrentMarkSweep")
                        .addOutputWriterFactory(GraphiteWriter.builder()
                                .setHost("172.16.23.110")
                                .setPort(2003)
                                .setDebugEnabled(true)
                                .setRootPrefix("jon.foo.bar")
                                .build())
                        .build())
                .build()));
    }
}
