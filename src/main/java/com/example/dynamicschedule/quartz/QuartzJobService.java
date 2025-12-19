package com.example.dynamicschedule.quartz;

import com.example.dynamicschedule.entity.ScheduledJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuartzJobService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuartzJobService.class);
    
    @Autowired
    private Scheduler scheduler;
    
    /**
     * 添加定时任务到Quartz调度器
     */
    public void addJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        
        // 检查任务是否已存在
        if (!scheduler.checkExists(jobKey)) {
            // 创建JobDetail
            JobDetail jobDetail = JobBuilder.newJob(JobExecutor.class)
                    .withIdentity(jobKey)
                    .usingJobData("beanName", job.getBeanName())
                    .usingJobData("methodName", job.getMethodName())
                    .usingJobData("methodParams", job.getMethodParams())
                    .usingJobData("distributed", job.getDistributed() != null ? job.getDistributed() : false)
                    .build();
            
            // 创建CronTrigger
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(job.getJobName() + "Trigger", job.getJobGroup())
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();
            
            // 调度任务
            scheduler.scheduleJob(jobDetail, cronTrigger);
            logger.info("成功添加任务: {}", job.getJobName());
        } else {
            logger.warn("任务已存在，跳过添加: {}", job.getJobName());
        }
    }
    
    /**
     * 更新定时任务
     */
    public void updateJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName() + "Trigger", job.getJobGroup());
        
        // 检查任务是否存在
        if (scheduler.checkExists(jobKey)) {
            // 任务存在，更新触发器
            CronTrigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                    .build();
            
            scheduler.rescheduleJob(triggerKey, newTrigger);
            logger.info("成功更新任务触发器: {}", job.getJobName());
        } else {
            // 任务不存在，创建新任务
            addJob(job);
        }
        
        // 更新任务的JobDataMap（如果需要）
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail != null) {
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put("beanName", job.getBeanName());
            jobDataMap.put("methodName", job.getMethodName());
            jobDataMap.put("methodParams", job.getMethodParams());
            jobDataMap.put("distributed", job.getDistributed() != null ? job.getDistributed() : false);
            
            // 使用新的JobDetail替换旧的
            JobDetail newJobDetail = JobBuilder.newJob(JobExecutor.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .build();
            
            scheduler.addJob(newJobDetail, true, true);
            logger.info("成功更新任务详情: {}", job.getJobName());
        }
    }
    
    /**
     * 删除定时任务
     */
    public void deleteJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName() + "Trigger", job.getJobGroup());
        
        scheduler.pauseTrigger(triggerKey); // 停止触发器
        scheduler.unscheduleJob(triggerKey); // 移除触发器
        scheduler.deleteJob(jobKey); // 删除任务
        logger.info("成功删除任务: {}", job.getJobName());
    }
    
    /**
     * 暂停定时任务
     */
    public void pauseJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        scheduler.pauseJob(jobKey);
        logger.info("成功暂停任务: {}", job.getJobName());
    }
    
    /**
     * 恢复定时任务
     */
    public void resumeJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        // 检查任务是否存在，如果不存在则重新创建
        if (!scheduler.checkExists(jobKey)) {
            addJob(job);
        }
        scheduler.resumeJob(jobKey);
        logger.info("成功恢复任务: {}", job.getJobName());
    }
    
    /**
     * 立即执行任务
     */
    public void runJob(ScheduledJob job) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());
        // 检查任务是否存在，如果不存在则重新创建
        if (!scheduler.checkExists(jobKey)) {
            addJob(job);
        }
        scheduler.triggerJob(jobKey);
        logger.info("成功触发任务: {}", job.getJobName());
    }
}