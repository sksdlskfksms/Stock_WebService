package com.stock.util;


import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderUtil {
    public static String getOrderCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
