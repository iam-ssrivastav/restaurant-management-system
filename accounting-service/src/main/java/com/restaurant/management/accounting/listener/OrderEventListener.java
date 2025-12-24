package com.restaurant.management.accounting.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.management.accounting.model.Invoice;
import com.restaurant.management.accounting.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final InvoiceRepository repository;
    private final ObjectMapper objectMapper;

    public OrderEventListener(InvoiceRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "accounting-group")
    public void handleOrderCreated(String eventPayload) {
        log.info("Accounting: Received order-created event from Kafka: {}", eventPayload);

        try {
            JsonNode node = objectMapper.readTree(eventPayload);
            Long orderId = node.get("orderId").asLong();
            Double amount = node.get("amount").asDouble();

            Invoice invoice = new Invoice(orderId, amount, "UNPAID");
            repository.save(invoice);

            log.info("Accounting: Invoice generated for Order ID: {} with amount: {}", orderId, amount);
        } catch (Exception e) {
            log.error("Accounting: Failed to process event. Reason: {}", e.getMessage());
        }
    }
}
