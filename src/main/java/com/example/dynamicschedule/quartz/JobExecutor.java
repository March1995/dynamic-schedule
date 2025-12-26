package com.example.dynamicschedule.quartz;

import com.example.dynamicschedule.entity.JobExecutionLog;
import com.example.dynamicschedule.scheduler.MasterScheduler;
import com.example.dynamicschedule.service.JobExecutionLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JobExecutor implements Job {
    
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private MasterScheduler masterScheduler;
    
    @Autowired
    private JobExecutionLogService jobExecutionLogService;
    
    @Override
    public void execute(JobExecutionContext context) {
        // 获取任务是否为分布式执行模式
        String jobName = context.getJobDetail().getKey().getName();
        String jobGroup = context.getJobDetail().getKey().getGroup();
        
        // 检查是否应该执行任务（分布式锁）
        // 如果是分布式执行模式，则所有节点都可以执行；否则只有master节点执行
        Boolean distributed = (Boolean) context.getMergedJobDataMap().get("distributed");
        if (distributed == null) {
            distributed = false; // 默认为单机执行模式
        }
        
        if (!distributed && !masterScheduler.isMaster()) {
            logger.debug("当前节点不是master节点，跳过任务执行，任务名称: {}", jobName);
            return;
        }
        
        String beanName = context.getMergedJobDataMap().getString("beanName");
        String methodName = context.getMergedJobDataMap().getString("methodName");
        String methodParams = context.getMergedJobDataMap().getString("methodParams");
        
        // 执行任务并记录日志
        executeAndLogJob(jobName, jobGroup, beanName, methodName, methodParams);
    }
    
    private void executeAndLogJob(String jobName, String jobGroup, String beanName, String methodName, String methodParams) {
        JobExecutionLog log = new JobExecutionLog();
        log.setJobName(jobName);
        log.setJobGroup(jobGroup);
        log.setBeanName(beanName);
        log.setMethodName(methodName);
        log.setMethodParams(methodParams);
        log.setStartTime(LocalDateTime.now());
        
        long startTime = System.currentTimeMillis();
        boolean success = false;
        String errorMessage = null;
        
        try {
            // 执行任务（前置操作）
            executeJob(beanName, methodName, methodParams);
            success = true;
        } catch (Exception e) {
            errorMessage = e.getMessage();
            logger.error("执行定时任务失败: {}", e.getMessage());
        } finally {
            // 记录结束时间等信息（后置操作）
            long endTime = System.currentTimeMillis();
            log.setEndTime(LocalDateTime.now());
            log.setDuration(endTime - startTime);
            log.setStatus(success ? 1 : 0); // 1-成功，0-失败
            if (errorMessage != null) {
                log.setErrorMessage(errorMessage);
            }
            log.setCreatedAt(LocalDateTime.now());
            
            // 保存日志
            try {
                jobExecutionLogService.saveLog(log);
            } catch (Exception e) {
                logger.error("保存任务执行日志失败: ", e);
            }
        }
    }
    
    private void executeJob(String beanName, String methodName, String methodParams) throws Exception {
        // 检查Bean是否存在
        if (!applicationContext.containsBean(beanName)) {
            throw new RuntimeException("找不到指定的Bean: " + beanName);
        }
        
        Object target = applicationContext.getBean(beanName);
        java.lang.reflect.Method method;
        if (methodParams != null && !methodParams.isEmpty()) {
            method = target.getClass().getMethod(methodName, String.class);
            method.invoke(target, methodParams);
        } else {
            method = target.getClass().getMethod(methodName);
            method.invoke(target);
        }
    }
}