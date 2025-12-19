package com.example.dynamicschedule.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "server_nodes")
public class ServerNode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "node_id", nullable = false, unique = true, length = 100)
    private String nodeId;
    
    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "is_master")
    private Boolean isMaster = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public ServerNode() {}
    
    public ServerNode(String nodeId, String ipAddress) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.lastHeartbeat = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public Boolean getIsMaster() {
        return isMaster;
    }
    
    public void setIsMaster(Boolean master) {
        isMaster = master;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}