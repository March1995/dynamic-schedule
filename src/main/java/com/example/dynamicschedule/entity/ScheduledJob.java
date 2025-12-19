package com.example.dynamicschedule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_jobs")
public class ScheduledJob {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "job_name", nullable = false, unique = true, length = 100)
    private String jobName;
    
    @Column(name = "job_group", nullable = false, length = 100)
    private String jobGroup = "DEFAULT";
    
    @Column(name = "cron_expression", nullable = false, length = 100)
    private String cronExpression;
    
    @Column(name = "bean_name", nullable = false, length = 100)
    private String beanName;
    
    @Column(name = "method_name", nullable = false, length = 100)
    private String methodName;
    
    @Column(name = "method_params", length = 255)
    private String methodParams;
    
    @Column(name = "status", nullable = false)
    private Integer status = 1; // 1-运行中，0-已暂停
    
    @Column(name = "distributed", nullable = false)
    private Boolean distributed = false; // true-分布式执行，false-单机执行
    
    @Column(name = "remark", length = 500)
    private String remark;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public ScheduledJob() {}
    
    public ScheduledJob(String jobName, String cronExpression, String beanName, String methodName) {
        this.jobName = jobName;
        this.cronExpression = cronExpression;
        this.beanName = beanName;
        this.methodName = methodName;
    }
    
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
    
    public String getCronExpression() {
        return cronExpression;
    }
    
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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
    
    public Boolean getDistributed() {
        return distributed;
    }
    
    public void setDistributed(Boolean distributed) {
        this.distributed = distributed;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}