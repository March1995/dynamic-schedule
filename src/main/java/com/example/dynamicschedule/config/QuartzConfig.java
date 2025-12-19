package com.example.dynamicschedule.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    private final DataSource dataSource;

    public QuartzConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Quartz配置
    public Properties quartzProperties() {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "DynamicScheduleScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        
        // JobStore配置
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.H2Delegate");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        
        // ThreadPool配置
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        
        return properties;
    }
}