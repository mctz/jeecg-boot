package com.vmq.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.vmq.constant.Constant;
import com.vmq.constant.PayTypeEnum;
import com.vmq.dao.PayOrderDao;
import com.vmq.dao.SettingDao;
import com.vmq.dao.TmpPriceDao;
import com.vmq.entity.VmqSetting;
import com.vmq.utils.PayQueryUtil;
import com.vmq.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class QuartzService {
    @Autowired
    private SettingDao settingDao;
    @Autowired
    private PayOrderDao payOrderDao;
    @Autowired
    private TmpPriceDao tmpPriceDao;
    @Autowired
    private WebService webService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 处理过期订单：更新订单状态并删除临时金额
     */
    @Scheduled(fixedRate = 60000)
    public void autoUpdatePayOrderStatus(){
        String method = "autoUpdatePayOrderStatus";
        String temp = redisTemplate.opsForValue().get(method);
        if (StringUtils.isNotBlank(temp)) {
            return;
        }
        String msg = "";
        // 获取超时时间（分钟）
        List<VmqSetting> vmqSettingList = settingDao.findAll();
        for (VmqSetting vmqSetting : vmqSettingList) {
            String username = vmqSetting.getUsername();
            try {
                String timeout = vmqSetting.getClose();
                int timeoutMS = Integer.valueOf(timeout)*60*1000;

                String closeTime = String.valueOf(new Date().getTime());
                timeout = String.valueOf(new Date().getTime() - timeoutMS);

                // 更改过期订单关闭时间和状态
                int row = payOrderDao.setTimeout(username,timeout,closeTime);

                long beginTime = Long.valueOf(closeTime) - timeoutMS;

                List<Map<String,Object>> payOrders = payOrderDao.findAllByCloseDate(username,beginTime, Long.valueOf(closeTime));
                for (Map payOrder: payOrders) {
                    tmpPriceDao.delprice(username,payOrder.get("type")+"-"+payOrder.get("really_price"));
                }
                // 补偿机制，删除异常数据
                tmpPriceDao.delpriceByUsername(username);
                msg += String.format("[%s]成功清理%d个订单 ", username, row);
            }catch (Exception e){
                e.printStackTrace();
                log.info("清理订单失败: {}",e.getMessage());
            }
            String lastheart = vmqSetting.getLastheart();
            String state = vmqSetting.getJkstate();
            if ("1".equals(state) && new Date().getTime() - Long.valueOf(lastheart) > 60*1000){
                vmqSetting.setJkstate("0");
                settingDao.save(vmqSetting);
            }
        }
        log.info(msg);
        redisTemplate.opsForValue().set(method, method, Constant.NUMBER_30, TimeUnit.SECONDS);
    }

    /**
     * 支付宝云端监控
     */
    @Scheduled(fixedRate = 5000)
    public void queryAliPayAndPush(){
        String method = "queryAliPayAndPush";
        String temp = redisTemplate.opsForValue().get(method);
        if (StringUtils.isNotBlank(temp)) {
            return;
        }
        // 获取超时时间（分钟）
        List<VmqSetting> vmqSettingList = settingDao.findAll();
        for (VmqSetting vmqSetting : vmqSettingList) {
            String username = vmqSetting.getUsername();
            if (StringUtils.isBlank(vmqSetting.getAliCookie())) {
                continue;
            }
            String cookie = vmqSetting.getAliCookie();
            JSONArray array = PayQueryUtil.getAliPayPage(120,cookie);
            array.stream().forEach(detail -> validAliPayInfo(username,(LinkedHashMap) detail));
        }
        redisTemplate.opsForValue().set(method, method, Constant.NUMBER_2, TimeUnit.SECONDS);
    }

    private void validAliPayInfo(String username, LinkedHashMap detailMap) {
        int payType = PayTypeEnum.ZFB.getCode();
        JSONObject detail = JSON.parseObject(JSON.toJSONString(detailMap));
        String gmtCreate = detail.getString("gmtCreate");
        String goodsTitle = detail.getString("goodsTitle");
        String outTradeNo = detail.getString("outTradeNo");
        String totalAmount = detail.getString("totalAmount");
        String tradeRefundAmount = detail.getString("tradeRefundAmount");
        String tradeStatus = detail.getString("tradeStatus");
        long payTime = DateUtil.parseDateTime(gmtCreate).getTime();
        if (Constant.SUCCESS.equals(tradeStatus)) {
            if (System.currentTimeMillis() - payTime > Constant.MIN5) {
                return;
            }
            log.info("创建时间：{}\t名称：{}\t商户订单号：{}\t订单金额：{}\t退款金额：{}\t交易状态：{}",gmtCreate,goodsTitle,outTradeNo,totalAmount,tradeRefundAmount,tradeStatus);
            if (webService.checkRepeatPush(username,payType,totalAmount,Long.valueOf(payTime))) {
                return;
            }
            String result = webService.webPush(username, payType, totalAmount, outTradeNo);
            log.info("validAliPayInfo: {}",result);
        }
    }
}