# aizhixin cloud base on spring cloud

--
#### 系统环境要求：
    spring cloud 采用Camden版本
    spring boot 采用1.4.5版本
    Java SDK java8及以上
#### 编译、打包命令(采用Maven的方式)
    mvn compile
    mvn package
#### 部署方式：
    嵌入tomcat的方式
#### 子工程简介
    discovery_enreka_server 服务的注册发现中心，采用Netflix的eureka作为微服务的注册发现中心
    config_server   采用Spring Cloud Config，git作为配置管理存放地，kafka作为数据总线
    api_gateway     采用Netflix的zuul为API Gateway
    org_manager     组织结构等学校基础数据的操作API
    service_admin   监控服务，监控所有应用的健康状况及运行指标
    hystrix_dashboard   API运行状况实时监控
    turbine 多服务监控状态集成
    school_manager  新的学校内容管理API，目前主要用于学校定制化首页
    diandian    点点后台API，目前主要用于点点相关功能
    token-auth  内部服务使用身份认证功能
    io  文档服务，目前主要用于开卷office文档处理
    
#### 项目依赖
    discovery_enreka_server是所有服务的基础，必须第一个启动；其次必须具有高可用部署方式，最少部署两个节点。
    config_server所有其他其他服务的配置管理中心，其他所有服务都会通过这个服务获取配置信息，因而是第二个启动的，注意这个服务需要配置git、zookeeper和kafka的相关参数。依赖于服务的发现中心。
    api_gateway对外提供API的调用，将API调用转化为实际业务服务的访问。依赖于配置管理中心服务和服务的发现中心。
    org_manager业务API提供服务，API的实际提供者，但不直接对外提供API。依赖apiGateway、配置管理中心服务和服务的发现中心。
    service_admin公共监控服务，监控所有应用的健康状况及运行指标
    hystrix_dashboard服务API运行状况实时监控
    turbine多服务监控状态集成
    school_manager  新的学校内容管理API
    diandian    点点后台API
    token-auth  内部应用APP的权限校验及token生产校验
    io  文档服务，目前主要用于开卷office文档处理
    
#### 项目的启动
    java -D系统参数 -jar 子项目jar包.jar --spring.profiles.active=dev --server.port=端口 &
    最好后台启动，可以使用如上的java命令，也可以使用shell脚本。
    
#### 启动顺序及具体命令
    在开发环境，发现服务部署在三台机器情况，120(peer1)、121(peer2)、123（peer3）。
    120的启动命令: > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -Dpeer2Ip=172.16.23.121 discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1 --server.port=1111 &
    121的启动命令: > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -Dpeer3Ip=172.16.23.122 discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2 --server.port=1111 &
    122的启动命令: > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -Dpeer1Ip=172.16.23.120 discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer3 --server.port=1111 &
    
    发现服务部署在两台机器的情况，120(peer1)、121(peer2)
    120的启动命令: > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -Dpeer2Ip=172.16.23.121 discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1 --server.port=1111 &
    121的启动命令: > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -Dpeer3Ip=172.16.23.120 discovery_eureka_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2 --server.port=1111 &
    
    后一个启动的节点日志里边没有出现错误一般就是启动成功了，否则可能启动失败
    启动zookeeper和kafka，暂时使用单节点即可，进入bin同级目录后执行如下命令：
    > ./bin/zookeeper-server-start.sh config/zookeeper.properties &
    > ./bin/kafka-server-start.sh config/server.properties &
    启动配置中心命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -jar config_server-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=2222 &
    启动api_gateway命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -jar api_gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev  --server.port=3333 &
    启动org_manager命令：
    > java -XX:MaxMetaspaceSize=512m -Xms512m -Xmx2048m -DconfigServerIp=http://172.16.23.120:2222 -jar org_manager-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8007  &
    启动service_admin命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -DconfigServerIp=http://172.16.23.120:2222 -jar service_admin-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=4444  &
    启动school_manager命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -DconfigServerIp=http://172.16.23.120:2222 -jar school_manager-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=xxxx  &
    启动hystrix_dashboard命令：
    > java -jar hystrix_dashboard-0.0.1-SNAPSHOT.jar --server.port=5555  &
    启动turbine命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -DconfigServerIp=http://172.16.23.120:2222 -jar turbine-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=6666  &
    启动token-auth命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -DconfigServerIp=http://172.16.23.120:2222 -jar token_auth-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8003  &
    启动io命令：
    > java -XX:MaxMetaspaceSize=256m -Xms512m -Xmx1024m -DconfigServerIp=http://172.16.23.120:2222 -jar io-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=8010  &
    
    