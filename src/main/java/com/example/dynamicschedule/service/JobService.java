package com.example.dynamicschedule.service;

import com.example.dynamicschedule.entity.ScheduledJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    
    /**
     * 添加定时任务
     */
    ScheduledJob addJob(ScheduledJob job);
    
    /**
     * 更新定时任务
     */
    ScheduledJob updateJob(ScheduledJob job);
    
    /**
     * 删除定时任务
     */
    void deleteJob(Long jobId);
    
    /**
     * 暂停定时任务
     */
    void pauseJob(Long jobId);
    
    /**
     * 恢复定时任务
     */
    void resumeJob(Long jobId);
    
    /**
     * 立即执行任务
     */
    void runJob(Long jobId);
    
    /**
     * 查询所有任务
     */
    List<ScheduledJob> getAllJobs();
    
    /**
     * 分页查询任务
     */
    Page<ScheduledJob> getJobs(Pageable pageable);
    
    /**
     * 根据ID获取任务
     */
    ScheduledJob getJobById(Long jobId);
}