package com.example.dynamicschedule.quartz;

import com.example.dynamicschedule.entity.ScheduledJob;
import com.example.dynamicschedule.scheduler.MasterScheduler;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributedQuartzJobService {
    
    private static final Logger logger = LoggerFactory.getLogger(DistributedQuartzJobService.class);
    
    @Autowired
    private Scheduler scheduler;
    
    @Autowired
    private MasterScheduler masterScheduler;
    
    /**
     * 添加定时任务到Quartz调度器（带分布式锁检查）
     */
    public void addJob(ScheduledJob job) throws SchedulerException {
        // 在分布式环境中，所有节点都需要注册任务，但只有master节点会真正执行
        JobDetail jobDetail = JobBuilder.newJob(JobExecutor.class)
                .withIdentity(job.getJobName(), job.getJobGroup())
                .usingJobData("beanName", job.getBeanName())
                .usingJobData("methodName", job.getMethodName())
                .usingJobData("methodParams", job.getMethodParams())
                .usingJobData("distributed", true)
                .build();
        
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(job.getJobName() + "Trigger", job.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                .build();
        
        scheduler.scheduleJob(jobDetail, cronTrigger);
        logger.info("任务 {} 已添加到调度器", job.getJobName());
    }
    
    /**
     * 执行任务前检查是否为master节点
     */
    public boolean shouldExecuteJob() {
        // 检查当前节点是否为master节点
        boolean isMaster = masterScheduler.isMaster();
        if (!isMaster) {
            logger.debug("当前节点不是master节点，跳过任务执行");
        }
        return isMaster;
    }
}