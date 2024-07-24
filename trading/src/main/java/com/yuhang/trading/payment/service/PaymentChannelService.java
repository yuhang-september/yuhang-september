package com.yuhang.trading.payment.service;

import com.yuhang.service.common.Constants;
import com.yuhang.service.common.JsonResult;
import com.yuhang.service.common.RuleException;
import com.yuhang.service.common.utils.DateUtil;
import com.yuhang.service.common.utils.SessionUtil;
import com.yuhang.service.entity.account.Account;
import com.yuhang.service.entity.payment.AccountPaymentChannel;
import com.yuhang.service.entity.payment.AccountPaymentChannelRequest;
import com.yuhang.service.entity.payment.BankCard;
import com.yuhang.service.entity.payment.PaymentChannel;
import com.yuhang.service.mapper.payment.AccountPaymentChannelMapper;
import com.yuhang.service.mapper.payment.AccountPaymentChannelRequestMapper;
import com.yuhang.service.mapper.payment.PaymentChannelMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Description:
 *
 * @author David
 * 2/29/2024 9:43 PM
 */
@Service
public class PaymentChannelService {

    @Resource
    PaymentChannelMapper paymentChannelMapper;

    @Resource
    AccountPaymentChannelMapper accountPaymentChannelMapper;

    @Resource
    AccountPaymentChannelRequestMapper accountPaymentChannelRequestMapper;

    @Transactional
    public boolean savePaymentChannel(BankCard bankCard) throws RuleException {
        AccountPaymentChannel accountPaymentChannel = new AccountPaymentChannel();
        accountPaymentChannel.setId(UUID.randomUUID().toString().replaceAll("-",""));
        Account accountInfo = SessionUtil.getAccountInfo();
        if (accountInfo == null) {
            throw new RuleException(JsonResult.UNKNOWN, "The session is timeout, please log in again.");
        }
        setAccountPaymentInfo(bankCard, accountPaymentChannel, accountInfo);
        AccountPaymentChannelRequest request = new AccountPaymentChannelRequest();
        BeanUtils.copyProperties(accountPaymentChannel, request);
        accountPaymentChannelRequestMapper.insert(request);
        return accountPaymentChannelMapper.insert(accountPaymentChannel);
    }

    private void setAccountPaymentInfo(BankCard bankCard, AccountPaymentChannel accountPaymentChannel, Account accountInfo) throws RuleException {
        accountPaymentChannel.setAccountNo(accountInfo.getId());
        accountPaymentChannel.setBankAccountNo(bankCard.getBankAccountNo());
        accountPaymentChannel.setPaymentChannel(getResponsiblePaymentChannel(bankCard.getBank()));
        accountPaymentChannel.setStatus(Constants.PAYMENT_CHANNEL_STATUS_ACTIVE);
        Date currentDate = DateUtil.getCurrentDate();
        accountPaymentChannel.setCreateTime(currentDate);
        accountPaymentChannel.setLastUpdateTime(currentDate);
    }

    private String getResponsiblePaymentChannel(String bank) throws RuleException {
        PaymentChannel paymentChannel = paymentChannelMapper.selectByBank(bank);
        if (paymentChannel == null) {
            throw new RuleException(JsonResult.UNKNOWN, "Please check your bank card information.");
        }
        return paymentChannel.getPaymentChannel();
    }

    public List<AccountPaymentChannel> queryAllAccountPaymentChannels(String accountId) {
        return accountPaymentChannelMapper.queryByAccountNo(accountId);
    }
}
