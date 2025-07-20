# Медиа-платформа с микросервисной архитектурой

[![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)

Платформа для управления видео-контентом и социальным взаимодействием, построенная на микросервисной архитектуре с использованием реактивного программирования и современных DevOps-практик.

## 🏗 Архитектура системы

```mermaid
graph TD
    A[Клиент] --> B[API Gateway (userway)]
    B --> C[Сервис видео (wehosty)]
    B --> D[Социальный сервис (social-service)]
    C --> E[(PostgreSQL)]
    D --> E
    B --> F[Prometheus]
    F --> G[Grafana]
```
## 📦 Микросервисы

### 🚪 API Gateway (userway)
**Технологии**: Spring WebFlux, Spring Security, JWT
**Особенности**:
- Асинхронная обработка запросов
- Маршрутизация между сервисами
- Авторизация и аутентификация
- Интеграционные тесты (Testcontainers)

### 🎥 Видео сервис (wehosty)
**Технологии**: Spring MVC, Hibernate
**Особенности**:
- Загрузка и обработка видео
- Хранение метаданных в PostgreSQL
- Синхронная обработка запросов
- Локальное хранение видеофайлов

### 🤝 Социальный сервис (social-service)
**Технологии**: Spring WebFlux, R2DBC
**Особенности**:
- Управление комментариями и лайками
- Реактивное взаимодействие с БД
- Реaltime-обновления через WebSocket
- Интеграция с API Gateway

## 🚀 Запуск проекта

### Предварительные требования
- Docker 20.10+
- Docker Compose 2.0+

### Инструкция по запуску:
```bash
# Клонировать репозиторий
git clone https://github.com/yourusername/vkr-platform.git
cd vkr-platform

# Запуск всех сервисов
docker-compose up --build

# Остановка и очистка
docker-compose down -v
```

Порты сервисов:
- API Gateway: 8080
- Видео сервис: 8081
- Социальный сервис: 8082
- Prometheus: 9090
- Grafana: 3000

## 📊 Мониторинг

Система включает полноценный стек мониторинга:
- **Prometheus** - сбор метрик производительности
- **Grafana** - визуализация метрик (логин: admin, пароль: admin)

Пример дашборда в Grafana:
```grafana
Dashboard ID: 13213
```

## 🛠 Тестирование

Для запуска интеграционных тестов в сервисе userway:
```bash
cd userway
./mvnw test
```
