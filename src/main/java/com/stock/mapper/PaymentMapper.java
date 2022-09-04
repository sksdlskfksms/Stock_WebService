package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.PaymentVO;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaymentMapper {

    void saveAutoPaymentInfo(PaymentVO paymentVO);

    PaymentVO selectPayment(Long reserveId);

}
