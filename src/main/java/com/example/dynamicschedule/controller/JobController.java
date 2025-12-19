package com.example.dynamicschedule.controller;

import com.example.dynamicschedule.entity.ScheduledJob;
import com.example.dynamicschedule.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    
    @Autowired
    private JobService jobService;
    
    /**
     * 创建定时任务
     */
    @PostMapping
    public Map<String, Object> createJob(@RequestBody ScheduledJob job) {
        Map<String, Object> result = new HashMap<>();
        try {
            ScheduledJob savedJob = jobService.addJob(job);
            result.put("success", true);
            result.put("data", savedJob);
            result.put("message", "任务创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "任务创建失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新定时任务
     */
    @PutMapping("/{id}")
    public Map<String, Object> updateJob(@PathVariable Long id, @RequestBody ScheduledJob job) {
        Map<String, Object> result = new HashMap<>();
        try {
            job.setId(id);
            ScheduledJob updatedJob = jobService.updateJob(job);
            result.put("success", true);
            result.put("data", updatedJob);
            result.put("message", "任务更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "任务更新失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除定时任务
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteJob(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            jobService.deleteJob(id);
            result.put("success", true);
            result.put("message", "任务删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "任务删除失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 暂停定时任务
     */
    @PostMapping("/{id}/pause")
    public Map<String, Object> pauseJob(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            jobService.pauseJob(id);
            result.put("success", true);
            result.put("message", "任务已暂停");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "暂停任务失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 恢复定时任务
     */
    @PostMapping("/{id}/resume")
    public Map<String, Object> resumeJob(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            jobService.resumeJob(id);
            result.put("success", true);
            result.put("message", "任务已恢复");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "恢复任务失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 立即执行任务
     */
    @PostMapping("/{id}/run")
    public Map<String, Object> runJob(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            jobService.runJob(id);
            result.put("success", true);
            result.put("message", "任务已启动");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "执行任务失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 查询所有任务
     */
    @GetMapping
    public Map<String, Object> getAllJobs() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ScheduledJob> jobs = jobService.getAllJobs();
            result.put("success", true);
            result.put("data", jobs);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询任务列表失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 分页查询任务
     */
    @GetMapping("/page")
    public Map<String, Object> getJobs(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> result = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ScheduledJob> jobs = jobService.getJobs(pageable);
            result.put("success", true);
            result.put("data", jobs.getContent());
            result.put("total", jobs.getTotalElements());
            result.put("currentPage", page);
            result.put("totalPages", jobs.getTotalPages());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "分页查询任务失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据ID获取任务
     */
    @GetMapping("/{id}")
    public Map<String, Object> getJobById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            ScheduledJob job = jobService.getJobById(id);
            if (job != null) {
                result.put("success", true);
                result.put("data", job);
            } else {
                result.put("success", false);
                result.put("message", "任务不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询任务失败: " + e.getMessage());
        }
        return result;
    }
}