package com.practice.claims.service;

import com.practice.claims.dto.ClaimRequest;
import com.practice.claims.dto.ClaimResponse;
import com.practice.claims.entity.Claim;
import com.practice.claims.entity.Claim.ClaimStatus;
import com.practice.claims.entity.Policy;
import com.practice.claims.exception.ClaimExistException;
import com.practice.claims.exception.ClaimNotFoundException;
import com.practice.claims.exception.PolicyInvalidException;
import com.practice.claims.repository.ClaimRepository;
import com.practice.claims.repository.PolicyRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request) {
        ClaimStatus status = validateClaim(request);

        Claim claim = new Claim();
        claim.setClaimId(request.getClaimId());
        claim.setAmount(request.getAmount());
        claim.setPolicy(getPolicy(request.getPolicyNumber()));
        claim.setCreatedTime(LocalDateTime.now());
        claim.setReferenceNumber(UUID.randomUUID().toString());
        claim.setStatus(status);

        return toResponse(claimRepository.save(claim));
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaim(String claimId) {
        return claimRepository
                .findByClaimId(claimId)
                .map(this::toResponse)
                .orElseThrow(() -> new ClaimNotFoundException(claimId));
    }

    private ClaimStatus validateClaim(ClaimRequest request) {
        ensurePolicyExists(request.getPolicyNumber());

        if (claimRepository.existsByClaimId(request.getClaimId())) {
            throw new ClaimExistException(request.getClaimId());
        }

        Policy policy = getPolicy(request.getPolicyNumber());
        LocalDateTime now = LocalDateTime.now();

        if (isAmountRejected(request, policy)) {
            return ClaimStatus.REJECTED;
        }
        if (isFraudulent(request, policy, now)) {
            return ClaimStatus.UNDER_REVIEW;
        }
        return ClaimStatus.APPROVED;
    }

    private void ensurePolicyExists(String policyNumber) {
        if (!policyRepository.existsByPolicyNumber(policyNumber)) {
            throw new PolicyInvalidException(policyNumber);
        }
    }

    private Policy getPolicy(String policyNumber) {
        return policyRepository
                .findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new PolicyInvalidException(policyNumber));
    }

    private boolean isAmountRejected(ClaimRequest request, Policy policy) {
        return request.getAmount() >= policy.getPolicyAmount();
    }

    private boolean isFraudulent(ClaimRequest request, Policy policy, LocalDateTime now) {
        return countRecentClaims(request.getPolicyNumber(), now) >= 2
                || isOddHour(now)
                || now.isBefore(policy.getPolicyStart().plusWeeks(1));
    }

    private long countRecentClaims(String policyNumber, LocalDateTime now) {
        return claimRepository.countRecent(policyNumber, now.minusMonths(6));
    }

    private boolean isOddHour(LocalDateTime now) {
        int hour = now.getHour();
        return hour >= 2 && hour < 5;
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimId(),
                claim.getReferenceNumber(),
                claim.getCreatedTime(),
                claim.getStatus());
    }
}
