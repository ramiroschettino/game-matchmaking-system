# 🎮 Game Matchmaking System

Sistema de matchmaking en tiempo real para videojuegos multijugador. Dos microservicios que se comunican mediante eventos para crear partidas balanceadas automáticamente.

## 🎯 ¿Qué hace?

1. **10 jugadores se unen a la cola** → Player Service publica eventos
2. **Match Service crea partida automáticamente** cuando hay 10 jugadores
3. **Player Service actualiza jugadores** a "IN_MATCH"
4. **Partida finaliza** → Match Service publica resultado
5. **Player Service actualiza ratings** (ganadores +25, perdedores -20)

Todo automático, sin polling, usando **eventos asíncronos**.

---

## 🏗️ Arquitectura
```
┌────────────────────┐         RabbitMQ          ┌────────────────────┐
│  Player Service    │◄──────────────────────────►│  Match Service     │
│  (Puerto 8080)     │   Event-Driven Arch       │  (Puerto 8081)     │
│                    │                            │                    │
│  • Gestión players │                            │  • Matchmaking     │
│  • Ratings (ELO)   │                            │  • Crear partidas  │
│  • CQRS            │                            │  • Event Sourcing  │
└─────────┬──────────┘                            └──────────┬─────────┘
          │                                                  │
          ▼                                                  ▼
   MongoDB (playerdb)                              MongoDB (matchdb)
   • players (write)                               • match_events (events)
   • player_stats (read)                           • matches (projections)
```

---

## 🛠️ Stack Tecnológico

| Categoría | Tecnología | Propósito |
|-----------|------------|-----------|
| **Backend** | Java 17 + Spring Boot 3.5.7 | Framework principal |
| **Persistencia** | MongoDB 7 | Base de datos NoSQL |
| **Mensajería** | RabbitMQ 3.13 | Comunicación asíncrona entre servicios |
| **Build** | Gradle | Gestión de dependencias |
| **Containers** | Docker + Docker Compose | Desarrollo local |
| **Orquestación** | Kubernetes | Producción (escalado automático) |

---

## 📐 Patrones Arquitectónicos

### **1. Hexagonal Architecture (Ambos servicios)**
- **Domain**: Lógica de negocio pura (no depende de frameworks)
- **Ports**: Interfaces (contratos)
- **Adapters**: Implementaciones (MongoDB, RabbitMQ, REST)

### **2. CQRS (Player Service)**
- **Write Model** (`players`): Comandos (crear, actualizar)
- **Read Model** (`player_stats`): Consultas optimizadas (stats, rankings)

### **3. Event Sourcing (Match Service)**
- Guarda **todos los eventos** de una partida
- Reconstruye el estado reproduciendo eventos
- Auditoría completa: `MatchCreated` → `MatchStarted` → `MatchCompleted`

### **4. Event-Driven Architecture**
Servicios desacoplados que se comunican mediante eventos:
```
Player Service publica:
  • PlayerJoinedQueueEvent → Match Service lo escucha

Match Service publica:
  • MatchFoundEvent → Player Service lo escucha
  • MatchCompletedEvent → Player Service lo escucha
```

---

## 🔄 Flujo de Comunicación (Paso a Paso)
```
1. POST /players/{id}/queue/join
   ↓
2. Player Service: status = IN_QUEUE
   ↓
3. RabbitMQ: PlayerJoinedQueueEvent
   ↓
4. Match Service: Agregar a cola (1/10, 2/10... 10/10)
   ↓
5. Al llegar a 10: Crear partida balanceada (5v5)
   ↓
6. MongoDB: Guardar MatchCreatedEvent (Event Sourcing)
   ↓
7. RabbitMQ: MatchFoundEvent
   ↓
8. Player Service: Actualizar 10 jugadores a IN_MATCH
   ↓
9. POST /matches/{id}/complete (winnerTeam: TEAM_A)
   ↓
10. Match Service: Guardar MatchCompletedEvent
   ↓
11. RabbitMQ: MatchCompletedEvent
   ↓
12. Player Service:
    • Team A: rating +25
    • Team B: rating -20
    • Todos vuelven a IDLE
```

---

## 🐰 RabbitMQ - Configuración

**Exchange:** `game.events` (topic)

**Queues:**
- `matchmaking.queue` → Match Service escucha
- `match.found.queue` → Player Service escucha
- `match.completed.queue` → Player Service escucha

**Routing Keys:**
- `player.queue.joined` → Jugador se une
- `player.queue.left` → Jugador sale
- `match.found` → Partida creada
- `match.completed` → Partida finalizada

---

## 🚀 Cómo Ejecutar

### **1. Requisitos**
- Java 17+
- Docker + Docker Compose
- Git

### **2. Clonar**
```bash
git clone https://github.com/ramiroschettino/game-matchmaking-system.git
cd game-matchmaking-system
```

### **3. Levantar Infraestructura**
```bash
docker-compose up -d
```

Servicios disponibles:
- MongoDB: `localhost:27017`
- RabbitMQ: `localhost:5672`
- RabbitMQ UI: http://localhost:15672 (admin/admin123)

### **4. Levantar Servicios**

**Terminal 1:**
```bash
cd player-service
./gradlew bootRun
```

**Terminal 2:**
```bash
cd match-service
./gradlew bootRun
```

### **5. Verificar**
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

---

## 📡 API Endpoints

