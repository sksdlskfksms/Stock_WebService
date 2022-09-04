package com.stock.controller;

import com.stock.exception.CommonRedirectException;
import com.stock.exception.ProcessException;
import com.stock.service.AllatService;
import com.stock.service.CommonService;
import com.stock.service.PaymentService;
import com.stock.util.Const;
import com.stock.vo.UserVO;
import com.stock.vo.allat.AllatVO;
import com.stock.vo.payco.PaycoReserveInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(value = "/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AllatService allatService;

    @Autowired
    private CommonService commonService;


    /**
     * 해당 매체의 제휴 PG사 체크
     * @param mediaKey
     * @return
     */
    @GetMapping(value = "/pg")
    public ResponseEntity<String> findPartnerPg(@RequestParam String mediaKey){
        try{
            String pg = this.commonService.findMediaInfoByMediaKey(mediaKey).getPartnerPg();
            if (pg == null) pg = Const.PG.ALLAT.name();

            return new ResponseEntity<>(pg, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    /**
     * 자동결제 등록 정보 생성
     * 1. User & Reserve 데이터 생성
     * 2. 결제 팝업창 호출시 필요한 정보 생성
     */
    @PostMapping(value = "/{pg}/regist")
    public ResponseEntity<?> registration(@PathVariable(value = "pg") Const.PG pg, @RequestBody UserVO userVO){
        try{
            return this.paymentService.reserveAutoPayment(userVO, pg);
        }catch (ProcessException pe){
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", "99");
            resultMap.put("msg", pe.getMessage());
            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 올앳 암호화 데이터 전달
     */
    @PostMapping(value = "/allat/receive")
    public ModelAndView allatReceiveKey(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("euc-kr");

        String sResultCd = request.getParameter("allat_result_cd");
        String sResultMsg = request.getParameter("allat_result_msg");
        String sEncData = request.getParameter("allat_enc_data");

        log.info("---[ 올앳 인증 결과 ]-----------------------------------------------------------------------------------");
        log.info("sResultCd = {}, , sResultMsg = {}", sResultCd, sResultMsg);
        log.info("sEncData = {}", sEncData);
        log.info("-----------------------------------------------------------------------------------------------------");

        ModelAndView mnv = new ModelAndView("/redirect/allat-receive.jsp");
        mnv.addObject("sResultCd", sResultCd);
        mnv.addObject("sResultMsg", sResultMsg);
        mnv.addObject("sEncData", sEncData);

        return mnv;

    }


    /*
     * 가입 요금 결제
     * (일할 결제)
     */
    @RequestMapping(value = "/{pg}", method = {RequestMethod.POST, RequestMethod.GET})
    public String payment(@PathVariable(value = "pg") Const.PG pg, UserVO userVO, AllatVO allatVO,
                          PaycoReserveInfoVO paycoReserveInfoVO, RedirectAttributes redirect) {
        try{
            this.paymentService.payment(pg, userVO, paycoReserveInfoVO, allatVO);
            redirect.addAttribute("userCid", userVO.getUserCid());
            return "redirect:/user/join/result";
        }catch (Exception e){
            log.error("---[ 결제 진행 중 오류 발생 ]--------------------------------------------------------");
            log.error("UserCid = {}", userVO.getUserCid());
            log.error("----------------------------------------------------------------------------------");
            e.printStackTrace();
            throw new CommonRedirectException("오류가 발생했습니다.", "/user/join?error=true");
        }
    }


    /**
     * 올앳 자동결제 삭제 요청
     */
    @PostMapping(value = "/allat/cancel/request")
    public ResponseEntity<?> allatCancelRequest(@RequestBody UserVO userVO) {
        try{
            Map<String, String> result = this.allatService.allatCancelRequestParam(userVO.getUserCid());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 자동결제 취소 및 정보삭제
     */
    @PostMapping(value = "/cancel")
    public ResponseEntity<?> cancel(@RequestBody UserVO userVO) {
        try{
            this.paymentService.cancelPayment(userVO.getUserCid(), Const.JoinStatus.CANCEL);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (ProcessException pe){
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", "99");
            resultMap.put("msg", pe.getMessage());
            return new ResponseEntity<>(resultMap, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 가입진행 중 오류로 인한 올앳 결제 취소
     */
    @GetMapping(value = "/allat/error")
    public ModelAndView allatErrorRedirec(@RequestParam String userCid) {
        log.info("[가입진행 중 오류로 인한 올앳 결제 취소 진행중]");

        ModelAndView mnv = new ModelAndView("/redirect/allat-error-redirect.jsp");
        mnv.addObject("userCid", userCid);
        return mnv;
    }

}