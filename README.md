# NexusInventory — Supply Chain Management System

Full-stack inventory and order management platform for supply chain operations. Built with **Spring Boot** on the backend and **Kotlin + Jetpack Compose** on the mobile client.

## Project Structure

```
├── nexus-inventory-backend/    → REST API (Java 17, Spring Boot 3.2)
└── nexus-inventory-mobile/     → Android App (Kotlin, Jetpack Compose)
```

## Backend — Spring Boot REST API

### Stack
- Java 17 · Spring Boot 3.2 · Spring Data JPA · H2 (dev) / PostgreSQL (prod)
- Jakarta Validation · Lombok · JUnit 5 · Mockito

### Data Model

| Entity | Key Fields |
|---|---|
| `Product` | id, sku, name, price, stock, minStockLevel, category |
| `Category` | id, name, description |
| `Order` | id, customerName, status, totalAmount, createdAt |
| `OrderItem` | id, product, quantity, unitPrice, subtotal |

Order lifecycle: `PENDING` → `PROCESSING` → `COMPLETED` · Cancellable from `PENDING`/`PROCESSING` (restores stock).

### API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard` | Aggregated metrics (stock totals, active orders, low-stock alerts) |
| GET | `/api/products` | List products (optional `?search=` filter) |
| GET | `/api/products/{id}` | Single product by ID |
| GET | `/api/products/low-stock` | Products where `stock ≤ minStockLevel` |
| POST | `/api/products` | Register new product |
| PUT | `/api/products/{id}/stock` | Update stock quantity |
| GET | `/api/orders` | List orders (optional `?status=` filter) |
| GET | `/api/orders/{id}` | Single order with line items |
| POST | `/api/orders` | Create order — validates and deducts stock atomically |
| PUT | `/api/orders/{id}/status?status=` | Transition order status |

### Business Rules

- Stock is validated and deducted atomically when creating an order.
- Cancelling an order restores the reserved stock.
- Completed orders cannot be cancelled.
- All validation errors return structured JSON via `@RestControllerAdvice`.

### Running the Backend

```bash
cd nexus-inventory-backend
./mvnw spring-boot:run        # starts on port 8080
./mvnw test                   # runs unit tests
```

H2 console available at `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:nexusdb`, user: `sa`, no password).

### Tests

`OrderServiceTest` covers:
- Order creation with stock deduction
- Insufficient stock rejection
- Product-not-found handling
- Stock restoration on cancellation
- Status transition constraints

---

## Mobile — Android App (Kotlin)

### Stack
- Kotlin · Jetpack Compose · Material Design 3
- Retrofit 2 · OkHttp3 · Gson
- Coroutines · StateFlow · MVVM + Clean Architecture

### Architecture

```
app/src/main/java/com/nexus/inventory/
├── data/           → Retrofit service, DTOs, repository impl
├── domain/         → Models, repository interface, use cases
└── ui/             → Compose screens, ViewModels, theme
```

### Screens

| Screen | Description |
|---|---|
| **Dashboard** | Stock metrics, active orders count, low-stock product alerts |
| **Products** | Searchable product list with color-coded stock indicators |
| **Create Order** | Bottom sheet form — select products, set quantities, submit |
| **Update Stock** | Dialog to adjust product stock levels |

Stock indicators: 🟢 Sufficient · 🟡 Low · 🔴 Out of Stock

### UI States

Every screen implements three states: **Loading** (shimmer animation), **Success** (data), and **Error** (message + retry).

### Connecting to the Backend

The app defaults to `http://10.0.2.2:8080/` (Android Emulator → host loopback). For a physical device on the same network, update `RetrofitClient.kt`:

```kotlin
RetrofitClient.setBaseUrl("http://<YOUR_LOCAL_IP>:8080/")
```

### Running the App

1. Open `nexus-inventory-mobile/` in Android Studio.
2. Let Gradle sync.
3. Start the backend first (`./mvnw spring-boot:run`).
4. Run the app on an emulator (API 24+) or physical device.

---

## License

MIT
