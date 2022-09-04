package com.stock.controller;

import com.stock.exception.CommonRedirectException;
import com.stock.service.CommonService;
import com.stock.service.EMoneyService;
import com.stock.service.UserService;
import com.stock.service.impl.oauth2.KakaoLogin;
import com.stock.service.impl.oauth2.NaverLogin;
import com.stock.util.Const;
import com.stock.vo.emoney.EMoneyMainDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class CommonController {

    @Autowired
    private EMoneyService eMoneyService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonService commonService;

    private NaverLogin naverLogin;

    private KakaoLogin kakaoLogin;

    @Autowired
    private void setNaverLogin(NaverLogin naverLogin) {
        this.naverLogin = naverLogin;
    }

    @Autowired
    private void setKakaoLogin(KakaoLogin kakaoLogin) {
        this.kakaoLogin = kakaoLogin;
    }


    /**
     * 메인 페이지
     */
    @RequestMapping(value = {"/main"})
    public ModelAndView main(HttpSession session,
                             @RequestParam(required = false) String mediaKey,
                             @RequestParam(required = false) String userKey)  {

        try{
            ModelAndView mnv = new ModelAndView("/main/main.jsp");
            boolean isJoinUser = false;
            boolean isUseSnsLogin = false;

            // 최초접속시 스크립트로 URL에서 파라미터를 제거하므로 새로고침했을시엔 세션에서 가져옴
            if(mediaKey == null) {
                mediaKey = (String) session.getAttribute("mediaKey");
            } else {
                session.setAttribute("mediaKey", mediaKey);
            }

            if(userKey == null) {
                userKey = (String) session.getAttribute("mediaUserKey");
            } else {
                session.setAttribute("mediaUserKey", userKey);
            }

            log.info("[Main 접속] -----------------------------> mediakey = {}, mediaUserKey = {}", mediaKey, userKey);
            if(userKey != null && mediaKey != null){
                // 가입여부
                isJoinUser = this.userService.isJoinUser(userKey, mediaKey);
            }

            // SNS로그인 사용여부
            if(mediaKey != null) isUseSnsLogin = this.commonService.findMediaInfoByMediaKey(mediaKey).getSnsUserFlg();
            if(isUseSnsLogin){
                mnv.addObject("kakaoAuthUrl", kakaoLogin.getAuthorizationUrl(session));
                mnv.addObject("naverAuthUrl", naverLogin.getAuthorizationUrl(session));
            }

            // 이머니 주식정보 API
            EMoneyMainDataVO eMoneyMainDataVO = this.eMoneyService.landMainEMoney();
            mnv.addObject("emoneyData", eMoneyMainDataVO);
            mnv.addObject("isJoinUser", isJoinUser);
            mnv.addObject("isSnsLoginOk", isUseSnsLogin);
            return mnv;

        }catch (Exception e){
            log.error("--- [Main 페이지 접근 오류 발생] -------------------------------");
            log.error("mediaKey = {}, mediaUserKey = {}", mediaKey, userKey);
            log.error("-----------------------------------------------------------");
            e.printStackTrace();
            throw new CommonRedirectException("ERROR", "/");
        }

    }


    /**
     * 이용약관 페이지
     * @return
     */
    @GetMapping(value = "/term/{name}")
    public String termInfo(@PathVariable(value = "name") Const.TermType name) {
        if(name.equals(Const.TermType.PERSONAL_INFO)){
            return "term/term-of-personalInfo.jsp";
        }else if(name.equals(Const.TermType.PERSONAL_INFO_SUPPLY)){
            return "term/term-of-personalInfoSupply.jsp";
        }
        return "term/term-of-service.jsp";
    }


}

