package org.example.service;

import jakarta.transaction.Transactional;
import org.example.entity.AuditLog;
import org.example.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logChange(String action, Long entityId, String entityType, String oldValue, String newValue, String user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityId(entityId);
        log.setEntityType(entityType);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setChangedBy(user);
        log.setTimestamp(new Date()); // assuming you want a timestamp
        auditLogRepository.save(log);
    }
}
