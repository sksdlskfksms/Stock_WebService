package com.stock.vo.allat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 올앳 결제 승인 요청 결과
 */
@Getter
@Setter
@ToString
public class AllatApproveReqVO {
    private Long   reserveId;
    private String sReplyCd;    //결과코드
    private String sReplyMsg;   //결과메세지
    private String sFixKey;     // 인증키
    private String sApplyYmd;   // 인증일

}
