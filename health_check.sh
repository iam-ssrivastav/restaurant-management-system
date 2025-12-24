#!/bin/bash

echo "⏳ Waiting for services to initialize... (This may take 60s)"
sleep 10

# Loops to check if logs contain "Started Application"
check_service_up() {
    SERVICE=$1
    LOG_FILE="logs/$SERVICE.log"
    for i in {1..20}; do
        if grep -q "Started" "$LOG_FILE"; then
            echo "✅ $SERVICE is UP"
            return 0
        fi
        sleep 3
    done
    echo "❌ $SERVICE failed to start. Last 5 lines of logs:"
    tail -n 5 "$LOG_FILE"
    return 1
}

# Check all services
check_service_up "eureka-server"
check_service_up "api-gateway"
check_service_up "order-service"
check_service_up "restaurant-service"
check_service_up "notification-service"

echo "---------------------------------------------------"
echo "🔍 Checking Eureka Registry..."
if grep -q "Registered instance" "logs/eureka-server.log"; then
    echo "✅ Instances are registering with Eureka"
else
    echo "⚠️  No instances registered yet. Waiting 10s more..."
    sleep 10
fi

echo "---------------------------------------------------"
echo "🚀 READY TO TEST!"
