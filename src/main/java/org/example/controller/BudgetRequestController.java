package org.example.controller;

import org.example.RequestStatus;
import org.example.entity.BudgetRequest;
import org.example.repository.BudgetRequestRepository;
import org.example.service.BudgetRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budget-request")
public class BudgetRequestController {

    private final BudgetRequestService budgetRequestService;
    private final BudgetRequestRepository budgetRequestRepository;

    public BudgetRequestController(BudgetRequestService budgetRequestService,
                                   BudgetRequestRepository budgetRequestRepository) {
        this.budgetRequestService = budgetRequestService;
        this.budgetRequestRepository = budgetRequestRepository;
    }

    @PostMapping
    public ResponseEntity<BudgetRequest> submit(@RequestBody BudgetRequest request) {
        BudgetRequest saved = budgetRequestService.submitRequest(request);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<BudgetRequest> approve(@PathVariable Long id,
                                                 @RequestHeader("X-User") String manager) {
        BudgetRequest approved = budgetRequestService.approveRequest(id, manager);
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<BudgetRequest> reject(@PathVariable Long id,
                                                @RequestHeader("X-User") String manager) {
        BudgetRequest rejected = budgetRequestService.rejectRequest(id, manager);
        return ResponseEntity.ok(rejected);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BudgetRequest>> getPendingRequests() {
        List<BudgetRequest> pending = budgetRequestRepository.findAllByStatus(RequestStatus.PENDING);
        return ResponseEntity.ok(pending);
    }
}
