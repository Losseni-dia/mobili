package com.mobili.backend.module.payment.fedaPay.controller;

import com.mobili.backend.module.booking.booking.service.BookingService;
import com.mobili.backend.module.payment.fedaPay.service.FedaPayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    @Value("${FEDAPAY_WEBHOOK_SECRET}")
    private String webhookSecret;

    private final BookingService bookingService;
    private final FedaPayService fedaPayService;


    @PostMapping("/checkout/{bookingId}")
    public ResponseEntity<Map<String, String>> createCheckout(@PathVariable("bookingId") Long bookingId) {
        log.info("🚀 Requête de paiement reçue pour le Booking ID: {}", bookingId);

        var booking = bookingService.findById(bookingId);

        String url = fedaPayService.createPaymentUrl(
                booking.getTotalPrice(),
                booking.getCustomer().getEmail(),
                bookingId);

        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestParam("secret") String secret) {

        if (!webhookSecret.equals(secret)) {
            log.error("❌ Secret Webhook incorrect !");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Map<String, Object> entity = (Map<String, Object>) payload.get("entity");
            if (entity == null)
                return ResponseEntity.ok().build();

            String status = (String) entity.get("status");

            if ("approved".equals(status)) {
                Map<String, Object> metadata = (Map<String, Object>) entity.get("custom_metadata");
                if (metadata != null && metadata.containsKey("booking_id")) {

                    // Extraction sécurisée de l'ID
                    Long bookingId = Long.valueOf(metadata.get("booking_id").toString());

                    // ✅ APPEL AU SERVICE POUR VALIDER ET GÉNÉRER LES TICKETS
                    bookingService.confirmFedaPayPayment(bookingId);
                }
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("💥 Erreur Webhook: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}