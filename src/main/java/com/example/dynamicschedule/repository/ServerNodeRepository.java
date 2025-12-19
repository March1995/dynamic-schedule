package com.example.dynamicschedule.repository;

import com.example.dynamicschedule.entity.ServerNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServerNodeRepository extends JpaRepository<ServerNode, Long> {
    
    Optional<ServerNode> findByNodeId(String nodeId);
    
    List<ServerNode> findByLastHeartbeatBefore(LocalDateTime dateTime);
    
    @Modifying
    @Transactional
    @Query("UPDATE ServerNode s SET s.isMaster = false")
    void resetAllMasters();
    
    @Modifying
    @Transactional
    @Query("UPDATE ServerNode s SET s.isMaster = true WHERE s.nodeId = ?1")
    void setMaster(String nodeId);
    
    @Query("SELECT s FROM ServerNode s WHERE s.isMaster = true")
    List<ServerNode> findMasterNodes();
}