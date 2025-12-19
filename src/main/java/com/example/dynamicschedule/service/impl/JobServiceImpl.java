package com.example.dynamicschedule.service.impl;

import com.example.dynamicschedule.entity.ScheduledJob;
import com.example.dynamicschedule.quartz.QuartzJobService;
import com.example.dynamicschedule.repository.ScheduledJobRepository;
import com.example.dynamicschedule.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    
    @Autowired
    private ScheduledJobRepository scheduledJobRepository;
    
    @Autowired
    private QuartzJobService quartzJobService;
    
    @Override
    public ScheduledJob addJob(ScheduledJob job) {
        ScheduledJob savedJob = scheduledJobRepository.save(job);
        try {
            if (savedJob.getStatus() == 1) { // 只有运行中的任务才添加到调度器
                quartzJobService.addJob(savedJob);
            }
        } catch (Exception e) {
            // 如果添加到Quartz失败，回滚数据库操作
            scheduledJobRepository.delete(savedJob);
            throw new RuntimeException("添加任务到调度器失败", e);
        }
        return savedJob;
    }
    
    @Override
    public ScheduledJob updateJob(ScheduledJob job) {
        ScheduledJob existingJob = scheduledJobRepository.findById(job.getId()).orElse(null);
        if (existingJob == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 保存更新到数据库
        ScheduledJob savedJob = scheduledJobRepository.save(job);
        
        try {
            // 更新调度器中的任务
            if (savedJob.getStatus() == 1) { // 运行中的任务
                quartzJobService.updateJob(savedJob);
            } else { // 暂停的任务，从调度器中删除
                try {
                    quartzJobService.deleteJob(savedJob);
                } catch (Exception e) {
                    // 忽略删除失败的异常
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("更新调度器任务失败", e);
        }
        return savedJob;
    }
    
    @Override
    public void deleteJob(Long jobId) {
        ScheduledJob job = scheduledJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            try {
                quartzJobService.deleteJob(job);
            } catch (Exception e) {
                throw new RuntimeException("从调度器删除任务失败", e);
            }
            scheduledJobRepository.deleteById(jobId);
        }
    }
    
    @Override
    public void pauseJob(Long jobId) {
        ScheduledJob job = scheduledJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setStatus(0); // 暂停状态
            scheduledJobRepository.save(job);
            try {
                quartzJobService.pauseJob(job);
            } catch (Exception e) {
                // 如果暂停失败，尝试从调度器中删除任务
                try {
                    quartzJobService.deleteJob(job);
                } catch (Exception ex) {
                    throw new RuntimeException("暂停调度器任务失败", ex);
                }
            }
        }
    }
    
    @Override
    public void resumeJob(Long jobId) {
        ScheduledJob job = scheduledJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setStatus(1); // 运行状态
            scheduledJobRepository.save(job);
            try {
                // 恢复任务时，如果任务不存在则重新添加
                try {
                    quartzJobService.resumeJob(job);
                } catch (Exception e) {
                    // 如果恢复失败，可能是因为任务不存在，尝试重新添加
                    quartzJobService.addJob(job);
                }
            } catch (Exception e) {
                throw new RuntimeException("恢复调度器任务失败", e);
            }
        }
    }
    
    @Override
    public void runJob(Long jobId) {
        ScheduledJob job = scheduledJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            try {
                quartzJobService.runJob(job);
            } catch (Exception e) {
                throw new RuntimeException("立即执行任务失败", e);
            }
        }
    }
    
    @Override
    public List<ScheduledJob> getAllJobs() {
        List<ScheduledJob> jobs = scheduledJobRepository.findAll();
        // 同步数据库中的任务到调度器
        for (ScheduledJob job : jobs) {
            if (job.getStatus() == 1) { // 只同步运行中的任务
                try {
                    quartzJobService.addJob(job);
                } catch (Exception e) {
                    // 如果任务已存在则忽略
                }
            }
        }
        return jobs;
    }
    
    @Override
    public Page<ScheduledJob> getJobs(Pageable pageable) {
        return scheduledJobRepository.findAll(pageable);
    }
    
    @Override
    public ScheduledJob getJobById(Long jobId) {
        return scheduledJobRepository.findById(jobId).orElse(null);
    }
}