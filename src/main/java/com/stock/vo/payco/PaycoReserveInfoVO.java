package com.stock.vo.payco;

import lombok.Data;

/*
 * 페이코 자동결제 예약 완료 후 정합성 검사를 위해 받는 데이터
 */
@Data
public class PaycoReserveInfoVO {
    private String sellerKey;                        // 판매자Key
    private String sellerAutoPaymentReferenceKey;    // 자동결제 관리번호
    private String autoPaymentCertifyKey;            // 자동결제 인증 키
}
