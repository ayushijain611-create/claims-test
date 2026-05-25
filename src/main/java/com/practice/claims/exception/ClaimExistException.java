package com.practice.claims.exception;

import lombok.Getter;

@Getter
public class ClaimExistException extends RuntimeException {

    private final String claimId;

    public ClaimExistException(String claimId) {
        super("Claim already exists: " + claimId);
        this.claimId = claimId;
    }
}
