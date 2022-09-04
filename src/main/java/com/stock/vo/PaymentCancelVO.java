package com.stock.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 자동결제 결제 취소 처리 내역 데이터
 */
@Getter @Setter
@ToString
public class PaymentCancelVO {

    private Long   id;
    private Long   reserveId;
    private String orderCode;
    private int    refundAmount;
    private String pgName;
    private String refundResultCode;
    private String refundResultMessage;
    private String deleteResultCode;
    private String deleteResultMessage;
    private String allatFixKey;
    private String paycoOrderNo;
    private String completeYmdt;

    public void setRefundSuccessResult() {
        this.refundResultCode = "00";
        this.refundResultMessage = "정상";
    }

    public void setDeleteSuccessResult() {
        this.deleteResultCode = "00";
        this.deleteResultMessage = "정상";
    }

    public boolean isRefundSuccess(){
        return this.refundResultCode.equals("00");
    }

    public boolean isDeleteSuccess(){
        return this.deleteResultCode.equals("00");
    }

}


