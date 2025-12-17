#!/usr/bin/env bash
set -euo pipefail

echo "Starting services (will build if needed)..."
docker compose up -d --build

echo "Waiting a moment for services to initialize..."
sleep 1

# wait for REST readiness
echo "Waiting for REST service to be ready..."
for i in $(seq 1 30); do
  status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/orders/test || true)
  if [ "$status" = "200" ]; then
    echo "REST ready"
    break
  fi
  sleep 1
done

# helper to query jaeger for a demo id and service
query_jaeger() {
  local svc="$1"; local demo="$2"; local limit=${3:-50}
  # fetch recent traces for the service and filter for demo.id==demo
  curl -s "http://localhost:16686/api/traces?service=$svc&limit=$limit" \
    | jq -r --arg demo "$demo" '.data[]? | select(.spans[]?.tags[]? | (.key=="demo.id" and .value==$demo)) | .traceID' || true
}

DEMO_PREFIX=${1:-demo}

echo "Creating a base order via gRPC to obtain an orderId (used for get/update/delete tests)"
BASE_DEMO="$DEMO_PREFIX-base-$(date +%s)"
BASE_RESP=$(grpcurl -plaintext -H "x-demo-id: $BASE_DEMO" -d '{"product":"DemoWidget","quantity":1}' localhost:9090 com.example.grpc.OrderService/CreateOrder)
BASE_ID=$(echo "$BASE_RESP" | jq -r '.orderId')
echo "Created base order id: $BASE_ID"

declare -a calls

# Format: label|type|svc|method_or_service|payload_or_path
# type: grpc or rest
calls+=("hello-grpc|grpc|grpc-service|com.example.grpc.HelloService/SayHello|{\"name\":\"Grpc Service\"}")
calls+=("create-order-grpc|grpc|grpc-service|com.example.grpc.OrderService/CreateOrder|{\"product\":\"DemoWidget\",\"quantity\":1}")
calls+=("list-orders-grpc|grpc|grpc-service|com.example.grpc.OrderService/ListOrders|{}")
calls+=("get-order-grpc|grpc|grpc-service|com.example.grpc.OrderService/GetOrder|{\"id\":$BASE_ID}")
calls+=("update-order-grpc|grpc|grpc-service|com.example.grpc.OrderService/UpdateOrder|{\"orderId\":$BASE_ID,\"product\":\"Updated\",\"quantity\":2}")
calls+=("delete-order-grpc|grpc|grpc-service|com.example.grpc.OrderService/DeleteOrder|{\"id\":$BASE_ID}")

calls+=("test-rest|rest|rest-service|GET|/orders/test")
calls+=("create-order-rest|rest|rest-service|POST|/orders|{\"product\":\"DemoWidget\",\"quantity\":1}")
calls+=("list-orders-rest|rest|rest-service|GET|/orders")
calls+=("get-order-rest|rest|rest-service|GET|/orders/$BASE_ID")
calls+=("update-order-rest|rest|rest-service|PUT|/orders/$BASE_ID|{\"product\":\"Updated\",\"quantity\":2}")
calls+=("delete-order-rest|rest|rest-service|DELETE|/orders/$BASE_ID")

echo
echo "Executing calls to create one trace per API..."
# macOS bash doesn't support associative arrays; use a temp file to record results
RESULTS_FILE="$(mktemp /tmp/trace_results.XXXXXX)"
trap 'rm -f "$RESULTS_FILE"' EXIT

for entry in "${calls[@]}"; do
  # split into parts robustly so REST entries can include both path and payload
  IFS='|' read -ra parts <<<"$entry"
  label=${parts[0]}
  type=${parts[1]}
  svc=${parts[2]}
  method_or_service=${parts[3]}
  path=${parts[4]:-}
  payload=${parts[5]:-}
  DEMO_ID="$DEMO_PREFIX-$label-$(date +%s%3N)"
  echo "\n--- $label ($type) -> demo.id=$DEMO_ID ---"
  if [ "$type" = "grpc" ]; then
    # method_or_service holds the full gRPC method (e.g., com.example.grpc.HelloService/SayHello)
    resp=$(grpcurl -plaintext -H "x-demo-id: $DEMO_ID" -d "$payload" localhost:9090 "$method_or_service" 2>/dev/null || true)
    echo "Response: $resp"
    # query jaeger for the grpc-service traces with this demo id
    # give the agent a moment to export
    sleep 2
    tid=$(query_jaeger "$svc" "$DEMO_ID")
    echo "Jaeger trace id(s): $tid"
    echo "$label:$tid" >> "$RESULTS_FILE"
  else
    # REST: method_or_service is HTTP method, payload_or_path is path or optional payload
    http_method="$method_or_service"; path="$payload"
    if [ "$http_method" = "GET" ] || [ "$http_method" = "DELETE" ]; then
      resp=$(curl -s -H "X-Demo-Id: $DEMO_ID" -X "$http_method" "http://localhost:8080$path" || true)
    else
      resp=$(curl -s -H "X-Demo-Id: $DEMO_ID" -H "Content-Type: application/json" -X "$http_method" -d "$payload" "http://localhost:8080$path" || true)
    fi
    echo "Response: $resp"
    # give the agent a moment to export
    sleep 2
    tid=$(query_jaeger "$svc" "$DEMO_ID")
    echo "Jaeger trace id(s): $tid"
    echo "$label:$tid" >> "$RESULTS_FILE"
  fi
done

echo
echo "Summary (label -> trace id(s)):"
while IFS=: read -r k v; do
  echo "$k -> $v"
done < "$RESULTS_FILE"

echo "Done. You can now open Jaeger UI (http://localhost:16686) and search by tag: demo.id=<demo-prefix>-<label>-* to find traces per API for comparison."
