# 🎮 Game Matchmaking System

> Sistema de matchmaking en tiempo real para videojuegos multijugador construido con microservicios, Event-Driven Architecture y patrones modernos de diseño.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7-green.svg)](https://www.mongodb.com/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.13-orange.svg)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-ready-blue.svg)](https://www.docker.com/)

---

## 📋 Índice

- [Descripción](#-descripción)
- [Características Principales](#-características-principales)
- [Arquitectura](#️-arquitectura)
- [Stack Tecnológico](#️-stack-tecnológico)
- [Patrones Arquitectónicos](#️-patrones-arquitectónicos)
- [Comenzar](#-comenzar)
- [API Endpoints](#-api-endpoints)
- [Flujo de Negocio](#-flujo-de-negocio)
- [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 📖 Descripción

Sistema distribuido que gestiona el matchmaking de jugadores en tiempo real. Cuando 10 jugadores (5v5) se unen a la cola, el sistema automáticamente:

1. ✅ Crea una partida balanceada por rating (ELO)
2. ✅ Actualiza el estado de los jugadores en tiempo real
3. ✅ Calcula y actualiza ratings al finalizar la partida
4. ✅ Mantiene estadísticas completas (wins, losses, winRate)

**Todo comunicado mediante eventos asíncronos** sin necesidad de polling.

---

## ⭐ Características Principales

### **Microservicios Desacoplados**
- Player Service y Match Service se comunican únicamente por eventos
- Independientes en desarrollo y despliegue
- Resilientes a fallos del otro servicio

### **Event-Driven Architecture**
- Comunicación 100% asíncrona mediante RabbitMQ
- Sin dependencias síncronas entre servicios
- Escalable horizontalmente

### **Event Sourcing**
- Historial completo de todas las partidas
- Posibilidad de reconstruir estado desde eventos
- Auditoría completa de cambios

### **CQRS (Command Query Responsibility Segregation)**
- Modelos separados para escritura y lectura
- Consultas optimizadas sin afectar escrituras
- Mejor rendimiento en queries complejas

### **Hexagonal Architecture**
- Dominio aislado de infraestructura
- Fácil de testear y mantener
- Cambio de tecnologías sin afectar lógica de negocio

---

## 🏗️ Arquitectura
```
┌───────────────────────────────────────────────────────┐
│                   DOCKER COMPOSE                      │
├───────────────────────────────────────────────────────┤
│                                                        │
│  ┌──────────────────┐   RabbitMQ   ┌───────────────┐ │
│  │ Player Service   │◄──────────────►│ Match Service │ │
│  │  (Port 8080)     │ Event-Driven  │ (Port 8081)   │ │
│  │                  │                │               │ │
│  │ • Gestión players│                │ • Matchmaking │ │
│  │ • Ratings (ELO)  │                │ • Partidas    │ │
│  │ • CQRS           │                │ • Event Store │ │
│  └────────┬─────────┘                └───────┬───────┘ │
│           │                                  │         │
│           ▼                                  ▼         │
│    MongoDB (playerdb)              MongoDB (matchdb)  │
│    • players (write)               • match_events     │
│    • player_stats (read)           • matches          │
│                                                        │
└────────────────────────────────────────────────────────┘
```

### **Flujo de Comunicación**
```
1. Jugador se une a cola
   Player Service → RabbitMQ (PlayerJoinedQueueEvent)

2. Match Service escucha evento
   RabbitMQ → Match Service

3. Al llegar 10 jugadores: Crea partida
   Match Service → MongoDB (Event Sourcing)
   Match Service → RabbitMQ (MatchFoundEvent)

4. Player Service actualiza jugadores
   RabbitMQ → Player Service
   Player Service → MongoDB (status = IN_MATCH)

5. Partida finaliza
   Match Service → RabbitMQ (MatchCompletedEvent)

6. Player Service actualiza ratings
   RabbitMQ → Player Service
   Player Service → MongoDB (nuevos ratings y stats)
```

---

## 🛠️ Stack Tecnológico

### **Backend**
- **Java 17** - Lenguaje de programación
- **Spring Boot 3.5.7** - Framework
- **Spring Data MongoDB** - Persistencia NoSQL
- **Spring AMQP** - RabbitMQ integration
- **Lombok** - Reducción de boilerplate
- **Gradle** - Build automation

### **Infraestructura**
- **MongoDB 7** - Base de datos NoSQL
- **RabbitMQ 3.13** - Message broker
- **Docker Compose** - Orquestación de contenedores

### **Herramientas**
- **Spring Boot Actuator** - Health checks y métricas
- **MongoDB Compass** - Visualización de datos
- **Postman** - Testing de APIs

---

## 🏛️ Patrones Arquitectónicos

### **1. Hexagonal Architecture (Ports & Adapters)**

Separación clara entre dominio e infraestructura:
```
DOMAIN (Core)
├── Models: Entidades del negocio
├── Ports: Interfaces (contratos)
│   ├── Input: Use cases
│   └── Output: Repositorios, publishers
└── Services: Lógica de negocio

INFRASTRUCTURE
├── Input Adapters: REST Controllers, RabbitMQ Consumers
└── Output Adapters: MongoDB, RabbitMQ Publishers
```

**Beneficios:**
- Dominio independiente de frameworks
- Fácil cambio de base de datos o API
- Testing simplificado

---

### **2. CQRS (Player Service)**

Separación de modelos para escritura y lectura:

**Commands (Write):**
```
POST /players
POST /players/{id}/queue/join
     ↓
MongoDB: players (normalized)
```

**Queries (Read):**
```
GET /players/{id}/stats
GET /players/leaderboard
     ↓
MongoDB: player_stats (denormalized)
```

**Beneficios:**
- Consultas optimizadas sin afectar escrituras
- Escalado independiente de reads/writes
- Mejor rendimiento

---

### **3. Event Sourcing (Match Service)**

Guardar todos los eventos en vez del estado final:
```
EVENT STORE (match_events)
├── Event 1: MatchCreatedEvent (v1)
├── Event 2: MatchStartedEvent (v2)
└── Event 3: MatchCompletedEvent (v3)
      ↓ (replay events)
PROJECTION (matches)
Estado actual reconstruido desde eventos
```

**Beneficios:**
- Historial completo de cambios
- Auditoría y debugging
- Reconstrucción de estado en cualquier momento

---

### **4. Event-Driven Architecture**

Comunicación asíncrona entre servicios:
```
Player Service    →  RabbitMQ  →  Match Service
                  ←  RabbitMQ  ←
```

**Eventos:**
- `PlayerJoinedQueueEvent` → Match Service
- `MatchFoundEvent` → Player Service
- `MatchCompletedEvent` → Player Service

**Beneficios:**
- Servicios desacoplados
- Comunicación asíncrona
- Resiliente a fallos

---

## 🚀 Comenzar

### **Requisitos**
- Java 17+
- Docker & Docker Compose
- Git

### **1. Clonar repositorio**
```bash
git clone https://github.com/TU_USUARIO/game-matchmaking-system.git
cd game-matchmaking-system
```

### **2. Levantar infraestructura**
```bash
docker-compose up -d
```

**Servicios levantados:**
- MongoDB: `localhost:27017`
- RabbitMQ: `localhost:5672`
- RabbitMQ UI: http://localhost:15672 (admin/admin123)

### **3. Ejecutar servicios**

**Terminal 1 - Player Service:**
```bash
cd player-service
./gradlew bootRun
```

**Terminal 2 - Match Service:**
```bash
cd match-service
./gradlew bootRun
```

### **4. Verificar health**
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
| GET | `/api/v1/players/{id}` | Obtener jugador |
| GET | `/api/v1/players/{id}/stats` | Ver estadísticas |
| GET | `/api/v1/players/leaderboard` | Top jugadores |
| POST | `/api/v1/players/{id}/queue/join` | Unirse a cola |
| POST | `/api/v1/players/{id}/queue/leave` | Salir de cola |
| PUT | `/api/v1/players/{id}/rating` | Actualizar rating |

### **Match Service (8081)**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/matches/{id}` | Ver partida |
| GET | `/api/v1/matches/recent` | Partidas recientes |
| GET | `/api/v1/matches/queue/size` | Jugadores en cola |
| POST | `/api/v1/matches/{id}/start` | Iniciar partida |
| POST | `/api/v1/matches/{id}/complete` | Finalizar partida |

---

## 🎮 Flujo de Negocio

### **Ejemplo: Matchmaking completo**

**1. Crear 10 jugadores**
```bash
POST http://localhost:8080/api/v1/players
{
  "username": "Player1",
  "email": "player1@example.com"
}
```

**2. Unirse a la cola (uno por uno)**
```bash
POST http://localhost:8080/api/v1/players/{id}/queue/join
```

**Al 10mo jugador:**
```
✅ Match Service crea partida automáticamente
✅ Player Service actualiza 10 jugadores a IN_MATCH
✅ Todo mediante eventos (sin polling)
```

**3. Iniciar partida**
```bash
POST http://localhost:8081/api/v1/matches/{matchId}/start
```

**4. Completar partida**
```bash
POST http://localhost:8081/api/v1/matches/{matchId}/complete
{
  "winnerTeam": "TEAM_A",
  "durationSeconds": 1800
}
```

**5. Ver resultados**
```bash
GET http://localhost:8080/api/v1/players/leaderboard
```

**Resultado:**
- Team A: rating 1025 cada uno (+25)
- Team B: rating 980 cada uno (-20)
- Stats actualizadas automáticamente

---

## 📂 Estructura del Proyecto
```
game-matchmaking-system/
├── player-service/
│   └── src/main/java/com/gaming/player_service/
│       ├── domain/
│       │   ├── model/           # Entidades (Player, PlayerStats)
│       │   ├── port/
│       │   │   ├── input/       # Use cases (Commands, Queries)
│       │   │   └── output/      # Repositorios, Publishers
│       │   └── service/         # Lógica de negocio
│       └── infrastructure/
│           ├── adapter/
│           │   ├── input/
│           │   │   ├── rest/    # Controllers REST
│           │   │   └── messaging/  # RabbitMQ Consumers
│           │   └── output/
│           │       ├── persistence/  # MongoDB Adapters
│           │       └── messaging/    # RabbitMQ Publishers
│           └── config/          # Configuración
│
├── match-service/
│   └── src/main/java/com/gaming/match_service/
│       ├── domain/
│       │   ├── model/           # Match, Events (Event Sourcing)
│       │   ├── port/
│       │   │   ├── input/       # Use cases
│       │   │   └── output/      # EventStore, Repositories
│       │   └── service/         # Matchmaking, Event Sourcing
│       └── infrastructure/
│           ├── adapter/
│           │   ├── input/
│           │   │   ├── rest/    # Controllers REST
│           │   │   └── messaging/  # RabbitMQ Consumers
│           │   └── output/
│           │       ├── persistence/  # MongoDB EventStore
│           │       └── messaging/    # RabbitMQ Publishers
│           └── config/
│
├── docker-compose.yml           # Infraestructura local
└── README.md
```

---

## 🎯 Features Destacadas

### **1. Comunicación Asíncrona**
✅ Sin acoplamiento entre servicios  
✅ Resiliente a fallos  
✅ Escalable horizontalmente  

### **2. Event Sourcing**
✅ Historial completo de partidas  
✅ Auditoría de cambios  
✅ Reconstrucción de estado  

### **3. CQRS**
✅ Consultas optimizadas  
✅ Escrituras y lecturas separadas  
✅ Mejor rendimiento  

### **4. Hexagonal Architecture**
✅ Dominio independiente  
✅ Fácil de testear  
✅ Mantenible y extensible  

### **5. Production-Ready**
✅ Health checks  
✅ Error handling  
✅ Logging  
✅ Validaciones  

---

## 🤝 Contribuciones

Este es un proyecto de portfolio personal demostrando:
- ✅ Diseño de microservicios
- ✅ Event-Driven Architecture
- ✅ Patrones modernos (Hexagonal, CQRS, Event Sourcing)
- ✅ Comunicación asíncrona con RabbitMQ
- ✅ MongoDB para persistencia