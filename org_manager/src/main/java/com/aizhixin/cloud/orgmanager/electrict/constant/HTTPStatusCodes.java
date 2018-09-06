package com.aizhixin.cloud.orgmanager.electrict.constant;

/**
 * HTTP Status Code
 * HTTP状态码
 * 可以通过继承使用该状态码
 *
 * Created by ShuHaoWong on 2015/11/5.
 */
public interface HTTPStatusCodes {
    /**
     * 请求已成功，请求所希望的响应头或数据体将随此响应返回。
     */
    public final static int OK = 200;
    /**
     * 请求已经被实现，而且有一个新的资源已经依据请求的需要而建立，且其 URI
     * 已经随Location 头信息返回。假如需要的资源无法及时建立的话，应当返回 '202 Accepted'。
     */
    public final static int CREATED = 201;
    /**
     * 服务器遇到了一个未曾预料的状况，导致了它无法完成对请求的处理。一般来说，
     * 这个问题都会在服务器端的源代码出现错误时出现。
     */
    public final static int INTERNAL_SERVER_ERR = 500;
    /**
     *当前请求需要用户验证。该响应必须包含一个适用于被请求资源的 WWW-Authenticate
     * 信息头用以询问用户信息。客户端可以重复提交一个包含恰当的 Authorization 头信息
     * 的请求。如果当前请求已经包含了 Authorization 证书，那么401响应代表着服务器验证
     * 已经拒绝了那些证书。如果401响应包含了与前一个响应相同的身份验证询问，且浏览器
     * 已经至少尝试了一次验证，那么浏览器应当向用户展示响应中包含的实体信息，因为这个
     * 实体信息中可能包含了相关诊断信息。参见RFC 2617。
     */
    public final static int UNAUTHORIZED= 401;
    /**
     * 请求失败，请求所希望得到的资源未被在服务器上发现。没有信息能够告诉用
     * 户这个状况到底是暂时的还是永久的。假如服务器知道情况的话，应当使用
     * 410状态码来告知旧资源因为某些内部的配置机制问题，已经永久的不可用，
     * 而且没有任何可以跳转的地址。404这个状态码被广泛应用于当服务器不想揭示
     * 到底为何请求被拒绝或者没有其他适合的响应可用的情况下。出现这个错误的
     * 最有可能的原因是服务器端没有这个页面。
     */
    public final static int NOT_FOUND = 404;
    /**
     * <p>
     *  请求的资源的内容特性无法满足请求头中的条件，因而无法生成响应实体。<br/><br/>
     *  除非这是一个 HEAD 请求，否则该响应就应当返回一个包含可以让用户或者
     *  浏览器从中选择最合适的实体特性以及地址列表的实体。实体的格式由
     *  Content-Type 头中定义的媒体类型决定。浏览器可以根据格式及自身能力自
     *  行作出最佳选择。但是，规范中并没有定义任何作出此类自动选择的标准。
     *  </p>
     */
    public final static int NOT_ACCEPTABLE = 406;
}
