package com.stock.controller;

import com.stock.exception.CommonRedirectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping(value = "/error", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView error(HttpServletRequest request){
        final Object exceptionObj = request.getAttribute("javax.servlet.error.exception");
        if( exceptionObj != null){
            Throwable e = ((Exception) exceptionObj).getCause();
            e.printStackTrace();
        }

        String msg = "다시 시도해주세요.";
        final String status = request.getAttribute("javax.servlet.error.status_code").toString();
        if(status.startsWith("4")){
            msg = "잘못된 접근입니다. 메인페이지로 이동합니다.";
        } else if(status.startsWith("5")){
            msg = "오류가 발생하여 메인페이지로 이동합니다.";
        }
        throw new CommonRedirectException(msg, "/main");
    }
}
