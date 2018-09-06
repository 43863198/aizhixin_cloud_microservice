package com.aizhixin.cloud.apigateway.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.stereotype.Component;

@Component()
public class TomcatConfig extends TomcatEmbeddedServletContainerFactory
{
    public EmbeddedServletContainer getEmbeddedServletContainer(ServletContextInitializer... initializers)
    {
        return super.getEmbeddedServletContainer(initializers);
    }

    protected void customizeConnector(Connector connector)
    {
        super.customizeConnector(connector);
        Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();
        //设置最大连接数
        protocol.setMaxConnections(20000);
        //设置最大线程数
        protocol.setMaxThreads(1000);
        protocol.setConnectionTimeout(60000);
    }
}