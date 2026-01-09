# REST & gRPC Microservices with Jaeger Tracing using Kubernetes

This section demonstrates deploying REST and gRPC microservices on Kubernetes with distributed tracing using Jaeger. It covers installation, configuration, and validation steps, focusing on NodePort for service exposure.

---

## Prerequisites

- [Minikube](https://minikube.sigs.k8s.io/docs/start/) (for local Kubernetes)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl/) (Kubernetes CLI)
- [Docker](https://docs.docker.com/get-docker/) (for building images)

---

## What is Minikube?

Minikube is a tool that makes it easy to run Kubernetes locally. It creates a single-node Kubernetes cluster inside a VM or container, allowing you to develop and test Kubernetes applications without a full cluster.

---

## What is kubectl?

kubectl is the command-line tool for interacting with Kubernetes clusters. You use it to deploy applications, inspect and manage cluster resources, and troubleshoot issues.

---

## 1. Start Minikube

minikube start


---

## 2. Install PostgreSQL, Jaeger, and Services

### PostgreSQL Deployment

kubectl apply -f postgres-deploy.yaml

### Jaegar Deployment

kubectl apply -f jaeger-deploy.yaml

### REST & gRPC Services

kubectl apply -f rest-service-deploy.yaml
kubectl apply -f grpc-service-deploy.yaml


---

## 3. Understanding NodePort

### NodePort

NodePort exposes your service on a static port on each node’s IP. You can access the service from outside the cluster using `<NodeIP>:<NodePort>`. This is useful for development and testing.

### Example

- REST service: `http://<NodeIP>:31080`
- Jaeger UI: `http://<NodeIP>:31686`

---

## 4. Accessing Services with Minikube

Since Minikube does not provide external IPs by default, you need to use the `minikube service` command to get the external address of your services. For example:

minikube service rest-service --url
minikube service jaeger --url


This command will display the URL you can use to access your services from outside the cluster. Use these URLs for testing and validation.

---

## 5. OpenTelemetry Configuration

### Agent Setup

- The OpenTelemetry Java agent is copied into each service image.
- The agent is loaded via `JAVA_TOOL_OPTIONS` environment variable.

### Exporter Configuration

- Traces are exported to Jaeger using OTLP gRPC protocol.
- Ensure the following environment variables are set in each service:

env:

name: JAVA_TOOL_OPTIONS
value: "-javaagent:/opentelemetry/opentelemetry-javaagent.jar"

name: OTEL_TRACES_EXPORTER
value: "otlp"

name: OTEL_EXPORTER_OTLP_ENDPOINT
value: "http://jaeger:4317"

name: OTEL_EXPORTER_OTLP_PROTOCOL
value: "grpc"

name: OTEL_RESOURCE_ATTRIBUTES
value: "service.name=rest-service,service.instance.id=$(POD_NAME),k8s.pod.ip=$(POD_IP),k8s.node.name=$(NODE_NAME)"

name: OTEL_TRACES_SAMPLER
value: "always_on"


- Use the Downward API to inject pod metadata into environment variables.

---

## 6. Validation Commands

### Check Pods

kubectl get pod

### Check Services

kubectl get service

### Check ReplicaSets

kubectl get replicaset

### Check Deployments

kubectl get deployment

### Check Logs

kubectl logs deployment/rest-service
kubectl logs deployment/jaeger

## 7. Testing

### Generate Traffic

**POST**

curl -X POST (minikube rest-service url)/orders \
  -H "Content-Type: application/json" \
  -d '{
        "product": "Widget",
        "quantity": 5
      }

Similarly create more orders

**READ ALL (GET)**

curl (minikube rest-service url)/orders

**READ BY ID (GET)**

curl (minikube rest-service url)/orders/{id}

**UPDATE (PUT)**

curl -X PUT (minikube rest-service url)/orders/{id} \
  -H "Content-Type: application/json" \
  -d '{
        "product": "WidgetPro",
        "quantity": 10
      }'

### View Traces

- minikube service jaeger --url
- Open Jaeger UI as per the minkube service url
- Select service: `rest-service`
- Click "Find Traces"

---

## 7. Load Balancing Demo

Demonstrate Kubernetes Service load balancing across multiple REST service pods.

### Prerequisites

Scale your REST deployment to multiple replicas:

### 1. Generate Service URL (Run Once)

minikube service rest-service --url | head -n1 > rest-service-url.txt

On a new terminal -> cat rest-service-url.txt

**Output example:**
http://192.168.49.2:31080

### 3. Run the load_balancing.bash file

./load_balancing.bash

### Expected Output

Using URL: http://192.168.49.2:31080
Sending 20 requests...
Request 1: rest-service-7ff9c76544-vrmnw
Request 2: rest-service-7ff9c76544-abcdx
Request 3: rest-service-7ff9c76544-xyz12

==== Load Balancing Summary ====
rest-service-7ff9c76544-vrmnw: 17 requests (34.0%)
rest-service-7ff9c76544-abcdx: 17 requests (34.0%)
rest-service-7ff9c76544-xyz12: 16 requests (32.0%)
Kubernetes Service load balancing demonstrated!

## 8. Kubernetes Self-Healing Demo

Demonstrate Kubernetes **self-healing** - when a pod dies, Deployment automatically recreates it.

### 1. Check Current Pods

kubectl get pods -l app=rest-service -o wide

### 2. Execute the bash file and then open a new terminal to Kill a Pod

./load_balancing.bash

kubectl delete pod {podname} --force --grace-period=0

### 3. Watch Self-Healing in Action

Watch pods being recreated
kubectl get pods -l app=rest-service -w

Look at the bash script logs, you will see traffic being directed to a new pod now.

## 9. Cleanup

If you need to use the services again in future just do 

minikube stop

On starting minikube again, the services would be restarted automatically without any explicit deployment needed.

If you want to remove the services

kubectl delete -f rest-service-deploy.yaml
kubectl delete -f grpc-service-deploy.yaml
kubectl delete -f jaeger-deploy.yaml
kubectl delete -f postgres-deploy.yaml


---

## References

- [Minikube Documentation](https://minikube.sigs.k8s.io/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/home/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation)





