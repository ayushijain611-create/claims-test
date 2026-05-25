package com.practice.claims;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.claims.dto.ClaimRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClaimsApplicationTests {

    private static final String BASE = "/v1/claims";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void createClaim_validFirstClaim() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("ABC123", "A877656543", 500))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").value("ABC123"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.referenceNumber").exists())
                .andExpect(jsonPath("$.creationTime").exists());
    }

    @Test
    @Order(2)
    void createClaim_duplicateClaimId_returns409() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("ABC123", "A877656543", 500))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.field").value("claimId"))
                .andExpect(jsonPath("$.value").value("ABC123"))
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Claim already exist, please enter a valid claim ID and try again"));
    }

    @Test
    @Order(3)
    void createClaim_amountEqualToPolicyAmount_returnsRejected() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("DEF123", "A877656543", 1000))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").value("DEF123"))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @Order(4)
    void createClaim_invalidPolicyNumber_returns400() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("GHI123", "B776655433", 500))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("policyNumber"))
                .andExpect(jsonPath("$.value").value("B776655433"))
                .andExpect(jsonPath("$.error").value("Policy number is invalid. No such policy exists."));
    }

    @Test
    @Order(5)
    void createClaim_validOnSecondPolicy_returnsApproved() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("GHI123", "B7766554333", 1000))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").value("GHI123"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @Order(6)
    void createClaim_secondClaimOnSamePolicy_returnsApproved() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("JKL123", "B7766554333", 500))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").value("JKL123"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @Order(7)
    void createClaim_fraudCheck_returnsUnderReview() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new ClaimRequest("LMN123", "B7766554333", 200))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.claimId").value("LMN123"))
                .andExpect(jsonPath("$.status").value("UNDER_REVIEW"));
    }

    @Test
    @Order(8)
    void createClaim_missingPolicyNumber_returns400() throws Exception {
        mockMvc.perform(
                        post(BASE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"claimId\":\"LMN123\",\"amount\":200}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].field").value("policyNumber"))
                .andExpect(jsonPath("$[0].error").value("Policy number is required"));
    }

    @Test
    @Order(9)
    void fetchClaim_existingClaim_returns200() throws Exception {
        mockMvc.perform(get(BASE + "/JKL123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.claimId").value("JKL123"))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.referenceNumber").exists())
                .andExpect(jsonPath("$.creationTime").exists());
    }

    @Test
    @Order(10)
    void fetchClaim_unknownClaimId_returns404() throws Exception {
        mockMvc.perform(get(BASE + "/NOP123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("claimId"))
                .andExpect(jsonPath("$.value").value("NOP123"))
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Claim with :claimId does not exist. Please enter a valid claim id to view details."));
    }

    @Test
    @Order(11)
    void fetchClaim_withoutClaimId_returns405() throws Exception {
        mockMvc.perform(get(BASE)).andExpect(status().isMethodNotAllowed());
    }
}
