package com.exchangerate.aspect;

import com.exchangerate.AntiShakeUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


@Aspect
public class AntiShakeOnClickAspect {
    @Pointcut(value = "execution(@butterknife.OnClick * *(..))")
    public void methodAnnotatedWithButterknifeOnClick() {}

    @Around("methodAnnotatedWithButterknifeOnClick()")
    public Object beforeJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            final Method currentMethod = methodSignature.getMethod();
            if (AntiShakeUtil.isFastDoubleClick(AntiShakeUtil.mIntDefaultTime)) {
                return result;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        result = joinPoint.proceed();
        return result;
    }
}