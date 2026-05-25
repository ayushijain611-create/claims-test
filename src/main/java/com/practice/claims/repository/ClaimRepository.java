package com.practice.claims.repository;

import com.practice.claims.entity.Claim;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<Claim> findByClaimId(String claimId);

    boolean existsByClaimId(String claimId);

    @Query("""
            SELECT c FROM Claim c
            WHERE c.policy.policyNumber = :num
            ORDER BY c.createdTime DESC
            """)
    List<Claim> claimsByPolicy(@Param("num") String policyNumber);

    @Query("""
            SELECT COUNT(c) FROM Claim c
            WHERE c.policy.policyNumber = :num
            AND c.createdTime >= :since
            """)
    long countRecent(@Param("num") String policyNumber, @Param("since") LocalDateTime since);
}
