package com.bc.util;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionRole {
    String[] value() default "";
}
