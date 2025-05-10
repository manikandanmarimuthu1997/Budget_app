package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.RequestStatus;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "budget_request")
@Data
public class BudgetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "requested_amount")
    private BigDecimal requestedAmount;

    @Column(name = "purpose")
    private String purpose;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "requested_by")
    private String requestedBy;

    @Column(name = "approved_by")
    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;

    @ManyToOne(cascade = CascadeType.PERSIST)  // Cascade persist operations
    @JoinColumn(name = "department_id")
    private Department department;
}
