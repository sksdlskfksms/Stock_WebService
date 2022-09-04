package com.stock.vo.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthEntity {
    private String sRequestNumber;
    private String sAuthType = "M";       // M: 핸드폰, C: 신용카드, X: 공인인증서
    private String popgubun  = "N";		  // Y : 취소버튼 있음 / N : 취소버튼 없음
    private String customize = "";		  // 없으면 기본 웹페이지 / Mobile : 모바일페이지
    private String sGender   = ""; 		  // 없으면 기본 선택 값 / 0 : 여자, 1 : 남자
    private String sReturnUrl;            // 성공시 이동될 URL
    private String sErrorUrl;             // 실패시 이동될 URL
    private String sPlainData;


    public String getsPlainData(String sSiteCode){
        return sPlainData = "7:REQ_SEQ" + this.sRequestNumber.getBytes().length + ":" + this.sRequestNumber +
                            "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
                            "9:AUTH_TYPE" + this.sAuthType.getBytes().length + ":" + this.sAuthType +
                            "7:RTN_URL" + this.sReturnUrl.getBytes().length + ":" + this.sReturnUrl +
                            "7:ERR_URL" + this.sErrorUrl.getBytes().length + ":" + this.sErrorUrl +
                            "11:POPUP_GUBUN" + this.popgubun.getBytes().length + ":" + this.popgubun +
                            "9:CUSTOMIZE" + this.customize.getBytes().length + ":" + this.customize +
                            "6:GENDER" + this.sGender.getBytes().length + ":" + this.sGender;
    }

    @Getter
    @Setter
    public class AuthResult {
        private String sEncData = "";
    }

}


