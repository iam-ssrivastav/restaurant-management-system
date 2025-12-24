package com.restaurant.management.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cache.annotation.EnableCaching;

/**
 * <h2>RestaurantServiceApplication</h2>
 * <p>
 * Entry point for the Restaurant Service.
 * This service handles menu management and inventory.
 * </p>
 * 
 * @author Shivam Srivastav
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public io.micrometer.observation.ObservationRegistry observationRegistry() {
        return io.micrometer.observation.ObservationRegistry.create();
    }
}
