package com.example.dynamicschedule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_execution_logs")
public class JobExecutionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;
    
    @Column(name = "job_group", nullable = false, length = 100)
    private String jobGroup;
    
    @Column(name = "bean_name", nullable = false, length = 100)
    private String beanName;
    
    @Column(name = "method_name", nullable = false, length = 100)
    private String methodName;
    
    @Column(name = "method_params", length = 255)
    private String methodParams;
    
    @Column(name = "status", nullable = false)
    private Integer status; // 1-成功，0-失败
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "duration", nullable = false)
    private Long duration;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public JobExecutionLog() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getJobName() {
        return jobName;
    }
    
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    
    public String getJobGroup() {
        return jobGroup;
    }
    
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
    
    public String getMethodName() {
        return methodName;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public String getMethodParams() {
        return methodParams;
    }
    
    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Long getDuration() {
        return duration;
    }
    
    public void setDuration(Long duration) {
        this.duration = duration;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}