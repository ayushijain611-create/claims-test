package com.practice.claims.exception;

import com.practice.claims.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors =
                ex.getBindingResult().getFieldErrors().stream()
                        .map(
                                error ->
                                        buildError(
                                                error.getField(),
                                                error.getRejectedValue() != null
                                                        ? String.valueOf(error.getRejectedValue())
                                                        : "",
                                                error.getDefaultMessage()))
                        .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(PolicyInvalidException.class)
    public ResponseEntity<ErrorResponse> handlePolicyInvalid(PolicyInvalidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        buildError(
                                "policyNumber",
                                ex.getPolicyNumber(),
                                "Policy number is invalid. No such policy exists."));
    }

    @ExceptionHandler(ClaimExistException.class)
    public ResponseEntity<ErrorResponse> handleClaimExist(ClaimExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        buildError(
                                "claimId",
                                ex.getClaimId(),
                                "Claim already exist, please enter a valid claim ID and try again"));
    }

    @ExceptionHandler(ClaimNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClaimNotFound(ClaimNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        buildError(
                                "claimId",
                                ex.getClaimId(),
                                "Claim with :claimId does not exist. Please enter a valid claim id to view details."));
    }

    private ErrorResponse buildError(String field, String value, String error) {
        return new ErrorResponse(field, value, error);
    }
}
