package com.exchangerate.aspect;

import com.exchangerate.AntiShakeUtil;
import com.exchangerate.annotation.AntiShake;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


@Aspect
public class AntiShakeAspect {

    @Pointcut("execution(@com.exchangerate.annotation.AntiShake * *(..))")
    public void executeAntiShakeBeforeAspect() {}

    @Around("executeAntiShakeBeforeAspect()")
    public Object beforeJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            final Method currentMethod = methodSignature.getMethod();
            final AntiShake aspectJAnnotation = currentMethod.getAnnotation(AntiShake.class);
            if (AntiShakeUtil.isFastDoubleClick(aspectJAnnotation.intervalTime())) {
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        result = joinPoint.proceed();
        return result;
    }

}