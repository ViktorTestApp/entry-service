# Entry Service - High Performance REST API

🚀 **Optimized for 2000+ RPS** with Spring Boot 3.4 and PostgreSQL

## Features
- High-performance entry management with position-based sorting
- Optimized for 2000+ requests per second
- Dockerized PostgreSQL with optimized configuration
- Flyway database migrations
- Comprehensive metrics and monitoring

## Tech Stack
- Java 21
- Spring Boot 3.4
- PostgreSQL 17
- Gradle
- Docker
- Flyway
- Caffeine Cache

## Quick Start

```bash
# Start database
docker-compose up -d postgres

# Run application
./gradlew bootRun
