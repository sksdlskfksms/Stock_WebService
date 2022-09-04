package com.stock.controller;

import com.stock.exception.CommonRedirectException;
import com.stock.service.AuthService;
import com.stock.util.Const;
import com.stock.util.ParamUtil;
import com.stock.vo.UserVO;
import com.stock.vo.auth.AuthResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 본인 인증
 */
@Slf4j
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;


    /**
     * 암호화 처리
     * @return
     */
    @GetMapping(value = "/auth/request/encdata")
    @ResponseBody
    public ResponseEntity<String> authRequest(HttpSession session, @RequestParam Const.JoinStatus type) {
        try{
            String sencData = authService.authRequest(session, type);
            return new ResponseEntity<>(sencData, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 복호화 및 인증 결과
     * @return
     */
    @RequestMapping(value = "/auth/{type}/result", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView authResponse(ServletRequest request, HttpSession session, @PathVariable String type) {
        try{
            String sEncodeData = ParamUtil.requestReplace(request.getParameter("EncodeData"), "encodeData");
            String sRequestNumber = (String)session.getAttribute("REQ_SEQ");
            AuthResultVO authResultVO = authService.authResponse(type, sEncodeData, sRequestNumber);

            ModelAndView mnv = new ModelAndView("/redirect/auth-redirect.jsp");
            mnv.addObject("name", authResultVO.getSName());
            mnv.addObject("phoneNum", authResultVO.getSMobileNo());
            mnv.addObject("userCid", authResultVO.getSConnInfo());
            mnv.addObject("encodeData", sEncodeData);
            mnv.addObject("type", type);
            return mnv;
        }catch (Exception e){
            e.printStackTrace();
            throw new CommonRedirectException("본인인증을 다시 진행해주세요.", "/user/" + type + "?error=true");
        }

    }


    /**
     * 팝업창 닫고 부모창에서 재실행시 복호화 중복 호출 방지용 리다이렉트 처리
     * @param type
     * @param userVO
     * @return
     */
    @PostMapping(value = "/user/{type}")
    public ModelAndView authResultRedirect(@PathVariable String type, UserVO userVO) {
        ModelAndView mnv = new ModelAndView(String.format("join/%s-auth-result.jsp", type));
        mnv.addObject("name", userVO.getName());
        mnv.addObject("phoneNum", userVO.getPhoneNum());
        mnv.addObject("userCid", userVO.getUserCid());
        mnv.addObject("encodeData", userVO.getEncodeData());

        return mnv;
    }

}

