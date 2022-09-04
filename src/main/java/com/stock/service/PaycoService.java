package com.stock.service;

import com.stock.vo.PaymentCancelVO;
import com.stock.vo.PaymentVO;
import com.stock.vo.ReserveVO;
import com.stock.vo.UserVO;
import com.stock.vo.payco.PaycoReserveInfoReturnVO;
import com.stock.vo.payco.PaycoReserveInfoVO;

public interface PaycoService {

    String paycoReserve(UserVO userVO, ReserveVO reserveVO) throws Exception;

    PaycoReserveInfoReturnVO paycoFindReserveInfo(PaycoReserveInfoVO paycoReturnVO) throws Exception;

    PaymentVO paycoPayment(ReserveVO reserveVO, String orderCode, int amount) throws Exception;

    PaymentCancelVO paycoRefund(PaymentCancelVO paymentCancelVO, String certifyKey) throws Exception;

    PaymentCancelVO paycoDeleteInfo(PaymentCancelVO paymentCancelVO, String certifyKey) throws Exception;
}
