#!/usr/bin/env bash
set -euo pipefail

echo "Starting services (will build if needed)..."
docker compose up -d --build

echo "Waiting a moment for services to initialize..."
sleep 3

echo "Calling HelloService SayHello..."
grpcurl -plaintext -d '{"name":"Grpc Service"}' localhost:9090 com.example.grpc.HelloService/SayHello

echo "Creating an order via OrderService..."
grpcurl -plaintext -d '{"product":"DemoWidget","quantity":1}' localhost:9090 com.example.grpc.OrderService/CreateOrder

echo "Fetching trace summary from Jaeger..."
curl -s 'http://localhost:16686/api/traces?service=grpc-service' | jq '.data | length as $len | {traces: $len, sample_trace_id: (.[0].traceID // "none")}'

echo "Done. Open Jaeger UI at http://localhost:16686 to inspect traces."
