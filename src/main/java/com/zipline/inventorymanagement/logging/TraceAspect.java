package com.zipline.inventorymanagement.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
  Inspired by Medium and stack-overflow article -
  https://medium.com/nerd-for-tech/logging-made-easy-30bd10effa65
 */
@Aspect
@Component
public class TraceAspect {

    @Around("execution(* *(..)) && @annotation(com.zipline.inventorymanagement.logging.Traceable)")
    public Object aroundTraceableMethod(ProceedingJoinPoint joinPoint) throws Throwable {

        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        return executeWithTrace(joinPoint, logger);
    }

    private Object executeWithTrace(ProceedingJoinPoint joinPoint, Logger logger) throws Throwable {

        long start = System.currentTimeMillis();
        try {
            logger.info(String.format("#### - Starting execution of method '%s'",  joinPoint.toShortString()));
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error(String.format("#### - Error while executing method '%s' : %s", joinPoint.toShortString(), throwable));
            throw throwable;
        } finally {
            logger.info(String.format("#### - End of method '%s' (duration %s ms)", joinPoint.toShortString(), (System.currentTimeMillis() - start)));
        }
    }
}


