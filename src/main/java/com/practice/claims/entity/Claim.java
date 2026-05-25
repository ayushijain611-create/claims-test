package com.practice.claims.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Claim ID is required")
    @Pattern(
            regexp = "^[A-Z]{3}[0-9]{3}$",
            message = "Claim ID must be 3 uppercase letters followed by 3 digits (e.g. ABC123)")
    private String claimId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_number", nullable = false)
    @NotNull
    private Policy policy;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @Column(unique = true)
    private String referenceNumber;

    private LocalDateTime createdTime;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    public enum ClaimStatus {
        UNDER_REVIEW,
        APPROVED,
        REJECTED
    }
}
