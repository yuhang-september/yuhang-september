package com.yuhang.trading.redeem;

import com.yuhang.service.entity.request.TradeRequest;
import com.yuhang.trading.common.Constants;
import com.yuhang.trading.common.utils.DateUtil;
import com.yuhang.trading.rabbitmq.Sender;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @author David
 * 5/17/2024
 */
@Service
public class RedeemService {

    @Resource
    Sender sender;

    public void redeem(TradeRequest request) {
        String tradeRequestId = UUID.randomUUID().toString().replaceAll("-", "");
        request.setTradeRequestId(tradeRequestId);
        Date currentDate = DateUtil.getCurrentDate();
        request.setCreateTime(currentDate);
        request.setLastUpdateTime(currentDate);
        request.setStatus(Constants.TRADE_REQUEST_STATUS_ORDER);
        request.setType(Constants.TRADE_TYPE_REDEEM);
        sender.sendToRedeemQueue(request);
    }

}