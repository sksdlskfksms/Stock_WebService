package com.stock.service.impl;

import com.stock.service.AuthService;
import com.stock.util.Const;
import com.stock.vo.auth.AuthEntity;
import com.stock.vo.auth.AuthResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Value("${auth.join.success.returnUrl}")
    private String authReturnJoinUrl;

    @Value("${auth.cancel.success.returnUrl}")
    private String authReturnCancelUrl;

    @Value("${auth.fail.returnUrl}")
    private String authErrorUrl;

    @Value("${auth.sSiteCode}")
    private String sSiteCode;      // 사이트 코드

    @Value("${auth.sSitePassword}")
    private String sSitePassword;  // 사이트 패스워드


    /**
     * 암호화 처리
     * @param session
     * @param type
     * @return
     * @throws Exception
     */
    @Override
    public String authRequest(HttpSession session, Const.JoinStatus type) throws Exception {
        NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
        AuthEntity niceAuthEntity = new AuthEntity();

        if(type.equals(Const.JoinStatus.JOIN)){
            niceAuthEntity.setSReturnUrl(this.authReturnJoinUrl);
        }else if(type.equals(Const.JoinStatus.CANCEL)){
            niceAuthEntity.setSReturnUrl(this.authReturnCancelUrl);
        }
        niceAuthEntity.setSErrorUrl(this.authErrorUrl);
        niceAuthEntity.setSRequestNumber(niceCheck.getRequestNO(this.sSiteCode));
        session.setAttribute("REQ_SEQ" , niceAuthEntity.getSRequestNumber());

        int iReturn = niceCheck.fnEncode(this.sSiteCode, this.sSitePassword, niceAuthEntity.getsPlainData(this.sSiteCode));

        AuthEntity.AuthResult authResult = niceAuthEntity.new AuthResult();
        if( iReturn == 0 ) authResult.setSEncData(niceCheck.getCipherData());
        else{
            if( iReturn == -1) log.error("[AuthController][authRequest] 암호화 시스템 에러입니다.");
            else if( iReturn == -2) log.error("[AuthController][authRequest] 암호화 처리오류입니다.");
            else if( iReturn == -3) log.error("[AuthController][authRequest] 암호화 데이터 오류입니다.");
            else if( iReturn == -9) log.error("[AuthController][authRequest] 입력 데이터 오류입니다.");
            else log.error("[AuthController][authRequest] 알수 없는 에러 입니다. iReturn -> {}", iReturn);

            throw new Exception("99");
        }

        return authResult.getSEncData();

    }


    /**
     * 복호화 및 인증 결과
     * @param type
     * @param sEncodeData
     * @param sRequestNumber
     * @return
     * @throws Exception
     */
    @Override
    public AuthResultVO authResponse(String type, String sEncodeData, String sRequestNumber) throws Exception {
        NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
        AuthResultVO authResultVO = new AuthResultVO();

        int iReturn = niceCheck.fnDecode(this.sSiteCode, this.sSitePassword, sEncodeData);
        log.info("[인증완료] iReturn = {}, EncodeData ----->{}", iReturn, sEncodeData);

        if( iReturn == 0 ){
            HashMap mapresult = niceCheck.fnParse(niceCheck.getPlainData());

            authResultVO.setSCipherTime(niceCheck.getCipherDateTime());
            authResultVO.setSConnInfo((String)mapresult.get("ERR_CODE"));       // 에러코드
            authResultVO.setSRequestNumber((String)mapresult.get("REQ_SEQ"));   // 요청 번호
            authResultVO.setSResponseNumber((String)mapresult.get("RES_SEQ"));  // 처리결과 고유번호
            authResultVO.setSConnInfo((String)mapresult.get("CI"));             // 연계정보 확인값 (CI_88 byte)
            authResultVO.setSName((String)mapresult.get("NAME"));               // 성명 (50 Byte, EUC-KR)
            authResultVO.setSMobileNo((String)mapresult.get("MOBILE_NO"));      // 휴대폰번호

            if(!authResultVO.getSRequestNumber().equals(sRequestNumber)){
                log.error("---[ 본인 인증 복호화 오류 ]------------------------------------------------------------------------");
                log.error("resultMsg = 세션 불일치 오류입니다, AuthResultVO = {}", authResultVO.toString());
                log.error("------------------------------------------------------------------------------------------------");
                throw new Exception();
            }

            return authResultVO;
        } else {
            String resultMsg = "";	// 오류 메세지
            if( iReturn == -1) resultMsg = "복호화 시스템 오류입니다";
            else if( iReturn == -4) resultMsg = "복호화 처리 오류입니다";
            else if( iReturn == -5) resultMsg = "복호화 해쉬 오류입니다";
            else if( iReturn == -6) resultMsg = "복호화 데이터 오류입니다";
            else if( iReturn == -9) resultMsg = "입력 데이터 오류입니다";
            else if( iReturn == -12) resultMsg = "사이트 패스워드 오류입니다";
            else resultMsg = "알수 없는 에러 입니다";

            log.error("--------------------------------------[ 본인 인증 복호화 오류 ]--------------------------------------");
            log.error("iReturn = {}, resultMsg = {}", iReturn, resultMsg);
            log.error("sEncodeData = {}", sEncodeData);
            log.error("------------------------------------------------------------------------------------------------");
            throw new Exception();
        }

    }


}