package com.stock.vo.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResultVO {
    private String sCipherTime;			    // 복호화한 시간
    private String sRequestNumber;			// 요청 번호
    private String sResponseNumber;		    // 인증 고유번호
    private String sConnInfo;				// 연계정보 확인값 (CI_88 byte)
    private String sErrorCode;				// 인증 에러 코드
    private String sName;                   // 성명
    private String sMobileNo;               // 휴대폰번호

    public String toString(){
        return "[ ErrorCode = " +  sErrorCode + ", RequestNumber = " + sRequestNumber + ", user_Ci = " + sConnInfo + " ]";
    }

}


