# AutoLot SaaS — Spring Boot Backend Learning Guide

## Context

Build the backend for a SaaS platform that serves car dealerships. Each dealership signs up and gets their own subdomain (e.g., `acme-motors.autolot.com`). Dealership admins manage car listings (with photos) and customize their website layout via a drag-and-drop builder. The public-facing dealership sites require no login — only admin operations need authentication.

**Key decisions:**
- Multi-tenancy: shared database with `tenant_id` column
- Auth: JWT tokens for admin users
- Images: local filesystem for MVP (designed to swap to S3 later)
- Tenant resolution: subdomain-based (`{slug}.yourapp.com`)
- Two API layers: public (no auth) and admin (JWT required)

---

## Data Model Overview

```
Dealership (tenant)
├── AdminUser (login to manage the dealership)
├── Vehicle (car listings)
│   └── VehicleImage (photos per vehicle)
├── SiteConfig (drag-and-drop layout JSON, theme, colors)
└── DealershipInfo (address, phone, logo, about text)
```

---

## Day 1: Project Setup & Core Entities

### Goals
- Bootstrap the Spring Boot project
- Create the Dealership (tenant) and AdminUser entities
- Get the database connected

### Tasks

**1.1 Create Spring Boot project**
- Go to [start.spring.io](https://start.spring.io)
- Dependencies: Spring Web, Spring Data JPA, Spring Security, PostgreSQL Driver, Validation
- Add manually to pom.xml: `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (v0.12.x)

**1.2 Configure application.yml**
- PostgreSQL datasource (create a DB called `autolot_db`)
- `spring.jpa.hibernate.ddl-auto: update`
- File upload config: `spring.servlet.multipart.max-file-size: 10MB`
- Custom property: `app.upload-dir: ./uploads` (for car images)
- JWT secret and expiration as custom properties

**1.3 Create the Dealership entity (this is your tenant)**
- Fields:
    - `id` (UUID, auto-generated)
    - `name` (String) — dealership business name
    - `slug` (String, unique) — used for subdomain, e.g., "acme-motors"
    - `email` (String) — primary contact email
    - `phone` (String)
    - `address` (String)
    - `logoUrl` (String, nullable)
    - `about` (Text, nullable) — about us blurb
    - `active` (boolean, default true)
    - `createdAt`, `updatedAt` (LocalDateTime)
- Validate slug format: lowercase, alphanumeric + hyphens only
- Create `DealershipRepository`

**1.4 Create the AdminUser entity**
- Fields:
    - `id` (UUID)
    - `dealership` (@ManyToOne → Dealership)
    - `email` (String, unique)
    - `password` (String, BCrypt hashed)
    - `fullName` (String)
    - `role` (enum: OWNER, ADMIN)
    - `createdAt`
- Create `AdminUserRepository`
- Add method: `findByEmail(String email)`

### Checkpoint
- Run the app, check that `dealerships` and `admin_users` tables exist in PostgreSQL
- Verify the foreign key relationship in the DB

---

## Day 2: Vehicle & Image Entities

### Goals
- Create the Vehicle listing entity with all standard fields
- Create VehicleImage entity for multiple photos per car
- Understand how file upload storage will work

### Tasks

**2.1 Create TenantScopedEntity base class**
- `@MappedSuperclass` — not a table, just a parent class
- Has a `@ManyToOne` to Dealership (the `tenant_id` column)
- Add Hibernate `@FilterDef` and `@Filter` for automatic tenant scoping
- All tenant-owned entities will extend this

**2.2 Create the Vehicle entity (extends TenantScopedEntity)**
- Fields:
    - `id` (UUID)
    - `make` (String) — e.g., Toyota, BMW
    - `model` (String) — e.g., Corolla, X5
    - `year` (int)
    - `price` (BigDecimal) — use BigDecimal for money, never double/float
    - `mileage` (int)
    - `vin` (String, nullable) — Vehicle Identification Number
    - `condition` (enum: NEW, USED, CERTIFIED_PRE_OWNED)
    - `transmission` (enum: AUTOMATIC, MANUAL)
    - `fuelType` (enum: PETROL, DIESEL, ELECTRIC, HYBRID)
    - `bodyType` (enum: SEDAN, SUV, TRUCK, COUPE, HATCHBACK, VAN, CONVERTIBLE)
    - `exteriorColor` (String)
    - `interiorColor` (String, nullable)
    - `description` (Text) — freeform description
    - `featured` (boolean) — whether to highlight on homepage
    - `status` (enum: AVAILABLE, SOLD, PENDING)
    - `createdAt`, `updatedAt`
- Create `VehicleRepository`

**2.3 Create the VehicleImage entity**
- Fields:
    - `id` (UUID)
    - `vehicle` (@ManyToOne → Vehicle)
    - `imageUrl` (String) — relative file path
    - `displayOrder` (int) — for image ordering
    - `primary` (boolean) — main display image
    - `createdAt`
- Create `VehicleImageRepository`

**2.4 Plan your file storage approach**
- Create an `uploads/` directory in your project
- Understand Spring's `MultipartFile` for handling uploads
- File naming strategy: `{dealershipSlug}/{vehicleId}/{uuid}.{ext}`
- This folder structure makes it easy to migrate to S3 later (same key structure)

### Checkpoint
- Run app, verify `vehicles` and `vehicle_images` tables exist
- Check that `vehicles` has a `dealership_id` foreign key column

---

## Day 3: Site Configuration Entity & JWT Auth

### Goals
- Create the SiteConfig entity for storing drag-and-drop layout data
- Implement JWT token generation and validation
- Set up password encoding

### Tasks

**3.1 Create the SiteConfig entity (extends TenantScopedEntity)**
- Fields:
    - `id` (UUID)
    - `layoutJson` (Text/JSON column) — stores the drag-and-drop layout
    - `theme` (String, default "default") — theme name
    - `primaryColor` (String) — hex color code
    - `secondaryColor` (String)
    - `fontFamily` (String, nullable)
    - `customCss` (Text, nullable) — for advanced users
    - `updatedAt`
- The `layoutJson` field stores the entire page layout as JSON
    - This is what the frontend drag-and-drop builder reads and writes
    - Example structure: `{"sections": [{"type": "hero", "order": 1, "config": {...}}, {"type": "inventory-grid", "order": 2, ...}]}`
- Consider using `@Column(columnDefinition = "jsonb")` for PostgreSQL JSONB type
- Create `SiteConfigRepository`

**3.2 Create JwtTokenProvider**
- Generate tokens containing: `userId`, `dealershipId`, `email`, `role`
- Validate tokens: check signature, check expiration
- Extract claims from token
- Use JJWT library

**3.3 Set up BCryptPasswordEncoder**
- Create a `@Bean` returning `BCryptPasswordEncoder`
- Test it: encode a password, verify it matches

### Checkpoint
- Generate a JWT, decode it at jwt.io — verify dealershipId is in the claims
- SiteConfig table exists with a `jsonb` column for layout

---

## Day 4: Security — Two API Layers

### Goals
- Configure Spring Security with two distinct API layers
- Public endpoints (no auth) for visitors browsing cars
- Admin endpoints (JWT required) for dealership management

### Tasks

**4.1 Create TenantContext (ThreadLocal)**
- Stores current dealership ID for the request
- `setDealershipId()`, `getDealershipId()`, `clear()`

**4.2 Create JwtAuthenticationFilter**
- Extends `OncePerRequestFilter`
- Only applies to `/api/admin/**` routes
- Extracts Bearer token, validates, sets TenantContext + SecurityContext

**4.3 Create SubdomainTenantFilter**
- A separate filter that runs on ALL requests
- Extracts subdomain from the `Host` header (e.g., `acme-motors.autolot.com` → `acme-motors`)
- Looks up the Dealership by slug
- Sets `TenantContext.setDealershipId()`
- For admin routes, the JWT filter will override this (or validate it matches)

**4.4 Configure SecurityConfig**
- Define the two layers:
  ```
  /api/public/**  → permitAll (no auth needed)
  /api/admin/**   → authenticated (JWT required)
  /api/auth/**    → permitAll (login/signup)
  ```
- Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
- Disable CSRF (stateless API)
- Enable CORS (frontend will be on different domain)

**4.5 Clear TenantContext after each request**
- Use a filter with `finally` block to ensure cleanup

### Checkpoint
- `GET /api/public/vehicles` returns 200 without auth
- `GET /api/admin/vehicles` returns 401 without auth
- Subdomain resolution works (test with Host header in Postman)

---

## Day 5: Auth Endpoints & Admin Vehicle CRUD

### Goals
- Implement dealership signup and admin login
- Build full CRUD for vehicle listings (admin side)

### Tasks

**5.1 Create AuthService and AuthController**
- `POST /api/auth/signup` — registers a new dealership:
    1. Validate slug is unique and properly formatted
    2. Create Dealership
    3. Create AdminUser with role=OWNER
    4. Create a default SiteConfig with starter layout JSON
    5. Return JWT
- `POST /api/auth/login` — admin login:
    1. Find user by email
    2. Verify password
    3. Return JWT

**5.2 Create VehicleService and Admin VehicleController**
- `POST /api/admin/vehicles` — create a listing
- `GET /api/admin/vehicles` — list all vehicles for this dealership (paginated)
- `GET /api/admin/vehicles/{id}` — get single vehicle
- `PUT /api/admin/vehicles/{id}` — update listing (price, status, details)
- `DELETE /api/admin/vehicles/{id}` — remove listing
- All endpoints auto-scoped by tenant via Hibernate filter
- When creating, auto-set the dealership from TenantContext

**5.3 Enable Hibernate tenant filter**
- Create an interceptor or aspect that enables the Hibernate filter
- Use `EntityManager.unwrap(Session.class).enableFilter("tenantFilter")`
- Pass `TenantContext.getDealershipId()` as parameter

**5.4 Handle exceptions**
- `DealershipNotFoundException` — invalid subdomain
- `EmailAlreadyExistsException`
- `VehicleNotFoundException`
- Global `@ControllerAdvice` to return consistent error JSON

### Checkpoint
- Sign up a dealership → get JWT → create 3 vehicles → list them
- Sign up a second dealership → list vehicles → should be empty (tenant isolation!)

---

## Day 6: Image Upload & Public API

### Goals
- Implement image upload for vehicle listings
- Build public-facing API that visitors use (no auth)

### Tasks

**6.1 Create FileStorageService**
- Method: `store(MultipartFile file, String dealershipSlug, UUID vehicleId)` → returns file path
- Method: `delete(String filePath)`
- Method: `loadAsResource(String filePath)` → returns `Resource` for serving
- Store files in: `{upload-dir}/{slug}/{vehicleId}/{uuid}.{ext}`
- Validate: only allow image types (jpg, png, webp), max 10MB
- Later you can create an S3 implementation of the same interface

**6.2 Create ImageController (admin)**
- `POST /api/admin/vehicles/{vehicleId}/images` — upload one or more images (multipart)
- `DELETE /api/admin/vehicles/{vehicleId}/images/{imageId}` — remove image
- `PUT /api/admin/vehicles/{vehicleId}/images/{imageId}/primary` — set as primary image
- `PUT /api/admin/vehicles/{vehicleId}/images/reorder` — change display order

**6.3 Create a static resource handler or image serving endpoint**
- Option A: configure Spring to serve `/uploads/**` as static resources
- Option B: create `GET /api/public/images/{path}` endpoint
- Make sure images are publicly accessible (visitors need to see car photos)

**6.4 Create Public API Controllers**
- These are what the dealership's public website calls (no auth needed):
- `GET /api/public/dealership` — dealership info (name, address, phone, logo, about)
    - Resolved via subdomain, no ID needed
- `GET /api/public/vehicles` — list available vehicles (paginated, filterable)
    - Query params: `make`, `model`, `minPrice`, `maxPrice`, `minYear`, `maxYear`, `bodyType`, `fuelType`, `condition`
    - Only return vehicles with `status=AVAILABLE`
    - Include primary image URL in response
- `GET /api/public/vehicles/{id}` — single vehicle detail with all images
- `GET /api/public/site-config` — returns the layout JSON + theme settings
    - This is what the frontend drag-and-drop renderer reads

### Checkpoint
- Upload 3 images to a vehicle
- Hit `GET /api/public/vehicles` (with subdomain Host header) — see vehicles with image URLs
- Hit `GET /api/public/site-config` — see the default layout JSON
- Verify images are accessible via their URLs

---

## Day 7: Site Config Management & Finishing Touches

### Goals
- Build the admin endpoint for the website builder's save/load
- Add pagination and sorting
- Polish and harden the API

### Tasks

**7.1 Create SiteConfig Admin Endpoints**
- `GET /api/admin/site-config` — load current layout + theme
- `PUT /api/admin/site-config` — save entire layout JSON + theme settings
    - This is the "Save" button in the drag-and-drop builder
- `PUT /api/admin/dealership` — update dealership info (name, phone, address, logo, about)

**7.2 Add Pagination**
- Use Spring Data's `Pageable` parameter in controllers
- Public vehicle listing should support: `?page=0&size=20&sort=price,asc`
- Return `Page<VehicleResponse>` with total count, pages, etc.

**7.3 Create proper DTOs / Response objects**
- Don't return entities directly — create response DTOs
- `VehicleResponse`, `VehicleDetailResponse` (with images), `DealershipResponse`
- Use a mapper pattern or manually map entity → DTO

**7.4 Add CORS Configuration**
- Allow requests from your frontend domain
- Configure allowed methods, headers, origins

**7.5 Add Swagger / OpenAPI documentation**
- Add `springdoc-openapi-starter-webmvc-ui` dependency
- Annotate controllers with `@Tag`, `@Operation`
- Accessible at `/swagger-ui.html` — very helpful for frontend team

### Checkpoint
- Full flow: signup → create vehicles → upload images → update site config
- Public API returns paginated, filterable vehicle listings
- Swagger UI shows all endpoints

---

## API Summary

### Auth (no auth required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register dealership + owner |
| POST | `/api/auth/login` | Admin login, returns JWT |

### Public (no auth, resolved by subdomain)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/dealership` | Dealership name, address, logo |
| GET | `/api/public/vehicles` | Paginated + filterable listings |
| GET | `/api/public/vehicles/{id}` | Single vehicle with all images |
| GET | `/api/public/site-config` | Layout JSON for website renderer |

### Admin (JWT required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET/PUT | `/api/admin/dealership` | View/update dealership info |
| CRUD | `/api/admin/vehicles` | Manage vehicle listings |
| POST | `/api/admin/vehicles/{id}/images` | Upload images |
| DELETE | `/api/admin/vehicles/{id}/images/{imgId}` | Delete image |
| GET/PUT | `/api/admin/site-config` | Load/save website layout |

---

## Key Concepts to Understand

| Concept | Why It Matters |
|---------|----------------|
| ThreadLocal | Holds dealership ID for the current request |
| Hibernate Filters | Auto-scopes all queries to current dealership |
| Subdomain Resolution | Maps `slug.yourapp.com` → dealership context |
| JWT Claims | Carries dealershipId so admin actions are scoped |
| MultipartFile | Spring's abstraction for handling file uploads |
| @MappedSuperclass | Shared tenant fields without extra table |
| Pageable | Spring Data's built-in pagination support |
| JSONB column | PostgreSQL native JSON storage for layout config |

---

## Common Mistakes to Avoid

1. **Using `double` for price** → Use `BigDecimal` for money, always
2. **Forgetting to clear TenantContext** → Dealership data leaks across requests
3. **Returning entities from controllers** → Use DTOs to control what's exposed
4. **Not validating image types** → Users could upload malicious files
5. **Hardcoding CORS origins** → Use config properties
6. **Storing absolute file paths in DB** → Store relative paths, construct full URL at serving time
7. **Missing @Transactional on signup** → Dealership saved but user creation fails = orphan data

---

## Testing Your Implementation

### Manual Testing (Postman)
```
1. POST /api/auth/signup {name: "Acme Motors", slug: "acme-motors", ...}  → JWT
2. POST /api/admin/vehicles (with JWT + vehicle data)                      → vehicle created
3. POST /api/admin/vehicles/{id}/images (multipart with photo)             → image uploaded
4. GET /api/public/vehicles (Host: acme-motors.localhost)                   → see listings
5. POST /api/auth/signup {slug: "best-cars"}                               → second dealership
6. GET /api/public/vehicles (Host: best-cars.localhost)                     → empty (isolation!)
7. PUT /api/admin/site-config (save layout JSON)
8. GET /api/public/site-config (Host: acme-motors.localhost)               → layout returned
```

### Subdomain Testing Locally
- Edit `/etc/hosts` and add:
  ```
  127.0.0.1  acme-motors.localhost
  127.0.0.1  best-cars.localhost
  ```
- Or use Postman with a custom `Host` header

---

## Future Enhancements (Post-MVP)

- Migrate file storage to AWS S3
- Add vehicle search with Elasticsearch
- Email notifications (new inquiry from website visitor)
- Custom domain support (dealership brings their own domain)
- Analytics dashboard (page views, most viewed vehicles)
- Stripe integration for subscription billing
- Refresh tokens for better security

---

## Resources

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Hibernate Filters](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#pc-filter)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Spring File Upload Guide](https://spring.io/guides/gs/uploading-files/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [PostgreSQL JSONB](https://www.postgresql.org/docs/current/datatype-json.html)
