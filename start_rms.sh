#!/bin/bash

# Configuration
LOG_DIR="logs"
mkdir -p $LOG_DIR

echo "=================================================="
echo "   RESTAURANT MANAGEMENT SYSTEM (RMS) STARTUP"
echo "   Author: Shivam Srivastav"
echo "=================================================="

start_service() {
    SERVICE_NAME=$1
    JAR_PATH=$2
    PORT=$3
    
    echo "Starting $SERVICE_NAME on port $PORT..."
    nohup java -jar $JAR_PATH > $LOG_DIR/$SERVICE_NAME.log 2>&1 &
    PID=$!
    echo "$SERVICE_NAME started with PID $PID. Logs at $LOG_DIR/$SERVICE_NAME.log"
    echo $PID > $LOG_DIR/$SERVICE_NAME.pid
    
    # Simple check loop
    echo "Waiting for $SERVICE_NAME to initialize..."
    for i in {1..30}; do
        if grep -q "Started" $LOG_DIR/$SERVICE_NAME.log; then
            echo "✅ $SERVICE_NAME is UP!"
            return 0
        fi
        sleep 2
    done
    echo "⚠️  $SERVICE_NAME might be taking longer than expected. Check logs."
}

# 1. Start Eureka
start_service "eureka-server" "eureka-server/target/eureka-server-1.0.0.jar" 8761

# 2. Start Gateway
start_service "api-gateway" "api-gateway/target/api-gateway-1.0.0.jar" 8080

# 3. Start Order Service
start_service "order-service" "order-service/target/order-service-1.0.0.jar" 8081

# 4. Start Restaurant Service
start_service "restaurant-service" "restaurant-service/target/restaurant-service-1.0.0.jar" 8082

# 5. Start Notification Service
start_service "notification-service" "notification-service/target/notification-service-1.0.0.jar" 8083

echo "=================================================="
echo "   ALL SERVICES STARTED"
echo "=================================================="
echo "Use 'tail -f logs/*.log' to monitor."
