package com.yuhang.trading.mapper.buy;

import com.yuhang.trading.entity.request.TradeRequest;
import org.apache.ibatis.annotations.Param;

public interface BuyMapper {

    void insert(@Param("request") TradeRequest request);

}