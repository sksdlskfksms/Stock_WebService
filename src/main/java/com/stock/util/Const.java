package com.stock.util;

import java.text.SimpleDateFormat;

public interface Const {
    Integer FREE_CHARGE_PERIOD = 7;         // 무료체험기간
    Integer CHARGE_DAY         = 20;        // 정산일
    Integer CHARGE_PRICE       = 500;       // 일할정산금액
    Integer FIX_AMOUNT         = 15000;     // 정액요금

    enum TermType {
        SERVICE,
        PERSONAL_INFO,
        PERSONAL_INFO_SUPPLY;
    }

    enum JoinStatus {
        JOIN,
        WAIT,
        CANCEL,
        join,
        cancel,
        all;
    }

    enum PG {
        PAYCO,
        ALLAT,
        payco,
        allat;
    }

    SimpleDateFormat yyyyMMddHHmm_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
    SimpleDateFormat yyyy_MM_dd_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat yyyy_MM_FORMAT = new SimpleDateFormat("yyyy-MM");
}
