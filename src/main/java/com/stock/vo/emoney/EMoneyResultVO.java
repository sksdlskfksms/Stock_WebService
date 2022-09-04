package com.stock.vo.emoney;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EMoneyResultVO {
    String type;                // API 타입
    String userCid;             // 사용자구분코드
    String resultCode;          // 요청 결과 코드
    String resultMsg;           // 요청 결과 메세지
    String param;               // 요청 파라미터

}


