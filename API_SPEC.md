# 📖 Coffee Order System API Specification

이 문서는 Coffee Order System에서 제공하는 API 명세를 다룹니다. 모든 응답은 공통 응답 구조(`ApiResponse`)를 따릅니다.

## 🔗 공통 응답 구조 (Common Response)

```json
{
  "success": "boolean (성공 여부)",
  "code": "string (응답 코드, 성공 시 'SUCCESS')",
  "message": "string (응답 메시지)",
  "data": "T (실제 데이터)"
}
```

---

## ☕ 메뉴 API (Coffee API)

### 1. 전체 메뉴 조회
모든 커피 메뉴 목록을 조회합니다. 캐싱이 적용되어 있습니다 (1시간).

- **URL**: `/api/coffees`
- **Method**: `GET`
- **Response Data**: `List<CoffeeResponse>`

#### Response Example
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "성공",
  "data": [
    {
      "id": 1,
      "name": "아메리카노",
      "price": 4500
    },
    {
      "id": 2,
      "name": "카페라떼",
      "price": 5000
    }
  ]
}
```

### 2. 인기 메뉴 조회
최근 7일간의 주문 내역을 바탕으로 상위 3개 메뉴를 조회합니다. 캐싱이 적용되어 있습니다 (10분).

- **URL**: `/api/coffees/popular`
- **Method**: `GET`
- **Response Data**: `List<CoffeeResponse>`

---

## 💰 포인트 API (Point API)

### 1. 포인트 충전
사용자의 포인트를 충전합니다.

- **URL**: `/api/point/charge`
- **Method**: `POST`
- **Request Body**:
    - `userId` (String): 사용자 식별자
    - `amount` (Long): 충전할 금액

#### Request Example
```json
{
  "userId": "user123",
  "amount": 10000
}
```

#### Response Example
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "userId": "user123",
    "amount": 10000
  }
}
```

---

## 🛍️ 주문 API (Order API)

### 1. 커피 주문
커피를 주문합니다. 사용자 포인트가 차감되며 재고가 확인됩니다.

- **URL**: `/api/order`
- **Method**: `POST`
- **Request Body**:
    - `userId` (String): 사용자 식별자
    - `coffeeId` (Long): 커피 메뉴 ID
    - `amount` (Integer): 주문 수량

#### Request Example
```json
{
  "userId": "user123",
  "coffeeId": 1,
  "amount": 2
}
```

#### Response Example
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "성공",
  "data": {
    "orderId": 1,
    "userId": "user123",
    "coffeeId": 1,
    "quantity": 2,
    "totalPrice": 9000,
    "orderDate": "2024-04-06T12:00:00"
  }
}
```
