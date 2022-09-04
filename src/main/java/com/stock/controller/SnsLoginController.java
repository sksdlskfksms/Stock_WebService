package com.stock.controller;

import com.stock.exception.CommonRedirectException;
import com.stock.service.UserService;
import com.stock.service.impl.oauth2.KakaoLogin;
import com.stock.service.impl.oauth2.NaverLogin;
import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * SNS 간편 로그인
 */
@Slf4j
@RequestMapping(value = {"/login"})
@Controller
public class SnsLoginController {
    @Autowired
    private UserService userService;

    private KakaoLogin kakaoLogin;

    private NaverLogin naverLogin;

    @Autowired
    private void setKakaoLogin(KakaoLogin kakaoLogin) {
        this.kakaoLogin = kakaoLogin;
    }

    @Autowired
    private void setNaverLogin(NaverLogin naverLogin) {
        this.naverLogin = naverLogin;
    }


    /**
     * 카카오 로그인 성공 후 콜백
     * @param code
     * @param state
     */
    @RequestMapping(value = "/kakao", method = { RequestMethod.GET, RequestMethod.POST })
    public String kakao(HttpSession session,
                        @RequestParam(required = false) String code,
                        @RequestParam(required = false) String state,
                        @RequestParam(required = false) String mediaKey) throws IOException {
        try {
            if(mediaKey == null){
                mediaKey = (String) session.getAttribute("mediaKey");
            }

            OAuth2AccessToken oauthToken = kakaoLogin.getAccessToken(code, state);
            String apiResult = kakaoLogin.getUserProfile(oauthToken);                   // 계정 정보 조회 API
            JSONObject obj = new JSONObject(apiResult).getJSONObject("kakao_account");

            String mediaUserKey = "kakao_" + obj.getString("email");
            session.setAttribute("mediaUserKey", mediaUserKey);

            // 기가입 여부 체크
            boolean isJoinUser = this.userService.isJoinUser(mediaUserKey, mediaKey);
            if(isJoinUser){
                return "redirect:/main?userKey=" + mediaUserKey;
            }else{
                return "redirect:/user/join";
            }

        } catch (JSONException e) {
            throw new CommonRedirectException("정상적인 접근이 아닙니다.", "/main");
        }

    }


    /**
     * 네이버 로그인 성공 후 콜백
     * @param code
     * @param state
     */
    @RequestMapping(value = "/naver", method = { RequestMethod.GET, RequestMethod.POST })
    public String naver(HttpSession session,
                        @RequestParam(required = false) String code,
                        @RequestParam(required = false) String state,
                        @RequestParam(required = false) String mediaKey) throws IOException {
        try {
            if(mediaKey == null){
                mediaKey = (String) session.getAttribute("mediaKey");
            }

            OAuth2AccessToken oauthToken = naverLogin.getAccessToken(code, state);
            String apiResult = naverLogin.getUserProfile(oauthToken);                   // 계정 정보 조회 API
            JSONObject obj = new JSONObject(apiResult).getJSONObject("response");

            String mediaUserKey = "naver_" + obj.getString("email");
            session.setAttribute("mediaUserKey", mediaUserKey);

            // 기가입 여부 체크
            boolean isJoinUser = this.userService.isJoinUser(mediaUserKey, mediaKey);
            if(isJoinUser){
                return "redirect:/main?userKey=" + mediaUserKey;
            }else{
                return "redirect:/user/join";
            }

        } catch (JSONException e) {
            throw new CommonRedirectException("정상적인 접근이 아닙니다.", "/main");
        }

    }

}
