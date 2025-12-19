package com.example.dynamicschedule.repository;

import com.example.dynamicschedule.entity.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    
    Optional<ScheduledJob> findByJobName(String jobName);
    
    List<ScheduledJob> findByStatus(Integer status);
    
    @Query("SELECT s FROM ScheduledJob s WHERE s.status = 1")
    List<ScheduledJob> findActiveJobs();
}