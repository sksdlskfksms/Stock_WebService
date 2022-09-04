package com.stock.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 자동결제 결제 처리 내역 데이터
 */
@Getter
@Setter
@ToString
public class PaymentVO {

    private Long   id;
    private Long   reserveId;
    private String orderCode;
    private int    paymentAmount;
    private String pgName;
    private String resultCode;
    private String resultMessage;
    private String allatOrderNo;
    private String paycoOrderNo;
    private String paycoOrderCertifyKey;
    private String completeYmdt;

    public void setSuccessResult() {
        this.resultCode = "00";
        this.resultMessage = "정상";
    }

    public boolean isResultSuccess(){
        return this.resultCode.equals("00");
    }
}


