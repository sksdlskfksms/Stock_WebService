package com.stock.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 자동결제 신청 정보 데이터
 */
@Getter @Setter
@ToString
public class ReserveVO {

    private Long   id;
    private Long   userId;
    private String userCid;
    private String pgName;
    private String paycoReserveOrderNo;
    private String paycoAutopaymentNo;
    private String paycoAutopaymentCertifyKey;
    private String allatFixKey;

}


