package com.example.dynamicschedule.scheduler;

import com.example.dynamicschedule.entity.ServerNode;
import com.example.dynamicschedule.repository.ServerNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class MasterScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterScheduler.class);
    
    @Autowired
    private ServerNodeRepository serverNodeRepository;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    private String nodeId;
    private String ipAddress;
    
    public MasterScheduler() {
        try {
            // 初始化节点ID和IP地址
            this.nodeId = UUID.randomUUID().toString();
            this.ipAddress = InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
        } catch (UnknownHostException e) {
            this.nodeId = UUID.randomUUID().toString();
            this.ipAddress = "unknown:" + serverPort;
            logger.error("无法获取本地IP地址", e);
        }
    }
    
    /**
     * 定期发送心跳并检查是否需要成为master节点
     */
//    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void heartbeat() {
        try {
            // 更新自己的心跳时间
            ServerNode node = serverNodeRepository.findByNodeId(nodeId).orElse(null);
            if (node == null) {
                node = new ServerNode(nodeId, ipAddress);
                node.setIsMaster(false);
            }
            node.setLastHeartbeat(LocalDateTime.now());
            serverNodeRepository.save(node);
            
            // 检查是否有master节点
            List<ServerNode> masters = serverNodeRepository.findMasterNodes();
            if (masters.isEmpty()) {
                // 如果没有master节点，则尝试成为master
                becomeMaster();
            } else {
                // 检查master节点是否还活着（15秒内有心跳）
                boolean masterAlive = false;
                for (ServerNode master : masters) {
                    if (master.getLastHeartbeat().isAfter(LocalDateTime.now().minusSeconds(15))) {
                        masterAlive = true;
                        break;
                    }
                }
                
                // 如果master节点不活跃，则尝试成为新的master
                if (!masterAlive) {
                    becomeMaster();
                }
            }
            
            // 清理过期的节点（30秒内无心跳）
            LocalDateTime expiryTime = LocalDateTime.now().minusSeconds(30);
            List<ServerNode> expiredNodes = serverNodeRepository.findByLastHeartbeatBefore(expiryTime);
            if (!expiredNodes.isEmpty()) {
                serverNodeRepository.deleteAll(expiredNodes);
                logger.info("清理了 {} 个过期节点", expiredNodes.size());
            }
            
        } catch (Exception e) {
            logger.error("心跳检测异常", e);
        }
    }
    
    /**
     * 尝试成为master节点
     */
    @Transactional
    public void becomeMaster() {
        try {
            // 先重置所有master标记
            serverNodeRepository.resetAllMasters();
            // 设置自己为master
            serverNodeRepository.setMaster(nodeId);
            logger.info("节点 {} 成为master节点", nodeId);
        } catch (Exception e) {
            logger.error("成为master节点失败", e);
        }
    }
    
    /**
     * 判断当前节点是否为master节点
     */
    @Transactional(readOnly = true)
    public boolean isMaster() {
        try {
            ServerNode node = serverNodeRepository.findByNodeId(nodeId).orElse(null);
            return node != null && node.getIsMaster() != null && node.getIsMaster();
        } catch (Exception e) {
            logger.error("检查master状态异常", e);
            return false;
        }
    }
}