package com.studentmanagement.fees.service;

import com.studentmanagement.fees.dto.*;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentInitiationResponse initiatePayment(InitiatePaymentRequest request);

    void handleWebhook(String payload, String signature);

    PaymentResponse getPayment(UUID id);

    String downloadReceipt(UUID paymentId);

    FeeSummaryResponse getFeeSummary();

    List<OutstandingBalanceResponse> getOutstandingBalances();
}
