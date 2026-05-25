package com.practice.claims.exception;

import lombok.Getter;

@Getter
public class ClaimNotFoundException extends RuntimeException {

    private final String claimId;

    public ClaimNotFoundException(String claimId) {
        super("Claim not found: " + claimId);
        this.claimId = claimId;
    }
}
