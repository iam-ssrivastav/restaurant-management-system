package com.restaurant.management.notification.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Shivam Srivastav
 *         Kafka listener for order events.
 */
@Service
public class OrderEventListener {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderEventListener.class);
    private final com.restaurant.management.notification.repository.NotificationLogRepository repository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public OrderEventListener(com.restaurant.management.notification.repository.NotificationLogRepository repository,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "notification-group")
    public void handleOrderCreated(String eventPayload) {
        processEvent(eventPayload, "Order Confirmation", "Order confirmation sent for Order #");
    }

    @KafkaListener(topics = "order-ready", groupId = "notification-group")
    public void handleOrderReady(String eventPayload) {
        processEvent(eventPayload, "Kitchen Alert", "Your order is ready and being assigned to a courier! Order #");
    }

    private void processEvent(String eventPayload, String type, String messagePrefix) {
        log.info("Audit Log: Received {} event from Kafka: {}", type, eventPayload);
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(eventPayload);
            Long orderId = node.get("orderId").asLong();

            log.info("Notification: Sending '{}' for Order ID: {}...", type, orderId);

            com.restaurant.management.notification.model.NotificationLog logEntry = new com.restaurant.management.notification.model.NotificationLog(
                    orderId, messagePrefix + orderId);
            repository.save(logEntry);

            log.info("Notification: {} message logged and sent successfully!", type);
        } catch (Exception e) {
            log.error("Notification: Failed to process {} event. Reason: {}", type, e.getMessage());
        }
    }
}
