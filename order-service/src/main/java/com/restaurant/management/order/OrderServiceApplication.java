package com.restaurant.management.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <h2>OrderServiceApplication</h2>
 * <p>
 * Entry point for the iSeatDr Order Engine.
 * </p>
 * 
 * @author Shivam Srivastav
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
