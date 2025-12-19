package com.example.dynamicschedule.service.impl;

import com.example.dynamicschedule.entity.JobExecutionLog;
import com.example.dynamicschedule.repository.JobExecutionLogRepository;
import com.example.dynamicschedule.service.JobExecutionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class JobExecutionLogServiceImpl implements JobExecutionLogService {
    
    @Autowired
    private JobExecutionLogRepository jobExecutionLogRepository;
    
    @Override
    public JobExecutionLog saveLog(JobExecutionLog log) {
        return jobExecutionLogRepository.save(log);
    }
    
    @Override
    public Page<JobExecutionLog> getLogs(Pageable pageable) {
        // 默认按创建时间倒序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return jobExecutionLogRepository.findAll(sortedPageable);
    }
    
    @Override
    public Page<JobExecutionLog> getLogsByJobName(String jobName, Pageable pageable) {
        // 按创建时间倒序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return jobExecutionLogRepository.findByJobName(jobName, sortedPageable);
    }
}