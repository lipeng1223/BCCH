package com.bc.util;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Money extends BigDecimal {
    
    public Money(Float f){
        super(f);
        setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Money(Integer i){
        super(i);
        setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Money(Double d){
        super(d);
        setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
