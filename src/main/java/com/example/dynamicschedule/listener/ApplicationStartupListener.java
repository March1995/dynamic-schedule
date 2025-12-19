package com.example.dynamicschedule.listener;

import com.example.dynamicschedule.entity.ScheduledJob;
import com.example.dynamicschedule.quartz.QuartzJobService;
import com.example.dynamicschedule.repository.ScheduledJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    @Autowired
    private ScheduledJobRepository scheduledJobRepository;
    
    @Autowired
    private QuartzJobService quartzJobService;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 确保只在主应用上下文启动时执行一次
        if (event.getApplicationContext().getParent() == null) {
            logger.info("应用启动完成，开始加载数据库中的定时任务到调度器");
            loadJobsToScheduler();
        }
    }
    
    /**
     * 将数据库中处于运行状态的任务加载到调度器中
     */
    private void loadJobsToScheduler() {
        try {
            // 查询所有处于运行状态的任务
            List<ScheduledJob> runningJobs = scheduledJobRepository.findByStatus(1);
            logger.info("找到 {} 个运行中的任务需要加载到调度器", runningJobs.size());
            
            for (ScheduledJob job : runningJobs) {
                try {
                    quartzJobService.addJob(job);
                    logger.info("成功加载任务到调度器: {}", job.getJobName());
                } catch (Exception e) {
                    logger.error("加载任务到调度器失败: {}", job.getJobName(), e);
                }
            }
            
            logger.info("任务加载完成");
        } catch (Exception e) {
            logger.error("加载任务到调度器时发生错误", e);
        }
    }
}