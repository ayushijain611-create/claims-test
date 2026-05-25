package com.practice.claims.dto;

import com.practice.claims.entity.Claim.ClaimStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {

    private String claimId;
    private String referenceNumber;
    private LocalDateTime creationTime;
    private ClaimStatus status;
}
