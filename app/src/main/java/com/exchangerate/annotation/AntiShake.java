package com.exchangerate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 防抖处理
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD })
public @interface AntiShake {
    int intervalTime()default 300;
}
