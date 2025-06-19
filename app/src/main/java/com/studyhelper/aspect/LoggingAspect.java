package com.studyhelper.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.studyhelper.service.*.*(..))")
    public Object logServices(ProceedingJoinPoint pjp) throws Throwable {
        try {
            log.atDebug().log("Метод {} вызван", pjp.toLongString());
            var startTime = System.currentTimeMillis();
            Object result = pjp.proceed(pjp.getArgs());
            var endTime = System.currentTimeMillis();
            log.atInfo().log("Метод {} выполнялся {}ms", pjp.getSignature().toShortString(), endTime - startTime);
            return result;
        } catch (Throwable t) {
            log.atError().log("Возникло исключение", t);
            throw t;
        } finally {
            log.atDebug().log("Метод {} завершился", pjp.toShortString());
        }
    }
}
