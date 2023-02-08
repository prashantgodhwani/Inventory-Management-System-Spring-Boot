package com.zipline.inventorymanagement.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;


@Profile("with-trace-aop")
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationTraceableAspect extends TraceAspect {

    @PostConstruct
    public void init(){
        Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.warn("###-AOP-### ApplicationTraceableAspect started for this application");
    }
}
