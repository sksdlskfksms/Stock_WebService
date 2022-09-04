package com.stock.mapper;

import com.stock.core.anotation.Mapper;
import com.stock.vo.PaymentCancelVO;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CancelMapper {

    void saveCancelPaymentInfo(PaymentCancelVO paymentCancelVO);

}
