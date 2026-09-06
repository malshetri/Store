# Spring Boot Store API

An e-commerce REST API I am building with Java and Spring Boot while following Code with Mosh's Spring Boot course. This project is part of my learning portfolio, covering REST endpoints, relational data modeling, shopping carts, and authentication, with checkout and order management planned next.

**Status: in progress.** The descriptions below reflect the current source code. Application startup and endpoint behavior have not been runtime-verified as part of this documentation update.

## Current progress

| Area | What is built so far | Status |
| --- | --- | --- |
| Persistence | MySQL configuration, JPA entities and repositories, and two Flyway schema migrations | Implemented |
| Users | Registration with validation, duplicate-email checks, BCrypt password hashing, and password-free response DTOs; list, retrieve, update, and delete handlers | Implemented; protected operations await the authentication flow |
| Products | Create, retrieve, update, delete, and filter by category; category existence checks on writes | Implemented; routes currently require authentication |
| Shopping carts | Create UUID carts, add products, update quantities, remove items, clear carts, and calculate totals using `BigDecimal` | Implemented; public routes |
| Authentication | Email/password verification through Spring Security, a database-backed `UserDetailsService`, and stateless security configuration | In progress; login does not issue tokens |
| API documentation | springdoc OpenAPI dependency and controller annotations | In progress; documentation routes currently require authentication |
| Checkout, payments, and orders | Planned Stripe checkout, payment events, and customer order history | Planned |

Adding the same product to a cart increments its quantity. Quantity updates accept values from 1 to 1000. Cart totals use current product prices, and clearing a cart removes its items while retaining the cart itself.

Addresses, profiles, categories, and wishlists have persistence models, but no dedicated REST management endpoints. A small Thymeleaf greeting page and a `/hello` endpoint are also present; a complete storefront frontend is not included.

## Technology stack

| Technology | Use |
| --- | --- |
| Java 23 | Configured Java version |
| Spring Boot 3.4.1 / Spring MVC | Application framework and REST controllers |
| Maven Wrapper | Build and dependency management |
| Spring Data JPA / Hibernate | Persistence, relationships, queries, and entity graphs |
| MySQL / Flyway | Relational storage and versioned schema migrations |
| Spring Security / BCrypt | Credential verification and registration password hashing |
| Jakarta Bean Validation | Registration, login, and cart quantity validation |
| MapStruct 1.6.3 / Lombok 1.18.46 | DTO mapping and generated boilerplate |
| springdoc OpenAPI 2.8.6 | API documentation integration |
| Thymeleaf | Demonstration home page |
| Spring Boot Test / Spring Security Test | Test dependencies |

## Project structure

The application uses packages grouped by technical responsibility under `com.muneer.store`.

```text
src/main/java/com/muneer/store/
  SpringApiStarterApplication.java
  config/         Security configuration and authentication beans
  controllers/    REST endpoints, home page, and validation error handling
  dtos/           Request and response models
  entities/       JPA models and cart calculations
  exeptions/      Custom cart and product exceptions
  mappers/        MapStruct entity/DTO mappings
  repositories/   Spring Data repositories
  services/       Cart operations and user lookup for authentication
  validation/     Custom lowercase validation
src/main/resources/
  application.yaml
  db/migration/   Initial store schema and cart tables
  templates/      Demonstration home page
src/test/java/com/muneer/store/
  SpringApiStarterApplicationTests.java
```

Cart requests flow through a controller, service, and repository. User and product controllers currently perform their CRUD operations through repositories directly. DTOs shape responses, while entities model relationships and cart behavior.

## Run locally

You need JDK 23, a running MySQL server compatible with the migration scripts, and network access for the Maven Wrapper and dependencies on the first build. Set `JAVA_HOME` to your JDK installation. The cart migration uses MySQL's `uuid_to_bin(uuid())` and expression defaults.

Clone the repository:

```sh
git clone https://github.com/malshetri/Store.git
cd Store
```

Create an empty database using your MySQL client:

```sql
CREATE DATABASE IF NOT EXISTS store_api;
```

Use a local database account with permission to create and modify tables in this database. Override the datasource values in `application.yaml` with your own credentials in the terminal where you start the application.

**Windows PowerShell:**

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/store_api'
$env:SPRING_DATASOURCE_USERNAME = 'your-database-user'
$env:SPRING_DATASOURCE_PASSWORD = 'your-database-password'
.\mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```sh
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/store_api'
export SPRING_DATASOURCE_USERNAME='your-database-user'
export SPRING_DATASOURCE_PASSWORD='your-database-password'
./mvnw spring-boot:run
```

The default API base URL is `http://localhost:8080`. Flyway applies the migrations during startup:

- `V1__initial_migration.sql`: users, addresses, profiles, categories, products, and wishlist relationships.
- `V2__create_cart_tables.sql`: carts and cart items, including uniqueness for each cart/product pair.

The migrations do not seed categories or products. Testing cart item operations requires product data in your local database. Product creation also requires an existing category, and its API is currently protected by the unfinished authentication flow.

