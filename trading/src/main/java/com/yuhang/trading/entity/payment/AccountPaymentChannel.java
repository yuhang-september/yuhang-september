package com.yuhang.trading.entity.payment;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Description:
 *
 * @author David
 * @Date 3/13/2024 5:40 PM
 */
@Data
public class AccountPaymentChannel implements Serializable {

    @Serial
    private static final long serialVersionUID = -9108881759037320976L;
    private String id;
    private String accountNo;
    private String bankAccountNo;
    private String paymentChannel;
}
