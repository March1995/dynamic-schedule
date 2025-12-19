package com.example.dynamicschedule.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("testJobBean")
public class TestJobBean {
    
    private static final Logger logger = LoggerFactory.getLogger(TestJobBean.class);
    
    public void execute() {
        logger.info("执行测试任务: 无参数");
        System.out.println("执行测试任务: 无参数");
    }
    
    public void execute(String params) {
        logger.info("执行测试任务: 参数 = " + params);
        System.out.println("执行测试任务: 参数 = " + params);
    }
}