package com.imooc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class ServiceLogAspect {

    public static final Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP通知:
     * 1.前置通知：在方法调用之前通知
     * 2.后置通知：在方法调用之后通知
     * 3.环绕通知：在方法调用之前和之后，都分别可以通知
     * 4.异常通知：如果方法调用过程中发生异常，则通知
     * 5.最终通知：在方法调用之后执行
     */
    /**
     * 切面表达式
     * execution 代表所要执行的表达式主题
     * 第一处：* 代表方法的返回值 *代表所有类型
     * 第二处：包名代表aop监控的类所在的包
     * 第三处：..代表该包以及其子包下的所有的类
     * 第四处：*代表类名，*代表所有类
     * 第五处：*(..) *代表类中的方法名，(..)表示方法中的任何参数
     *
     * @param joinPoint
     * @return
     */
    @Around("execution(* com.imooc.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("======开始执行 {}.{}======",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName());

        //开始时间
        long begin = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        //结束时间
        long end = System.currentTimeMillis();

        long takeTime = end - begin;

        if (takeTime > 10000) {
            log.error("执行结束，耗时:{}", takeTime);
        } else if (takeTime> 5000) {
            log.warn("执行结束，耗时:{}", takeTime);
        } else {
            log.info("执行结束，耗时:{}", takeTime);
        }

        return result;

    }
}
