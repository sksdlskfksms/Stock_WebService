package com.stock.vo.payco;

import lombok.Data;

/*
 * 페이코 주문 예약 API OUTPUT
 */
@Data
public class PaycoReserveVO {
    private Long   reserveId;       // reserveId
    private String reserveOrderNo;  // 주문예약번호
    private String orderSheetUrl;   // 자동결제 등록 및 변경 url (팝업 open)
}
