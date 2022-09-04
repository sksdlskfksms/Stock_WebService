package com.stock.service;

import com.stock.vo.PaymentCancelVO;
import com.stock.vo.PaymentVO;
import com.stock.vo.UserVO;
import com.stock.vo.allat.AllatApproveReqVO;

import java.util.Map;

public interface AllatService {

    Map<String, String> allatPaymentRequestParam(UserVO userVO);

    Map<String, String> allatCancelRequestParam(String userCid);

    AllatApproveReqVO allatPaymentRequest(String allatEncData) throws Exception;

    PaymentVO allatPaymentApprove(UserVO userVO, String orderCode, String cardKey);

    PaymentCancelVO allatRefund(PaymentCancelVO paymentCancelVO);

    PaymentCancelVO allatDeleteInfo(PaymentCancelVO paymentCancelVO, String cardKey);

}
