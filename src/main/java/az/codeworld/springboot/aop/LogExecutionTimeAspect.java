package az.codeworld.springboot.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionTimeAspect {
    private static final Logger log = LoggerFactory.getLogger(LogExecutionTimeAspect.class);

    @Pointcut("@annotation(com.example.demo.aop.LogExecutionTime)")
    public void logExecutionTimePointcut() {}

    @Around("logExecutionTimePointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            Object result = pjp.proceed();
            long timeMs = (System.nanoTime() - start) / 1_000_000;
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            log.info("Method {}.{} completed in {} ms",
                        signature.getDeclaringType().getSimpleName(),
                        signature.getName(),
                        timeMs);
            return result;
        } catch (Throwable ex) {
            long timeMs = (System.nanoTime() - start) / 1_000_000;
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            log.warn("Method {}.{} failed after {} ms with {}: {}",
                    signature.getDeclaringType().getSimpleName(),
                    signature.getName(), timeMs,
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
