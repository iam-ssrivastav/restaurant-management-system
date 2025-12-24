package com.restaurant.management.kitchen.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.management.kitchen.model.KitchenOrder;
import com.restaurant.management.kitchen.repository.KitchenOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final KitchenOrderRepository repository;
    private final ObjectMapper objectMapper;

    public OrderEventListener(KitchenOrderRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "kitchen-group")
    public void handleOrderCreated(String eventPayload) {
        log.info("Kitchen: Received order-created event from Kafka: {}", eventPayload);

        try {
            JsonNode node = objectMapper.readTree(eventPayload);
            Long orderId = node.get("orderId").asLong();

            KitchenOrder ticket = new KitchenOrder(orderId, "RECEIVED", "New ticket generated from Order #" + orderId);
            repository.save(ticket);

            log.info("Kitchen: Ticket created for Order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Kitchen: Failed to process event. Reason: {}", e.getMessage());
        }
    }
}
