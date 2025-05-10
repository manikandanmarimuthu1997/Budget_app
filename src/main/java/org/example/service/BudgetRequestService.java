package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.example.RequestStatus;
import org.example.entity.BudgetRequest;
import org.example.entity.Department;
import org.example.repository.BudgetRequestRepository;
import org.example.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
public class BudgetRequestService {

    private final BudgetRequestRepository budgetRequestRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    public BudgetRequestService(
            BudgetRequestRepository budgetRequestRepository,
            DepartmentRepository departmentRepository,
            AuditLogService auditLogService
    ) {
        this.budgetRequestRepository = budgetRequestRepository;
        this.departmentRepository = departmentRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public BudgetRequest submitRequest(BudgetRequest br) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<BudgetRequest> duplicates = budgetRequestRepository.findDuplicates(
                br.getPurpose(),
                br.getRequestedBy(),
                sevenDaysAgo
        );

        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException("Duplicate request within 7 days.");
        }

        if (br.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested amount must be positive.");
        }

        BigDecimal max = br.getDepartment().getYearlyAllocation().multiply(new BigDecimal("0.10"));
        if (br.getRequestedAmount().compareTo(max) > 0) {
            throw new IllegalArgumentException("Amount exceeds 10% of allocation.");
        }

        BudgetRequest saved = budgetRequestRepository.save(br);
        auditLogService.logChange("CREATE", saved.getId(), "BudgetRequest", null, saved.toString(), br.getRequestedBy());
        return saved;
    }

    @Transactional
    public BudgetRequest approveRequest(Long id, String manager) {
        BudgetRequest br = budgetRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BudgetRequest not found"));

        if (br.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed.");
        }

        br.setStatus(RequestStatus.APPROVED);
        br.setApprovedBy(manager);

        Department dept = br.getDepartment();
        dept.setCurrentBudget(dept.getCurrentBudget().subtract(br.getRequestedAmount()));
        departmentRepository.save(dept);

        BudgetRequest updated = budgetRequestRepository.save(br);
        auditLogService.logChange("APPROVE", br.getId(), "BudgetRequest", "PENDING", "APPROVED", manager);
        return updated;
    }

    @Transactional
    public BudgetRequest rejectRequest(Long id, String manager) {
        BudgetRequest br = budgetRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BudgetRequest not found"));

        if (br.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed.");
        }

        br.setStatus(RequestStatus.REJECTED);
        br.setApprovedBy(manager);
        BudgetRequest updated = budgetRequestRepository.save(br);
        auditLogService.logChange("REJECT", br.getId(), "BudgetRequest", "PENDING", "REJECTED", manager);
        return updated;
    }
}
