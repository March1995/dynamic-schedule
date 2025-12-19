package com.example.dynamicschedule.repository;

import com.example.dynamicschedule.entity.JobExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, Long> {
    
    Page<JobExecutionLog> findByJobName(String jobName, Pageable pageable);
}