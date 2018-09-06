# aizhixin cloud API Gateway

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
    java -jar api_gateway-0.0.1-SNAPSHOT.jar  --server.port=3333
#### 配置文件含义
    spring:
      application:
        name: api-gateway
      cloud:
        config:
          profile: dev
          label: master
          uri: http://172.16.23.108:2222
          
    其中：uri表示集中配置服务的范围地址，lable表示git的分支，name表示配置文件的前缀部分（也可以是一级目录），profile表示后缀（也可以是二级目录）
    上边的配置可以在git里匹配api-gateway-dev.yml(properties)类似这样的配置文件