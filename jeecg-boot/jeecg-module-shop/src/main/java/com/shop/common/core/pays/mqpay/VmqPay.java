package com.shop.common.core.pays.mqpay;

import com.alibaba.fastjson.JSON;
import com.shop.entity.Pays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.util.Map;

@Slf4j
public class VmqPay {

    public static String sendCreateMqPay(Pays pays, double price, String payId, String cloudPayid, String param) {

        Map mapTypes = JSON.parseObject(pays.getConfig());

        String key = mapTypes.get("key").toString();
        String create_url = mapTypes.get("create_url").toString();
        String notify_url = mapTypes.get("notify_url").toString();

        Integer type = 2; // 默认支付宝
        if (pays.getDriver().equals("mqpay_alipay")) {
            type = 2;
        } else if (pays.getDriver().equals("mqpay_wxpay")) {
            type = 1;
        }

        String notifyUrl = notify_url + "/mqpay/notifyUrl";
        String returnUrl = notify_url + "/mqpay/returnUrl";
        String content = payId + param + type + price;
        String jsSign = md5(content + key);
        log.info("md5({})",content);
        return create_url + "/createOrder?payId=" + payId + "&type=" + type + "&price=" + price + "&notifyUrl=" + notifyUrl + "&returnUrl=" + returnUrl + "&sign=" + jsSign + "&param=" + param + "&isHtml=1";
    }

    public static String md5(String text) {
        //加密后的字符串
        String encodeStr = DigestUtils.md5DigestAsHex(text.getBytes());
        return encodeStr;
    }

}