package com.aizhixin.cloud.paycallback.service;

import com.aizhixin.cloud.paycallback.common.core.PageUtil;
import com.aizhixin.cloud.paycallback.common.service.AESCryptor;
import com.aizhixin.cloud.paycallback.common.util.JsonUtil;
import com.aizhixin.cloud.paycallback.core.AliFailCode;
import com.aizhixin.cloud.paycallback.core.PaymentState;
import com.aizhixin.cloud.paycallback.core.PublishState;
import com.aizhixin.cloud.paycallback.domain.FeeUserInfoDomain;
import com.aizhixin.cloud.paycallback.domain.third.*;
import com.aizhixin.cloud.paycallback.entity.PaymentOrder;
import com.aizhixin.cloud.paycallback.entity.PaymentOrderItem;
import com.aizhixin.cloud.paycallback.entity.PersonalCost;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 第三方支付相关业务处理
 * 支付宝缴费大厅学生信息查询、缴费明细查询、缴费结果反馈业务逻辑处理
 */
@Component
@Transactional
public class ThirdPayService {
    private final static Logger LOG = LoggerFactory.getLogger(ThirdPayService.class);
    @Autowired
    private PersonalCostService personalCostService;
    @Autowired
    private PaymentOrderService paymentOrderService;
    @Autowired
    private PaymentOrderItemService paymentOrderItemService;
    @Autowired
    private AESCryptor aesCryptor;

    @Value("${security.id}")
    private String appid;
    @Value("${security.sign_key}")
    private String signKey;

    private boolean validateInputParam(InputDomain inputDomain) {
        if (null == inputDomain) {
            LOG.warn("没有入参");
            return false;
//            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "无入参", signKey);
        }
        if (StringUtils.isEmpty(inputDomain.getId()) || StringUtils.isEmpty(inputDomain.getParam()) || StringUtils.isEmpty(inputDomain.getSign())) {
            LOG.warn("入参有空值,id:({}), param:({}), sign:({})", inputDomain.getId(), inputDomain.getParam(), inputDomain.getSign());
//            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "缺少必须参数值", signKey);
            return false;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(inputDomain.getId()).append(inputDomain.getParam()).append(signKey);
        String sign = DigestUtils.md5Hex(sb.toString());
        if (sign.equalsIgnoreCase(inputDomain.getSign())) {
            return true;
        } else {
            LOG.warn("MD5值比对失败.传入值({}), 计算值({})", inputDomain.getSign(), sign);
        }
        return false;
    }

    /**
     * 支付宝交易大厅查询缴费学生的信息
     * @param inputDomain   查询加密签名对象
     * @return  查询结果
     */
    @Transactional(readOnly = true)
    public ReturenDomain findPayStudentInfo(InputDomain inputDomain) {
        if(!validateInputParam(inputDomain)) {
            LOG.warn("缴费学生的信息查询验证入参失败");
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "验证入参失败", signKey);
        }

        String json = aesCryptor.decrypt(inputDomain.getParam());
        if (null == json) {
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "缺少查询必须参数值", signKey);
        }
        LOG.debug("缴费学生的信息查询接口参数解密JSON结果：({})", json);
        String user_no = null;
        String key = null;

        ReturenDomain r = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            if (root.has("user_no")) {
                user_no = root.get("user_no").asText();
            }
            if (root.has("key")) {
                key = root.get("key").asText();
            }
            LOG.info("缴费学生的信息查询解析到正确的查询参数user_no({}), key({})", user_no, key);

