package com.yuhang.trading.mapper.account;

import com.yuhang.trading.entity.account.AccountRequest;
import org.apache.ibatis.annotations.Param;

public interface AccountRequestMapper {

    void insert(@Param("account") AccountRequest account);

}