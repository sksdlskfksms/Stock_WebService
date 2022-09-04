package com.stock.service;

import com.stock.util.Const;
import com.stock.vo.auth.AuthResultVO;

import javax.servlet.http.HttpSession;

public interface AuthService {

    String authRequest(HttpSession session, Const.JoinStatus type) throws Exception;

    AuthResultVO authResponse(String type, String sEncodeData, String sRequestNumber) throws Exception;


}
