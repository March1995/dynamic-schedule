-- 定时任务表
CREATE TABLE IF NOT EXISTS scheduled_jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL UNIQUE COMMENT '任务名称',
    job_group VARCHAR(100) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组',
    cron_expression VARCHAR(100) NOT NULL COMMENT 'cron表达式',
    bean_name VARCHAR(100) NOT NULL COMMENT '执行任务的bean名称',
    method_name VARCHAR(100) NOT NULL COMMENT '执行任务的方法名称',
    method_params VARCHAR(255) COMMENT '方法参数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '任务状态：1-运行中，0-已暂停',
    distributed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '执行模式：true-分布式执行，false-单机执行',
    remark VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 任务执行日志表
CREATE TABLE IF NOT EXISTS job_execution_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(100) NOT NULL COMMENT '任务组',
    bean_name VARCHAR(100) NOT NULL COMMENT '执行任务的bean名称',
    method_name VARCHAR(100) NOT NULL COMMENT '执行任务的方法名称',
    method_params VARCHAR(255) COMMENT '方法参数',
    status TINYINT NOT NULL COMMENT '执行状态：1-成功，0-失败',
    error_message TEXT COMMENT '错误信息',
    start_time TIMESTAMP NOT NULL COMMENT '开始执行时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束执行时间',
    duration BIGINT NOT NULL COMMENT '执行耗时（毫秒）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 服务节点表（用于分布式锁）
CREATE TABLE IF NOT EXISTS server_nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    node_id VARCHAR(100) NOT NULL UNIQUE COMMENT '节点唯一标识',
    ip_address VARCHAR(50) NOT NULL COMMENT 'IP地址',
    last_heartbeat TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后心跳时间',
    is_master BOOLEAN DEFAULT FALSE COMMENT '是否为主节点',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 创建索引
CREATE INDEX idx_scheduled_jobs_status ON scheduled_jobs(status);
CREATE INDEX idx_job_execution_logs_job_name ON job_execution_logs(job_name);
CREATE INDEX idx_job_execution_logs_created_at ON job_execution_logs(created_at);
CREATE INDEX idx_server_nodes_last_heartbeat ON server_nodes(last_heartbeat);

-- Quartz表结构已移至quartz_tables.sql文件中