# aizhixin cloud Config Center

--
#### 系统环境要求：
    spring cloud 采用Camden版本
    spring boot 采用1.4.5版本
    Java SDK java8及以上
#### 部署方式：
    嵌入tomcat的方式
#### 编译、打包及运行
    mvn compile
    mvn package
    java -jar config_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=2222
#### 配置文件含义
    spring:
      cloud:
        config:
          server:
            git:
              uri: http://172.16.23.58/panzhen/cloud_config_server.git
              search-paths: config-repo
              username: zhen.pan@dinglicom.com
              password: pz121206
          discovery:
            enabled: true
            service-id: config-server
          fail-fast: true
        stream:
          kafka:
            binder:
              zk-nodes: 172.16.23.108:2181
              brokers: 172.16.23.108:9092
          
    其中：git的子项表示访问git的地址、路径、用户名、密码
        stream 子项表示zookeeper和kafka的访问地址