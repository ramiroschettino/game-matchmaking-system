# Kubernetes Deployment Guide

## Prerequisites

- Kubernetes cluster (Minikube, Docker Desktop, or Cloud)
- kubectl installed
- Ingress controller (NGINX)

## Deploy to Kubernetes

### 1. Apply all manifests in order:
```bash
kubectl apply -f 00-namespace.yaml
kubectl apply -f 01-configmap-player.yaml
kubectl apply -f 02-configmap-match.yaml
kubectl apply -f 03-secrets.yaml
kubectl apply -f 04-mongodb-statefulset.yaml
kubectl apply -f 05-rabbitmq-statefulset.yaml

# Wait for MongoDB and RabbitMQ to be ready
kubectl wait --for=condition=ready pod -l app=mongodb -n game-matchmaking --timeout=300s
kubectl wait --for=condition=ready pod -l app=rabbitmq -n game-matchmaking --timeout=300s

kubectl apply -f 06-player-service-deployment.yaml
kubectl apply -f 07-match-service-deployment.yaml
kubectl apply -f 08-hpa.yaml
kubectl apply -f 09-ingress.yaml
```

### 2. Or apply all at once:
```bash
kubectl apply -f .
```

### 3. Verify deployment:
```bash
# Check all resources
kubectl get all -n game-matchmaking

# Check pods status
kubectl get pods -n game-matchmaking

# Check services
kubectl get svc -n game-matchmaking

# Check HPA
kubectl get hpa -n game-matchmaking

# Check ingress
kubectl get ingress -n game-matchmaking
```

### 4. Access the services:

Add to `/etc/hosts` (Linux/Mac) or `C:\Windows\System32\drivers\etc\hosts` (Windows):
```
127.0.0.1 game-matchmaking.local
```

Then access:
- Player Service: http://game-matchmaking.local/api/v1/players
- Match Service: http://game-matchmaking.local/api/v1/matches

### 5. Monitor logs:
```bash
# Player Service logs
kubectl logs -f deployment/player-service -n game-matchmaking

# Match Service logs
kubectl logs -f deployment/match-service -n game-matchmaking

# RabbitMQ logs
kubectl logs -f statefulset/rabbitmq -n game-matchmaking
```

### 6. Scale manually (optional):
```bash
# Scale player service to 5 replicas
kubectl scale deployment player-service --replicas=5 -n game-matchmaking

# Scale match service to 5 replicas
kubectl scale deployment match-service --replicas=5 -n game-matchmaking
```

### 7. Delete everything:
```bash
kubectl delete namespace game-matchmaking
```

## Auto-scaling

HPA will automatically scale pods based on:
- **CPU usage > 70%** → Scale up
- **Memory usage > 80%** → Scale up
- **Min replicas:** 2
- **Max replicas:** 10

## Resources per pod:

**Player Service:**
- Requests: 512Mi RAM, 500m CPU
- Limits: 1Gi RAM, 1000m CPU

**Match Service:**
- Requests: 512Mi RAM, 500m CPU
- Limits: 1Gi RAM, 1000m CPU

**MongoDB:**
- Requests: 256Mi RAM, 250m CPU
- Limits: 512Mi RAM, 500m CPU
- Storage: 10Gi

**RabbitMQ:**
- Requests: 256Mi RAM, 250m CPU
- Limits: 512Mi RAM, 500m CPU
- Storage: 5Gi
```

---

## 📊 **Estructura final de k8s/:**
```
k8s/
├── 00-namespace.yaml
├── 01-configmap-player.yaml
├── 02-configmap-match.yaml
├── 03-secrets.yaml
├── 04-mongodb-statefulset.yaml
├── 05-rabbitmq-statefulset.yaml
├── 06-player-service-deployment.yaml
├── 07-match-service-deployment.yaml
├── 08-hpa.yaml
├── 09-ingress.yaml
└── README.md