### **Player Service (8080)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/players` | Crear jugador |
| GET | `/api/v1/players/{id}` | Ver jugador |
| GET | `/api/v1/players/{id}/stats` | Ver estadísticas |
| GET | `/api/v1/players/leaderboard` | Top jugadores |
| POST | `/api/v1/players/{id}/queue/join` | Unirse a cola |
| POST | `/api/v1/players/{id}/queue/leave` | Salir de cola |

### **Match Service (8081)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/matches/{id}` | Ver partida |
| GET | `/api/v1/matches/recent` | Partidas recientes |
| GET | `/api/v1/matches/queue/size` | Jugadores en cola |
| POST | `/api/v1/matches/{id}/complete` | Finalizar partida |

---

## 🧪 Prueba Rápida

**1. Crear 10 jugadores:**
```bash
POST http://localhost:8080/api/v1/players
{
  "username": "Player1",
  "email": "player1@example.com"
}
```
Repetir para Player2... Player10

**2. Unir a la cola (uno por uno):**
```bash
POST http://localhost:8080/api/v1/players/{ID}/queue/join
```

**3. Al 10mo jugador → Se crea partida automáticamente!**

**4. Ver partida creada:**
```bash
GET http://localhost:8081/api/v1/matches/recent
```

**5. Completar partida:**
```bash
POST http://localhost:8081/api/v1/matches/{MATCH_ID}/complete
{
  "winnerTeam": "TEAM_A",
  "durationSeconds": 1800
}
```

**6. Ver ratings actualizados:**
```bash
GET http://localhost:8080/api/v1/players/leaderboard
```

---

## ☸️ Kubernetes - Deployment

### **Arquitectura en K8s**
```
┌─────────────────────────────────────────────────┐
│            Ingress Controller                   │
│  /api/v1/players → Player Service               │
│  /api/v1/matches → Match Service                │
└─────────────────────────────────────────────────┘
                      ↓
        ┌─────────────────────────────┐
        │    Player Service           │
        │    Deployment (2 replicas)  │
        │    + HPA (2-10 pods)        │
        └─────────────────────────────┘
                      ↓
        ┌─────────────────────────────┐
        │    Match Service            │
        │    Deployment (2 replicas)  │
        │    + HPA (2-10 pods)        │
        └─────────────────────────────┘
                      ↓
    ┌────────────┐         ┌────────────┐
    │  MongoDB   │         │  RabbitMQ  │
    │ StatefulSet│         │ StatefulSet│
    │ PVC: 10Gi  │         │ PVC: 5Gi   │
    └────────────┘         └────────────┘
```

### **Componentes Clave**

**Deployments:**
- `player-service`: 2 réplicas base
- `match-service`: 2 réplicas base

**HorizontalPodAutoscaler (HPA):**
- **Min replicas**: 2
- **Max replicas**: 10
- **Target CPU**: 70%
- **Target Memory**: 80%

**Cuando tráfico aumenta:**
```
Usuarios: 100  → 2 pods (cada servicio)
Usuarios: 500  → 4 pods
Usuarios: 2000 → 8 pods
Usuarios: 5000 → 10 pods (máximo)
```

**StatefulSets:**
- `mongodb`: Persistencia garantizada
- `rabbitmq`: Cluster de 3 nodos para alta disponibilidad

**ConfigMaps:**
- Variables de entorno
- Configuración de conexiones

**Secrets:**
- Credenciales de MongoDB
- Credenciales de RabbitMQ

**Health Checks:**
- **Liveness Probe**: `/actuator/health/liveness` (reinicia si falla)
- **Readiness Probe**: `/actuator/health/readiness` (no envía tráfico si no está listo)

### **Beneficios de Kubernetes**

✅ **Auto-scaling**: Más pods cuando hay más usuarios  
✅ **Self-healing**: Reinicia pods si fallan  
✅ **Zero-downtime deployments**: Rolling updates  
✅ **Load balancing**: Distribuye tráfico entre pods  
✅ **Resource limits**: CPU/Memory garantizados

---

## 📊 Monitoreo

**RabbitMQ Management:**  
http://localhost:15672 (admin/admin123)

**MongoDB Compass:**  
`mongodb://admin:admin123@localhost:27017`

**Health Checks:**
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

---

## 📂 Estructura del Proyecto
```
game-matchmaking-system/
├── player-service/          # Microservicio de jugadores
│   └── src/main/java/
│       ├── domain/          # Lógica de negocio
│       └── infrastructure/  # MongoDB, RabbitMQ, REST
├── match-service/           # Microservicio de partidas
│   └── src/main/java/
│       ├── domain/          # Lógica de negocio + Event Sourcing
│       └── infrastructure/  # MongoDB, RabbitMQ, REST
├── docker-compose.yml       # Infra local (MongoDB + RabbitMQ)
├── k8s/                     # Manifests de Kubernetes (próximamente)
└── README.md
```

---

## 🎯 Roadmap

- [x] Microservicios con Hexagonal Architecture
- [x] CQRS en Player Service
- [x] Event Sourcing en Match Service
- [x] Comunicación asíncrona con RabbitMQ
- [x] Docker Compose para desarrollo
- [ ] Dockerfiles + imágenes
- [ ] Kubernetes manifests
- [ ] HPA configurado
- [ ] CI/CD con GitHub Actions
- [ ] Tests unitarios e integración
- [ ] Monitoring con Prometheus + Grafana

---

## 👤 Autor

**Ramiro** - Portfolio de arquitectura de microservicios

---

## 📝 Licencia

MIT