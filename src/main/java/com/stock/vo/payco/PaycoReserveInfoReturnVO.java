package com.stock.vo.payco;

import lombok.Data;

/*
 * 페이코 자동결제 등록 API OUTPUT
 */
@Data
public class PaycoReserveInfoReturnVO {
    private Long   reserveId;
    private String sellerAutoPaymentReferenceKey;
    private String autoPaymentCertifyKey;
    private String autoPaymentNo;
    private String paymentMethodCode;
    private String paymentMethodName;
    private String pinNo;
    private String corporationCode;
    private String corporationName;
    private String registYmdt;
}
