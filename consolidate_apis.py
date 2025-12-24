import json
import os

files = {
    "Gateway": "gateway-api.json",
    "Order": "order-api.json",
    "Restaurant": "restaurant-api.json",
    "Kitchen": "kitchen-api.json",
    "Delivery": "delivery-api.json",
    "Accounting": "accounting-api.json"
}

consolidated = {
    "openapi": "3.0.1",
    "info": {
        "title": "Restaurant Management System - Unified API",
        "version": "1.0.0",
        "description": "Consolidated API contract for all microservices via API Gateway"
    },
    "servers": [
        {
            "url": "http://localhost:8080",
            "description": "API Gateway"
        }
    ],
    "paths": {},
    "components": {
        "schemas": {},
        "securitySchemes": {
            "bearerAuth": {
                "type": "http",
                "scheme": "bearer",
                "bearerFormat": "JWT"
            }
        }
    },
    "security": [
        {
            "bearerAuth": []
        }
    ]
}

for service, filename in files.items():
    if os.path.exists(filename):
        with open(filename, 'r') as f:
            data = json.load(f)
            if 'paths' in data:
                consolidated['paths'].update(data['paths'])
            if 'components' in data and 'schemas' in data['components']:
                consolidated['components']['schemas'].update(data['components']['schemas'])

with open('restaurant-management-api-contract.json', 'w') as f:
    json.dump(consolidated, f, indent=2)

print("Unified API contract created: restaurant-management-api-contract.json")
