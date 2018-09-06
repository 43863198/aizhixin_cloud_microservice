package com.aizhixin.cloud.paycallback.v1.controller;

import com.aizhixin.cloud.paycallback.common.util.DateUtil;
import com.aizhixin.cloud.paycallback.entity.AliCallBackLogRecord;
import com.aizhixin.cloud.paycallback.service.PersonalCostOrderService;
import com.aizhixin.cloud.paycallback.service.ThirdPayService;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝回调及缴费大厅调用接口
 */
@Controller
@RequestMapping("/alipay")
public class AlipayCallbackController {
//    private static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz58xBmYbiwo3wBk8AC/garRYKMv4gseiGoKgQ+E614ATXmRCNURbqXC+x0zuMinL4SBj90sOPMP1IWA+mGY1SFTLrev0VzZOx5+mqgk3213avpgSEemto5flq79N7WFNPfHogJVr/F715e1YY3+/0gEm7wyBQTVIg82cyeXttKdL2il/iBgwFDN6/OUQtSCG0jWDTAyv3umpVafadWT0mYM4CIE9v1bbGBrH+FuegOwM7X9Ir0wALyJKiX3PjpRtHR0ufOPc/EEXdKVjDQ+tKBHbIjJYnNKLxmh5fcQ9CEcGbvZeX0I4+pxmGtzyZc8PbGxFa+NnvgqpHib560P2RwIDAQAB";
    private final static Logger LOG = LoggerFactory.getLogger(AlipayCallbackController.class);
    @Value("${pay.alipayPublicKey}")
    private String alipayPublicKey;
    @Autowired
    private PersonalCostOrderService personalCostOrderService;

    private AliCallBackLogRecord processRequest(HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        AliCallBackLogRecord record = new AliCallBackLogRecord();
        Map<String,String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            System.out.println(name + "\t" + valueStr);
            if ("gmt_create".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setGmtCreate(DateUtil.parseSecond(valueStr));
                }
            } else if ("charset".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setCharset(valueStr);
                }
            } else if ("subject".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setSubject(valueStr);
                }
            } else if ("body".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setBody(valueStr);
                }
            } else if ("buyer_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setBuyerId(valueStr);
                }
            } else if ("invoice_amount".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setInvoiceAmount(new Double(valueStr));
                }
            } else if ("notify_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setNotifyId(valueStr);
                }
            } else if ("fund_bill_list".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setFundBillList(valueStr);
                }
            } else if ("notify_type".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setNotifyType(valueStr);
                }
            } else if ("trade_status".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setTradeStatus(valueStr);
                }
            } else if ("receipt_amount".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setReceiptAmount(new Double(valueStr));
                }
            } else if ("app_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setAppId(valueStr);
                }
            } else if ("buyer_pay_amount".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setBuyerPayAmount(new Double(valueStr));
                }
            } else if ("sign_type".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setSignType(valueStr);
                }
            } else if ("seller_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setSellerId(valueStr);
                }
            } else if ("gmt_payment".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setGmtPayment(DateUtil.parseSecond(valueStr));
                }
            } else if ("notify_time".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setNotifyTime(DateUtil.parseSecond(valueStr));
                }
            } else if ("version".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setVersion(valueStr);
                }
            } else if ("out_trade_no".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setOutTradeNo(valueStr);
                }
            } else if ("total_amount".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setTotalMmount(new Double(valueStr));
                }
            } else if ("trade_no".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setTradeNo(valueStr);
                }
            } else if ("auth_app_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setAuthAppId(valueStr);
                }
            } else if ("buyer_logon_id".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setBuyerLogonId(valueStr);
                }
            } else if ("point_amount".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setPointAmount(new Double(valueStr));
                }
            } else if ("sign".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setSign(valueStr);
                }
            } else if ("out_biz_no".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setOutBizNo(valueStr);
                }
            } else if ("gmt_refund".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setGmtRefund(DateUtil.parseSecond(valueStr));
                }
            } else if ("refund_fee".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setRefundFee(new Double(valueStr));
                }
            } else if ("gmt_close".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setGmtClose(DateUtil.parseSecond(valueStr));
                }
            } else if ("passback_params".equals(name)) {
                if (!StringUtils.isEmpty(valueStr)) {
                    record.setPassbackParams(valueStr);
                }
            }
            params.put(name, valueStr);
        }
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        LOG.info("Receive alipay params:{}", record);
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "utf-8", "RSA2");
            if (flag) {
                return record;
            } else {
                return null;
            }
        } catch (Exception e) {
            record = null;
            LOG.warn("Check alipay AlipaySignature fail:{}", e);
        }
        return record;
    }


    @RequestMapping("/callback")
    public void getReqAndResCallback(HttpServletRequest request, HttpServletResponse response){
        System.out.println("in callback api");
        AliCallBackLogRecord record = processRequest(request);
        try {
            if (null != record) {
                personalCostOrderService.doAliCallBackOrder(record);
            }
            response.getOutputStream().print("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/gateway")
    public void getReqAndResGateway(HttpServletRequest request, HttpServletResponse response){
        System.out.println("in gateway api");
        processRequest(request);
        try {
            response.getOutputStream().print("success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}