            FeeUserInfoDomain d = personalCostService.findUserInfoHasPay(null, user_no, key);
            if (null == d) {
                return new ReturenDomain (AliFailCode.OTHER_FAIL.getStateDesc(), "未找到学生信息", signKey);
            }
            json = mapper.writeValueAsString(d);
            LOG.info("缴费学生的信息查询，输出明文数据：({})", json);
            r = new ReturenDomain(AliFailCode.NORMAL.getStateDesc(), aesCryptor.encrypt(json), signKey);
        } catch (Exception e) {
            LOG.warn("缴费学生的信息查询解析解密后的JSON出错:{}", e);
            r = new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "解析解密后的JSON出错", signKey);
        }
        return r;
    }

    /**
     * 查询学生的缴费科目
     * @param inputDomain   查询加密签名对象
     * @return  查询结果
     */
    @Transactional(readOnly = true)
    public ReturenDomain findPaySubject(InputDomain inputDomain) {
        if(!validateInputParam(inputDomain)) {
            LOG.warn("查询学生缴费科目验证入参失败");
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "验证入参失败", signKey);
        }

        String json = aesCryptor.decrypt(inputDomain.getParam());
        if (null == json) {
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "缺少查询必须参数值", signKey);
        }
        LOG.debug("查询学生缴费科目入参明文：({})", json);
        String user_no = null;
        String status = "";
        String page_no = "";//缴费项状态，=1未支付（部分支付），=2已支付，=0或者无此参数表示查询所有缴费项目
        String page_size = "";//一页显示数量，默认为15条

        ObjectMapper mapper = new ObjectMapper();
        ReturenDomain r = null;
        int pageNo = 0;
        int pageSize = 15;
        try {
            JsonNode root = mapper.readTree(json);
            if (root.has("user_no")) {
                user_no = root.get("user_no").asText();
            }
            if (root.has("status")) {
                status = root.get("status").asText();
            }
            if (root.has("page_no")) {
                page_no = root.get("page_no").asText();
            }
            if (root.has("page_size")) {
                page_size = root.get("page_size").asText();
            }
            if (!StringUtils.isEmpty(page_no)) {
                pageNo = Integer.valueOf(page_no);
                if (pageNo >= 1) {
                    pageNo -= 1;
                }
            }
            if (!StringUtils.isEmpty(page_size)) {
                pageSize = Integer.valueOf(page_size);
            }
            if (StringUtils.isEmpty(user_no)) {
                new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "user_no是必须的", signKey);
            }
            LOG.info("查询学生缴费科目解析到正确的查询参数user_no({}), status({}), page_no({}), page_size({})", user_no, status, page_no, page_size);
        } catch (Exception e) {
            LOG.warn("查询学生缴费科目解析解密后的JSON出错:{}", e);
            r = new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "解析解密后的JSON出错", signKey);
        }

        PageRequest pageParam = PageUtil.createNoErrorPageRequest(pageNo, pageSize);
        Page<StudentPaySubjectDomain> page = null;
        if (StringUtils.isEmpty(status) || status.equals("0")) {
            page = personalCostService.findPersonalCostSubjectList(pageParam, user_no, PublishState.PUBLISH.getState());
        }  else if (status.equals("1")) {
            page = personalCostService.findPersonalCostSubjectListPaymentStateLessEq(pageParam, user_no, PublishState.PUBLISH.getState(), PaymentState.OWED.getState());
        } else  if (status.equals("2")) {
            page = personalCostService.findPersonalCostSubjectListPaymentStateGe(pageParam, user_no, PublishState.PUBLISH.getState(), PaymentState.OWED.getState());
        } else {
            r = new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "status=" + status + " 不被识别", signKey);
        }
        if (null != page) {
            StudentPaySubjectQueryListDomain jr = new StudentPaySubjectQueryListDomain(user_no, page_no, page_size);
            jr.setTotal_page("" + page.getTotalPages());
            jr.setData(page.getContent());
            try {
                json = mapper.writeValueAsString(jr);
                LOG.debug("查询学生缴费科目JSON结果:{}", json);
                r = new ReturenDomain(AliFailCode.NORMAL.getStateDesc(), aesCryptor.encrypt(json), signKey);
            } catch (Exception e) {
                LOG.warn("查询学生缴费科目生成JSON加密数据出错:{}", e);
                r = new ReturenDomain(AliFailCode.PARAM_FAIL.getStateDesc(), "生成JSON加密数据出错", signKey);
            }
        }
        return r;
    }

    /**
     * 订单支付反馈回调
     * @param inputDomain
     * @return
     */
    public ReturenDomain doFeedBackOrder(InputDomain inputDomain) {
        if(!validateInputParam(inputDomain)) {
            LOG.warn("支付结果反馈验证入参失败");
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "验证入参失败", signKey);
        }

        String json = aesCryptor.decrypt(inputDomain.getParam());
        if (null == json) {
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "缺少查询必须参数值", signKey);
        }
        LOG.debug("支付结果反馈解密参数JSON:{}", json);
        PaymentOrderDomain order = null;
        try {
            order = JsonUtil.decode(json, PaymentOrderDomain.class);
            if (null != order && null != order.getData() && order.getData().size() > 0) {
                if (StringUtils.isEmpty(order.getOrder_id()) || StringUtils.isEmpty(order.getTotal_fee()) || StringUtils.isEmpty(order.getUser_no())) {
                    LOG.warn("order_id , total_fee , user_no is requied.");
                    return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "order_id、total_fee、user_no是必须的", signKey);
                }
            } else {
                return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "订单及至少一个订单子项是必须的", signKey);
            }
        } catch (Exception e) {
            LOG.warn("支付结果反馈，JSON转订单对象失败.{}", e);
            return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "解析JSON数据失败", signKey);
        }

        PaymentOrder paymentOrder = new PaymentOrder ();
        paymentOrder.setInfo(order.getInfo());
        paymentOrder.setOrderEndDate(order.getOrder_end_date());
        paymentOrder.setOrderStartDate(order.getOrder_start_date());
        paymentOrder.setOrderId(order.getOrder_id());
        paymentOrder.setPayType(order.getPay_type());
        paymentOrder.setTotalFee(order.getTotal_fee());
        paymentOrder.setUserNo(order.getUser_no());

        List<PaymentOrderItem> paymentOrderItemList = new ArrayList<>();
        Map<String, PaymentOrderItem> personalCostMap = new HashMap<>();

        for (PaymentOrderItemDomain item : order.getData()) {
            PaymentOrderItem orderItem = new PaymentOrderItem();
            orderItem.setPaymentOrder(paymentOrder);

            orderItem.setFee(item.getFee());
            orderItem.setItemId(item.getItem_id());
            orderItem.setName(item.getName());
            if (StringUtils.isEmpty(orderItem.getItemId()) || StringUtils.isEmpty(orderItem.getFee())) {
                return new ReturenDomain (AliFailCode.PARAM_FAIL.getStateDesc(), "订单子项的item_id、fee是必须的", signKey);
            }
            paymentOrderItemList.add(orderItem);
            personalCostMap.put(orderItem.getItemId(), orderItem);
        }
        try {
            int status = doSaveProcessPersonalCostAndTradeRecord(paymentOrder, paymentOrderItemList, personalCostMap);//人员缴费叠加、状态变动、缴费大厅记录保存
            if (0 != status) {
                return new ReturenDomain (AliFailCode.OTHER_FAIL.getStateDesc(), "根据字段user_no和item_id没有查找到相应的人员费用信息", signKey);
            }
        } catch (Exception e) {
            LOG.warn("支付结果反馈保存订单及更新人员费用信息失败.{}", e);
            return new ReturenDomain (AliFailCode.OTHER_FAIL.getStateDesc(), "计算人员费用及保存支付记录失败", signKey);
        }
        return new ReturenDomain(AliFailCode.NORMAL.getStateDesc(), "接收处理成功", signKey);
    }

    private int doSaveProcessPersonalCostAndTradeRecord(PaymentOrder paymentOrder, List<PaymentOrderItem> paymentOrderItemList, Map<String, PaymentOrderItem> personalCostMap) {
        List<PersonalCost> personalCostList = personalCostService.findByIdNumberAndPaymentSubjectIdIn(paymentOrder.getUserNo(), personalCostMap.keySet());
        if (null == personalCostList || personalCostList.isEmpty()) {
            return -1;//返回给缴费大厅的错误状态
        } else {
            for (PersonalCost personalCost : personalCostList) {
                PaymentOrderItem orderItem = personalCostMap.get(personalCost.getId());
//                orderItem = personalCostMap.get(personalCost.getId());
                Double hasPay = 0.0;
                if (null != orderItem) {
                    if (null != personalCost.getHasPay()) {
                        hasPay = personalCost.getHasPay();
                    }
                    hasPay += (new Double(orderItem.getFee()) / 100);
                    personalCost.setHasPay(hasPay);
                    if (hasPay >= personalCost.getShouldPay()) {
                        personalCost.setPaymentState(PaymentState.COMPLETE.getState());
                    } else if (hasPay > 0) {
                        personalCost.setPaymentState(PaymentState.OWED.getState());
                    }
                    personalCost.setLastModifiedDate(new Date());
                } else {
                    LOG.warn("缴费科目ID没有找到相应的人员费用对象:{}" + personalCost.getPaymentSubject().getId());
                }
            }
            personalCostService.save(personalCostList);
        }
        paymentOrderService.save(paymentOrder);
        paymentOrderItemService.save(paymentOrderItemList);
        return 0;
    }
}
