package com.example.soup.aspect;

import com.example.soup.annotation.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 메서드 실행 시간을 측정하고 로깅하는 AOP Aspect
 * @LogExecutionTime 어노테이션이 붙은 메서드의 실행 시간을 측정합니다.
 */
@Aspect
@Component
@Slf4j
public class ExecutionTimeLogger {

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.info("🎯 [실행시간 측정] {} - {}ms", 
                    joinPoint.getSignature().toShortString(), 
                    executionTime);
            
            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            log.error("❌ [실행시간 측정] {} - {}ms (실패: {})", 
                    joinPoint.getSignature().toShortString(), 
                    executionTime, 
                    e.getMessage());
            
            throw e;
        }
    }
}



