package com.restaurant.management.delivery.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.management.delivery.model.DeliveryTask;
import com.restaurant.management.delivery.repository.DeliveryTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final DeliveryTaskRepository repository;
    private final ObjectMapper objectMapper;

    public OrderEventListener(DeliveryTaskRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "delivery-group")
    public void handleOrderCreated(String eventPayload) {
        log.info("Delivery: Received order-created event from Kafka: {}", eventPayload);

        try {
            JsonNode node = objectMapper.readTree(eventPayload);
            Long orderId = node.get("orderId").asLong();

            DeliveryTask task = new DeliveryTask(orderId, "PENDING", "External Delivery Address (Simulated)");
            repository.save(task);

            log.info("Delivery: Task created for Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Delivery: Failed to process event. Reason: {}", e.getMessage());
        }
    }
}
