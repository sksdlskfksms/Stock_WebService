package com.stock.service;

import com.stock.util.Const;
import com.stock.vo.UserVO;
import com.stock.vo.allat.AllatVO;
import com.stock.vo.payco.PaycoReserveInfoVO;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface PaymentService {

    ResponseEntity<?> reserveAutoPayment(UserVO userVO, Const.PG pg) throws Exception;

    void payment(Const.PG pg, UserVO userVO, PaycoReserveInfoVO paycoReserveInfoVO, AllatVO allatVO) throws Exception;

    void fixAmountAutoPayment() throws Exception;

    void cancelPayment(String userCid, Const.JoinStatus status) throws Exception;

    int calculateAmount(Const.JoinStatus typeEnum, Date joinDate);

}
