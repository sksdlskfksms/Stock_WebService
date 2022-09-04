package com.stock.controller;

import com.stock.vo.UserVO;
import com.stock.exception.CommonRedirectException;
import com.stock.service.EMoneyService;
import com.stock.service.UserService;
import com.stock.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EMoneyService eMoneyService;


    /**
     * 가입 / 해지 페이지
     * @return
     */
    @GetMapping(value = "/{type}")
    public String joinAndCancel(@PathVariable(value = "type") Const.JoinStatus type) {
        if(type.equals(Const.JoinStatus.join)){
            return "join/join.jsp";
        }else if(type.equals(Const.JoinStatus.cancel)){
            return "join/cancel.jsp";
        }else {
            throw new CommonRedirectException("잘못된 접근입니다.", "/main");
        }
    }


    /**
     * 가입 / 해지 완료 페이지
     */
    @GetMapping(value = "/{type}/result")
    public ModelAndView joinAndCancelResult(@RequestParam String userCid, @PathVariable String type) {
        try{
            UserVO userVO = this.userService.selectUserByUserCid(userCid, type);

            ModelAndView mnv = new ModelAndView(String.format("join/%s-result.jsp", type));
            mnv.addObject("name", userVO.getName());
            mnv.addObject("phoneNum", userVO.getPhoneNum() );
            mnv.addObject("joinDate", userVO.getDateToStringFormat(userVO.getJoinDate()));
            mnv.addObject("cancelDate", userVO.getDateToStringFormat(userVO.getCancelDate()));

            return mnv;
        }catch (NullPointerException n){
            throw new CommonRedirectException("해당하는 유저 정보가 없습니다.", "/main");
        }catch (Exception e){
            e.printStackTrace();
            throw new CommonRedirectException("오류가 발생했습니다.", "/main");
        }

    }



    /**
     * 회원정보 업데이트
     * (운영상 필요시 사용을 위해 임시로 API 형태로 개발)
     */
    @GetMapping(value = "/modify")
    public String modify(@RequestParam String userCid, @RequestParam String phoneNum) {
        try{
            UserVO userVO = this.userService.selectUserByUserCid(userCid, Const.JoinStatus.JOIN.name());
            userVO.setPhoneNum(phoneNum);

            this.eMoneyService.modifyEMoney(userVO);
            this.userService.updatePhoneOfUser(userCid, phoneNum);

            return "SUCCESS";
        } catch (NullPointerException n){
            n.printStackTrace();
            return "사용자 정보 없음";
        } catch (Exception e){
            e.printStackTrace();
            return "FAIL";
        }

    }

}

