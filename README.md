# ☕ Coffee Order System

커피 주문 시스템 프로젝트입니다. 대용량 트래픽을 고려한 성능 최적화와 안정적인 주문 처리에 중점을 두어 개발되었습니다.

## 🛠 Tech Stack
- **Framework**: Spring Boot 4.0.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Caching**: Redis (Spring Data Redis)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle

---

## 🚀 Getting Started

프로젝트를 로컬 환경에서 실행하기 위해 다음 단계를 수행해 주세요.

### 1. Prerequisites
실행을 위해 아래의 인프라가 준비되어 있어야 합니다.
- **MySQL**: 3306 포트 (기본값)
- **Redis**: 6379 포트 (기본값)

### 2. Database Setup
MySQL에 접속하여 프로젝트에서 사용할 데이터베이스를 생성합니다.
```sql
CREATE DATABASE coffee_order DEFAULT CHARACTER SET utf8mb4;
```

### 3. Configuration
`src/main/resources/application.yml`에 기본 설정이 포함되어 있습니다. 
만약 환경이 다르다면 환경 변수를 설정하거나 `application-local.yml`을 생성하여 재정의할 수 있습니다.
- `DB_URL`: JDBC 연결 URL
- `DB_USERNAME`: 데이터베이스 계정
- `DB_PASSWORD`: 데이터베이스 비밀번호
- `REDIS_HOST`: Redis 호스트 (기본: localhost)
- `REDIS_PORT`: Redis 포트 (기본: 6379)

### 4. Execution
```bash
./gradlew bootRun
```

---

## ✨ Key Features

### 🛡️ Concurrency Control (Redisson Distributed Lock)
- **기능**: 동시 주문 시 재고 부족 및 포인트 초과 차감 방지
- **최적화 전략**: 동일한 상품에 대해 수백 건의 주문이 동시에 발생하거나, 한 사용자가 여러 기기에서 동시에 주문/충전을 시도할 때 발생할 수 있는 데이터 부정합(Race Condition)을 방지하기 위해 **Redisson 분산 락**을 도입했습니다.
- **구현 방식**:
    1. **MultiLock 사용**: 주문 시 `coffee-order:user:{userId}`와 `coffee-order:coffee:{coffeeId}` 두 가지 락을 `MultiLock`으로 묶어 원자적으로 획득합니다. 이를 통해 포인트 차감과 재고 차감이 동시에 안전하게 이루어집니다.
    2. **TransactionTemplate 활용**: 락을 획득한 후 `TransactionTemplate`을 사용하여 비즈니스 로직(재고 차감 -> 포인트 차감 -> 주문 저장)이 하나의 트랜잭션 내에서 실행되도록 보장합니다.
    3. **독립적 보호**: `PointService.charge` 및 `CoffeeService.decreaseStock` 메서드 각각에도 개별 락을 적용하여, 파사드(Facade)를 통하지 않는 직접적인 호출 시에도 데이터 정합성을 유지합니다.
    4. **Watchdog 기반 안정성**: 락 임계 시간(leaseTime)을 명시적으로 설정하지 않고 Redisson의 **Watchdog** 기능을 활용하여, 긴 트랜잭션이나 네트워크 지연 상황에서도 안전하게 락이 유지되고 해제되도록 설계했습니다.

### ⚡ Coffee List Caching (Redis)
- **기능**: 커피 메뉴 목록 조회 (`GET /api/coffees`)
- **최적화 전략**: 커피 메뉴는 변경 빈도가 낮고 조회 빈도가 매우 높습니다. 매번 DB를 조회하는 대신 **Redis 캐시**를 활용하여 응답 속도를 향상시켰습니다.
- **구현 방식**: `@Cacheable` 어노테이션과 `RedisCacheManager`를 활용한 캐시 레이어 구현

### 📊 Popular Menus
- 최근 7일간 가장 많이 주문된 상위 3개 메뉴를 실시간으로 집계하여 노출합니다.

### 💰 Point System
- 사용자별 잔액(Point) 관리 및 충전, 주문 시 차감 로직이 포함되어 있습니다.

### 📨 Event-Driven Async Order Processing
- **기능**: 주문 완료 후 외부 데이터 수집 플랫폼으로의 주문 정보 전송
- **최적화 전략**: 외부 플랫폼 전송은 네트워크 지연이나 장애 발생 가능성이 높습니다. 이를 주문 트랜잭션과 분리하여 **사용자 응답 속도를 보장**하고 시스템 간 결합도를 낮췄습니다.
- **구현 방식**:
    1. 주문 성공 시 `OrderCreatedEvent` 발행
    2. `@Async` 및 `@TransactionalEventListener`를 사용하여 트랜잭션 종료 후 비동기적으로 전송 로직 수행
    3. 전송 실패 시 `OrderHistory` 테이블에 실패 내역 저장 (Fault Tolerance)

### 🔄 Decoupled Scheduler Architecture (Separate Project)
- **기능**: 전송 실패한 주문 데이터의 자동 재전송 복구
- **설계 의도**: 메인 API 서버(`coffee-order`)가 여러 인스턴스로 확장(Scale-out)될 때, 내장 스케줄러가 중복 실행되는 문제를 방지하기 위해 **스케줄링 전용 서버를 물리적으로 분리**했습니다.
- **운용 방식**: 별도의 레파지토리(`coffee-order-scheduler`)에서 구동되는 단일 인스턴스가 공유 DB를 감시하며 실패 데이터를 주기적으로 재처리합니다.
