package com.bc.util;

import java.math.BigDecimal;

public class MoneyRound {

    public static double round(BigDecimal bd){
        BigDecimal step1 = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
        BigDecimal step2 = step1.setScale(2, BigDecimal.ROUND_HALF_UP);
        return step2.doubleValue();
    }
}
