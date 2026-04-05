# ☕ Coffee Order System

**대용량 트래픽 환경에서도 정합성과 성능을 보장하는 커피 주문 시스템**입니다.  
분산 환경에서의 동시성 제어, Redis를 활용한 성능 최적화, 그리고 비동기 이벤트 기반의 안정적인 아키텍처를 지향합니다.

---

## 🛠 Tech Stack

| 분류 | 기술 스택                       |
| :--- |:----------------------------|
| **Framework** | Spring Boot 4.0.5 (Java 17) |
| **Database** | MySQL 8.0                   |
| **Caching/Lock** | Redis (Redisson)            |
| **ORM** | Spring Data JPA             |
| **Build Tool** | Gradle                      |
| **Testing** | JUnit5, Mockito             |

---

## 🚀 시작하기 (Getting Started)

로컬 환경에서 프로젝트를 실행하기 위한 단계입니다.

### 1. 사전 준비 (Prerequisites)
- **MySQL**: 3306 포트
- **Redis**: 6379 포트

### 2. 데이터베이스 설정
```sql
CREATE DATABASE coffee_order DEFAULT CHARACTER SET utf8mb4;
```

### 3. 설정 (Configuration)
`src/main/resources/application.yml`에 기본 설정이 포함되어 있습니다. 필요 시 환경 변수를 통해 재정의 가능합니다.
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`

### 4. 실행
```bash
./gradlew bootRun
```

---

## ✨ 핵심 기능 및 기술적 의사결정

### 🛡️ Redisson을 이용한 분산 락 (동시성 제어)
*   **문제**: 동일 상품에 대한 동시 주문이나 한 사용자의 동시 충전/주문 시 데이터 부정합(Race Condition) 발생 가능성.
*   **해결**: **Redisson Distributed Lock**을 도입하여 정합성을 보장합니다.
*   **상세 구현**:
    *   **MultiLock 활용**: 주문 시 `user`와 `coffee` 두 자원에 대해 원자적 락을 획득하여 포인트와 재고를 안전하게 처리합니다.
    *   **TransactionTemplate**: 락 획득 범위와 트랜잭션 범위를 일치시켜, 트랜잭션 종료 전 락이 해제되는 문제를 방지했습니다.
    *   **Watchdog**: 임계 시간을 직접 설정하지 않고 Redisson의 Watchdog 기능을 활용하여 네트워크 지연 등 예외 상황에서도 안전하게 락을 유지합니다.

### ⚡ Redis 캐싱 전략 및 성능 최적화
*   **기능**: 인기 메뉴 및 커피 목록 조회 성능 극대화.
*   **성능 최적화 (N+1 해결)**: 
    *   인기 메뉴 조회 시 개별 아이템을 루프로 조회하던 방식에서 **배치 조회(`findAllByIds`)** 방식으로 개선하여 DB 부하를 최소화했습니다.
*   **캐시 정책 (`CacheConfig`)**:
    *   **인기 메뉴 (`popularMenus`)**: 10분 TTL (최신 주문 트렌드 반영)
    *   **전체 메뉴 (`coffees`)**: 1시간 TTL (데이터 일관성 유지)
    *   **객체 직렬화**: 역직렬화 시 성능과 호환성을 위해 **JSON 포맷**을 사용하며, DTO의 기본 생성자를 통해 안정적인 데이터 복원을 보장합니다.

### 📊 실시간 인기 메뉴 집계
*   최근 7일간의 주문 내역을 바탕으로 상위 3개 메뉴를 실시간으로 집계하여 제공합니다. 캐시를 통해 대용량 요청에도 안정적인 응답 속도를 보장합니다.

### 💰 포인트 및 주문 시스템
*   **다중 수량 주문**: 한 번의 주문으로 여러 잔의 커피를 구매할 수 있도록 수량(`quantity`) 기능을 지원합니다.
*   **정밀한 금액 계산**: 주문 시점의 메뉴 단가와 수량을 곱하여 **총 결제 금액(`totalPrice`)**을 산출하고, 이를 기반으로 포인트를 차감합니다.
*   **이력 관리**: 가격 변동에 대비하여 주문 당시의 결제 금액을 영구 저장함으로써 데이터 추적성을 확보했습니다.

### 📨 비동기 이벤트 기반 주문 처리
*   **문제**: 외부 데이터 수집 플랫폼 전송 실패나 지연이 주문 트랜잭션에 영향을 주는 현상 방지.
*   **해결**: `OrderCreatedEvent` 발행 및 `@Async` 리스너를 통한 비동기 처리.
*   **내결함성(Fault Tolerance)**: 전송 실패 시 `OrderHistory` 테이블에 주문 당시의 **메뉴 ID, 수량, 결제 금액**을 상세히 기록하여 추후 유실 없는 복구가 가능하도록 설계했습니다.

### 🔄 외부 스케줄러를 통한 데이터 복구 (Decoupled Architecture)
*   **문제**: 메인 API 서버의 확장(Scale-out) 시 내장 스케줄러의 중복 실행 문제.
*   **해결**: 스케줄링 로직을 물리적으로 분리된 별도 프로젝트로 관리합니다.
*   **관련 프로젝트**: [Coffee System Scheduler](https://github.com/ilsamkim/coffee-system-scheduler)
    *   `OrderHistory`의 실패 내역을 주기적으로 감시하고 자동으로 재전송을 시도하여 데이터 유실을 방지합니다.
