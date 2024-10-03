package uk.gov.hmcts.reform.opal.scheduler.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogExecutionTimeAspect {

    @Around("@annotation(uk.gov.hmcts.reform.opal.scheduler.aspect.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.nanoTime();
        Object result = joinPoint.proceed();
        long endTime = System.nanoTime();

        long executionTime = endTime - startTime;

        log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");

        return result;
    }
}
