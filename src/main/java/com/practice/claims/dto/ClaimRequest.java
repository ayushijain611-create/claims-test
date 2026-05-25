package com.practice.claims.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {

    @NotBlank(message = "Claim ID is required")
    @Pattern(
            regexp = "^[A-Z]{3}[0-9]{3}$",
            message = "Claim ID must be 3 uppercase letters followed by 3 digits (e.g. ABC123)")
    private String claimId;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    @Positive(message = "Amount must be greater than 0")
    private double amount;
}
