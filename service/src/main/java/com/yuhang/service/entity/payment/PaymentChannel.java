package com.yuhang.service.entity.payment;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @author David
 * 2/29/2024 10:38 PM
 */

@Data
public class PaymentChannel implements Serializable {

    private static final long serialVersionUID = 3769316406659919738L;
    private int id;

    private String bank;

    private String paymentChannel;
}
