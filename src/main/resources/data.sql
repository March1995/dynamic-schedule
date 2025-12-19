-- 初始化测试数据
INSERT INTO scheduled_jobs (job_name, job_group, cron_expression, bean_name, method_name, method_params, status, distributed, remark) 
VALUES 
('testJob', 'DEFAULT', '0/10 * * * * ?', 'testJobBean', 'execute', 'test params', 1, FALSE, '测试任务'),
('distributedTestJob', 'DEFAULT', '0/30 * * * * ?', 'testJobBean', 'execute', 'distributed params', 1, TRUE, '分布式测试任务');

-- 初始化服务节点数据（用于测试）
INSERT INTO server_nodes (node_id, ip_address, last_heartbeat, is_master) 
VALUES 
('node1', '127.0.0.1', CURRENT_TIMESTAMP, TRUE);