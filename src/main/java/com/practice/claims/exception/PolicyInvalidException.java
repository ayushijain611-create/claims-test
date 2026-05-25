package com.practice.claims.exception;

import lombok.Getter;

@Getter
public class PolicyInvalidException extends RuntimeException {

    private final String policyNumber;

    public PolicyInvalidException(String policyNumber) {
        super("Policy number is invalid: " + policyNumber);
        this.policyNumber = policyNumber;
    }
}
