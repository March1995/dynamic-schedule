package com.example.dynamicschedule.service;

import com.example.dynamicschedule.entity.JobExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface JobExecutionLogService {
    
    /**
     * 保存执行日志
     */
    JobExecutionLog saveLog(JobExecutionLog log);
    
    /**
     * 分页查询执行日志（默认按创建时间倒序）
     */
    Page<JobExecutionLog> getLogs(Pageable pageable);
    
    /**
     * 根据任务名称查询执行日志（默认按创建时间倒序）
     */
    Page<JobExecutionLog> getLogsByJobName(String jobName, Pageable pageable);
}