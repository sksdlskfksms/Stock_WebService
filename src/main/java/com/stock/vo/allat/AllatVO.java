package com.stock.vo.allat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 올앳 결제 승인용 데이터
 */
@Getter
@Setter
@ToString
public class AllatVO {
    private String allat_enc_data;
    private String allat_order_no;
    private String userCid;

}
