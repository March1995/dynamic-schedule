package com.example.dynamicschedule.jobhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("testJobHandler")
public class TestJobHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(TestJobHandler.class);
    
    public void execute() {
        logger.info("执行测试任务: 当前时间 {}", System.currentTimeMillis());
        // 模拟一些工作
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void executeWithParams(String params) {
        logger.info("执行带参数的测试任务: 参数={}, 当前时间={}", params, System.currentTimeMillis());
        // 模拟一些工作
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}