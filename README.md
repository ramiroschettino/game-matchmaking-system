# 🎮 Game Matchmaking System

Microservices-based matchmaking system for online games using Spring Boot, MongoDB, RabbitMQ, and modern architectural patterns.

## 🏗️ Architecture

- **Player Service**: Manages player profiles, ratings (ELO), and matchmaking queue
    - Hexagonal Architecture
    - CQRS Pattern (Write/Read Models)

- **Match Service**: Creates balanced matches and tracks game history
    - Hexagonal Architecture
    - Event Sourcing Pattern

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.5.7**
- **MongoDB** - NoSQL database
- **RabbitMQ** - Message broker for async communication
- **Docker & Docker Compose** - Containerization
- **Gradle** - Build tool

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Git

### Run locally

1. **Clone the repository**
```bash
   git clone https://github.com/ramiroschettino/game-matchmaking-system.git
   cd game-matchmaking-system
```

2. **Start infrastructure (MongoDB + RabbitMQ)**
```bash
   docker-compose up -d
```

3. **Run Player Service**
```bash
   cd player-service
   ./gradlew bootRun
```

4. **Run Match Service** (in another terminal)
```bash
   cd match-service
   ./gradlew bootRun
```

5. **Verify services are running**
    - Player Service: http://localhost:8080/actuator/health
    - Match Service: http://localhost:8081/actuator/health
    - RabbitMQ Management: http://localhost:15672 (admin/admin123)

## 📊 Services

| Service | Port | Description |
|---------|------|-------------|
| Player Service | 8080 | Player management & matchmaking queue |
| Match Service | 8081 | Match creation & event sourcing |
| MongoDB | 27017 | Database |
| RabbitMQ | 5672 | Message broker |
| RabbitMQ Management | 15672 | Web UI (admin/admin123) |

## 🏛️ Architecture Patterns

- **Hexagonal Architecture**: Clear separation between domain logic and infrastructure
- **CQRS**: Separate models for reads and writes in Player Service
- **Event Sourcing**: Complete history of matches in Match Service
- **Event-Driven**: Async communication via RabbitMQ

## 🔮 Coming Soon

- [ ] Domain layer implementation
- [ ] REST Controllers
- [ ] RabbitMQ event publishers/consumers
- [ ] Matchmaking algorithm
- [ ] ELO rating calculator
- [ ] Kubernetes manifests
- [ ] Unit & Integration tests

## 📝 License

MIT License