The Flyway Maven plugin has separate settings in `pom.xml`, using `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. The commands above use application startup migrations and the `SPRING_DATASOURCE_*` variables. JWT and Stripe configuration are not required by the current application.

## API routes

Only `/carts/**`, `POST /users`, and `POST /auth/login` are explicitly public in the current security configuration. All other requests require authentication. Login validates credentials and returns an empty `200 OK` response on success, but does not establish a session or return an access token. Protected routes therefore cannot yet be exercised through the current login flow.

| Method | Route | Purpose | Current access |
| --- | --- | --- | --- |
| POST | `/users` | Register a user | Public |
| POST | `/auth/login` | Check email/password credentials | Public |
| GET | `/users?sort=name` | List users, sorted by `name` or `email` | Protected |
| GET / PUT / DELETE | `/users/{id}` | Retrieve, update, or delete a user | Protected |
| POST | `/users/{id}/change-password` | Password-change handler; see limitations below | Protected |
| GET | `/products?catagoryId={id}` | List products, optionally filtered by category | Protected |
| POST | `/products` | Create a product | Protected |
| GET / PUT / DELETE | `/products/{id}` | Retrieve, update, or delete a product | Protected |
| POST | `/carts` | Create an empty cart | Public |
| GET | `/carts/{cartId}` | Retrieve items and totals | Public |
| POST | `/carts/{cartId}/items` | Add a product using `productId` | Public |
| PUT | `/carts/{cartId}/items/{productId}` | Set an item's `quantity` | Public |
| DELETE | `/carts/{cartId}/items/{productId}` | Remove an item | Public |
| DELETE | `/carts/{cartId}/items` | Clear all items | Public |

The category filter is currently spelled **`catagoryId`** in the query parameter. Product JSON uses **`categoryId`**.

### Example: registration and a guest cart

With the application running, use PowerShell to register a user and check their credentials. Choose an email that has not already been registered.

```powershell
$baseUrl = 'http://localhost:8080'

$registration = @{
    name = 'Demo User'
    email = 'demo@example.com'
    password = 'ExamplePass123!'
} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$baseUrl/users" -ContentType 'application/json' -Body $registration

$login = @{
    email = 'demo@example.com'
    password = 'ExamplePass123!'
} | ConvertTo-Json
Invoke-WebRequest -Method Post -Uri "$baseUrl/auth/login" -ContentType 'application/json' -Body $login

$cart = Invoke-RestMethod -Method Post -Uri "$baseUrl/carts"
Invoke-RestMethod -Uri "$baseUrl/carts/$($cart.id)"
```

Registration returns `id`, `name`, and `email`. A newly created cart returns `id`, an empty `items` list, and `totalPrice` of zero. Guest cart operations do not require registration or login.

Once your local database contains a product, substitute its ID below:

```powershell
$productId = 1 # Replace with an existing product ID
$item = @{ productId = $productId } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri "$baseUrl/carts/$($cart.id)/items" -ContentType 'application/json' -Body $item

$quantity = @{ quantity = 2 } | ConvertTo-Json
Invoke-RestMethod -Method Put -Uri "$baseUrl/carts/$($cart.id)/items/$productId" -ContentType 'application/json' -Body $quantity
Invoke-RestMethod -Uri "$baseUrl/carts/$($cart.id)"
```

### OpenAPI documentation

The project includes springdoc and endpoint annotations. The documentation paths are `/swagger-ui.html` and `/v3/api-docs`, but both currently fall under the authentication requirement. Public access to documentation remains part of the security work; Swagger UI has not been verified in a running instance for this README.

## Testing

The repository currently contains one `@SpringBootTest` test, `contextLoads()`. It checks application context startup; there is no feature-level test suite yet. It uses the application configuration and requires an accessible database for startup and migrations, so use a dedicated local test database.

```powershell
# Windows, after setting datasource variables for your test database
.\mvnw.cmd test
```

```sh
# macOS / Linux, after setting datasource variables for your test database
./mvnw test
```

No build, automated tests, or live API requests were run for this documentation update. Current feature status is based on source inspection.

## Roadmap

The intended course end state is a customer flow from product browsing and guest carts to authenticated checkout, payment confirmation, and order history.

- [ ] Issue and validate JWT access tokens, add refresh tokens, and expose the current user.
- [ ] Add `USER` and `ADMIN` roles, public catalog reads, and administrator-only product writes.
- [ ] Finish security rules for protected endpoints and public API documentation.
- [ ] Add order and order-item persistence with prices captured at checkout.
- [ ] Integrate Stripe-hosted checkout through a payment gateway interface.
- [ ] Verify Stripe webhook signatures and update order payment status.
- [ ] Add customer-scoped order lists and individual order retrieval with ownership checks.
- [ ] Add sample catalog data and development/production configuration.

### Current limitations and follow-up work

- The password-change handler compares strings directly and saves the new password without BCrypt encoding. It needs correction before it can work consistently with registered accounts.
- Account ownership checks and role-based authorization are not implemented. Guest carts have no user ownership association.
- Validation is implemented on selected requests; product writes and user updates need further validation work.
- Database credentials are currently stored in `application.yaml`. Local runs should override them as shown above; moving credentials out of tracked configuration remains a follow-up.
- Feature tests are still needed for registration, password handling, cart totals, and endpoint authorization, followed by checkout and webhook tests when those features are added.

