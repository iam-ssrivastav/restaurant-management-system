package com.restaurant.management.order.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Shivam Srivastav
 *         Poller that picks up unprocessed Outbox events and sends them to
 *         Kafka.
 */
@Service
public class OutboxPoller {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPoller(OutboxRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void pollEvents() {
        List<OutboxEvent> events = repository.findByProcessedFalse();
        if (events.isEmpty())
            return;

        log.info("Outbox: Found {} events to process", events.size());

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getEventType(), event.getAggregateId(), event.getPayload());
                event.setProcessed(true);
                repository.save(event);
                log.info("Outbox: Successfully sent event {} to Kafka topic {}", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Outbox: Failed to send event {} to Kafka. Reason: {}", event.getId(), e.getMessage());
            }
        }
    }
}
