import org.example.RequestStatus;
import org.example.entity.BudgetRequest;
import org.example.entity.Department;
import org.example.repository.BudgetRequestRepository;
import org.example.repository.DepartmentRepository;
import org.example.service.AuditLogService;
import org.example.service.BudgetRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BudgetRequestServiceTests {

    private BudgetRequestService service;
    private BudgetRequestRepository budgetRequestRepository;
    private DepartmentRepository departmentRepository;
    private AuditLogService auditLogService;

    @BeforeEach
    void setup() {
        budgetRequestRepository = mock(BudgetRequestRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        auditLogService = mock(AuditLogService.class);

        service = new BudgetRequestService(budgetRequestRepository, departmentRepository, auditLogService);
    }

    @Test
    void testOverLimitRequestFails() {
        Department dept = new Department();
        dept.setYearlyAllocation(new BigDecimal("1000"));
        dept.setCurrentBudget(new BigDecimal("1000"));

        BudgetRequest req = new BudgetRequest();
        req.setRequestedAmount(new BigDecimal("200")); // Exceeds 10%
        req.setPurpose("New Laptops");
        req.setRequestedBy("john.doe");
        req.setDepartment(dept);
        req.setStatus(RequestStatus.PENDING);

        when(budgetRequestRepository.findDuplicates(anyString(), anyString(), any())).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> service.submitRequest(req));
        verifyNoInteractions(auditLogService);
    }
}
