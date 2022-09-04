package com.stock;

import com.stock.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @Autowired
    private PaymentService paymentService;


    /**
     * 자동 결제 처리
     */
    @Scheduled(cron = "0 0 0 20 * *") // 매달 20일 자정
    public void paycoAutoPayment() {
        try{
            this.paymentService.fixAmountAutoPayment();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
