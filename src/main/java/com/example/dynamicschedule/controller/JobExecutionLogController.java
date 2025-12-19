package com.example.dynamicschedule.controller;

import com.example.dynamicschedule.entity.JobExecutionLog;
import com.example.dynamicschedule.service.JobExecutionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/job-logs")
public class JobExecutionLogController {
    
    @Autowired
    private JobExecutionLogService jobExecutionLogService;
    
    /**
     * 分页查询执行日志
     */
    @GetMapping
    public Map<String, Object> getLogs(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<JobExecutionLog> logs = jobExecutionLogService.getLogs(pageable);
            result.put("success", true);
            result.put("data", logs.getContent());
            result.put("total", logs.getTotalElements());
            result.put("currentPage", page);
            result.put("totalPages", logs.getTotalPages());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据任务名称查询执行日志
     */
    @GetMapping("/job/{jobName}")
    public Map<String, Object> getLogsByJobName(@PathVariable String jobName,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<JobExecutionLog> logs = jobExecutionLogService.getLogsByJobName(jobName, pageable);
            result.put("success", true);
            result.put("data", logs.getContent());
            result.put("total", logs.getTotalElements());
            result.put("currentPage", page);
            result.put("totalPages", logs.getTotalPages());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}