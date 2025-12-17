# grpc-service — Quick gRPC commands

This README documents how to start the gRPC service and run basic commands (using `grpcurl`) to exercise the Hello and Order services.

## Prerequisites ✅

- Docker & Docker Compose
- Java + Maven (only required if you want to build locally)
- grpcurl (install via Homebrew: `brew install grpcurl`)

## Start the stack

From the repository root:

```bash
# build & start (uses docker-compose.yml in repo root)
docker compose up -d --build

# confirm services
docker compose ps
```

gRPC server listens on port `9090` by default.

## Auto-instrumentation with Jaeger (OpenTelemetry Java agent)

Follow these steps to quickly enable tracing and view traces in Jaeger:

1. Download the OpenTelemetry Java agent into the repository root:

```bash
curl -sSL -o opentelemetry-javaagent.jar \
	https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

2. Bring up the stack (the Compose file is already configured to mount the agent and start Jaeger):

```bash
docker compose up -d --build
```

3. Open Jaeger UI at: http://localhost:16686 and search for `grpc-service` or `rest-service` to view traces.

Notes:
- The Docker Compose file mounts `./opentelemetry-javaagent.jar` into both services and sets OTEL environment variables. Make sure the file exists at the repo root before `docker compose up`.
- For demo purposes the sampler is set to `always_on` so traces will always be exported to Jaeger.

## Inspect available services

```bash
# list services
grpcurl -plaintext localhost:9090 list

# describe OrderService
grpcurl -plaintext localhost:9090 describe com.example.grpc.OrderService
```

## HelloService (basic check)

```bash
grpcurl -plaintext -d '{"name":"Grpc Service"}' localhost:9090 com.example.grpc.HelloService/SayHello
# Expected response:
# { "message": "Hello, Grpc Service" }
```

## OrderService — CRUD examples

Create order:

```bash
grpcurl -plaintext -d '{"product":"Widget","quantity":5}' localhost:9090 com.example.grpc.OrderService/CreateOrder
```

List orders:

```bash
grpcurl -plaintext -d '{}' localhost:9090 com.example.grpc.OrderService/ListOrders
```

Get order (example id 5):

```bash
grpcurl -plaintext -d '{"id":5}' localhost:9090 com.example.grpc.OrderService/GetOrder
```

Update order:

```bash
grpcurl -plaintext -d '{"orderId":5,"product":"WidgetPrime","quantity":10}' localhost:9090 com.example.grpc.OrderService/UpdateOrder
```

Delete order:

```bash
grpcurl -plaintext -d '{"id":5}' localhost:9090 com.example.grpc.OrderService/DeleteOrder
```

Notes:

- Use `-plaintext` when the server is unsecured (default in local compose setup).
- The server's proto file is `src/main/proto/index.proto`.
- If you don't have `grpcurl` on your host, you can run it inside a container:

```bash
docker run --rm --network host fullstorydev/grpcurl -plaintext localhost:9090 list
```

## Troubleshooting

- If `grpcurl` reports connection refused, ensure the gRPC container is running and port `9090` is exposed in `docker compose ps`.
- If DB-dependent operations fail during tests locally, either start the `postgres` service from compose or run tests that use an in-memory DB.

---

If you'd like, I can add automated tests that call these endpoints (unit or integration). 🔧
