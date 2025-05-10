package org.example.repository;

import org.example.RequestStatus;
import org.example.entity.BudgetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BudgetRequestRepository extends JpaRepository<BudgetRequest, Long> {

    @Query("SELECT br FROM BudgetRequest br WHERE br.purpose = :purpose AND br.requestedBy = :requestedBy AND br.dateCreated >= :fromDate")
    List<BudgetRequest> findDuplicates(String purpose, String requestedBy, LocalDateTime fromDate);

    List<BudgetRequest> findAllByStatus(RequestStatus status);
}
