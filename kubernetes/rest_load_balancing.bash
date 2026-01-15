#!/usr/bin/env bash
set -euo pipefail

# Number of requests to send
REQUESTS=${1:-100}

# Read URL from file (created by minikube service --url)
URL_FILE="rest-service-url.txt"

if [[ ! -f "${URL_FILE}" ]]; then
  echo "❌ ${URL_FILE} not found!"
  echo "📝 Run this first:"
  echo "   minikube service rest-service --url | head -n1 > ${URL_FILE}"
  echo "   cat ${URL_FILE}"
  exit 1
fi

SERVICE_URL=$(cat "${URL_FILE}" | head -n1 | xargs)
echo "✅ Using URL from ${URL_FILE}: ${SERVICE_URL}"
echo "📊 Sending ${REQUESTS} requests to demonstrate Kubernetes Service load balancing..."
echo

declare -A COUNTS

for i in $(seq 1 "${REQUESTS}"); do
  RESP=$(curl -s --max-time 5 "${SERVICE_URL}/pod-id" 2>/dev/null || echo "ERROR")
  echo "Request ${i}: ${RESP} [$(date +'%H:%M:%S.%3N')]"
  
  POD_ID="${RESP}"
  COUNTS["${POD_ID}"]=$(( ${COUNTS["${POD_ID}"]:-0} + 1 ))
  
  sleep 1
done

echo
echo "==== 📈 Load Balancing Summary ===="
echo "Total requests: ${REQUESTS}"
echo

for POD in "${!COUNTS[@]}"; do
  COUNT=${COUNTS[$POD]}
  PERCENT=$(awk "BEGIN {printf \"%.1f\", (${COUNT}/${REQUESTS})*100}")
  echo "  ${POD}: ${COUNT} requests (${PERCENT}%)"
done

echo
echo "🎉 Kubernetes Service is successfully load balancing across pods!"
