# CLAUDE.md — Todo App Agent

## Rol

Je bent een coding agent die een full-stack todo web app bouwt voor Vince.
Je voert taken uit in volgorde, schrijft de code, en legt per stap kort uit wat je hebt gedaan en wat de volgende stap is.

---

## Project

**Doel**: Een responsive todo web app met login/registratie, max 5 taken per user, pastel design met dark mode.

**Stack**:
- Frontend: React (Vite), react-router-dom, axios
- Backend: Java 17, Spring Boot 3, Maven
- Database: PostgreSQL
- Auth: JWT (jjwt 0.9.1)

**Package root backend**: `com.vince.todo`

**Mappenstructuur backend**:
```
src/main/java/com/vince/todo/
├── model/          ← User.java, Todo.java
├── repository/     ← UserRepository.java, TodoRepository.java
├── service/        ← AuthService.java, TodoService.java
├── controller/     ← AuthController.java, TodoController.java
├── security/       ← JwtUtil.java, JwtAuthFilter.java, UserDetailsServiceImpl.java
└── config/         ← SecurityConfig.java
```

**Mappenstructuur frontend**:
```
frontend/src/
├── api/            ← api.js
├── pages/          ← LoginPage.jsx, RegisterPage.jsx, TodosPage.jsx
├── components/     ← (optioneel)
├── App.jsx
└── index.css
```

---

## Regels

1. Schrijf altijd **volledige bestanden**, geen snippets tenzij expliciet gevraagd.
2. Geef na elk bestand aan **waar het opgeslagen moet worden**.
3. Voeg **geen onnodige dependencies** toe buiten wat hieronder staat.
4. De backend draait op **poort 8080**, de frontend op **poort 5173**.
5. CORS is open voor `http://localhost:5173`.
6. Wachtwoorden worden gehashed met **BCrypt**.
7. JWT secret: `"mijn-geheime-sleutel-todo-app-2024"` (hardcoded voor MVP).
8. Max 5 todos per user wordt gehandhaafd in **TodoService**, niet in de database.
9. Schrijf **geen TypeScript**, alleen plain JavaScript voor de frontend.
10. Gebruik **geen externe UI libraries** (geen MUI, Tailwind, etc.) — alleen plain CSS met variabelen.

---

## Dependencies

### Backend (pom.xml)
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.2.0</version>
</parent>

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
  </dependency>
  <dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
  </dependency>
</dependencies>
```

### Frontend (installeren via terminal)
```bash
npm create vite@latest frontend -- --template react
cd frontend
npm install react-router-dom axios
```

---

## Database schema

```sql
CREATE TABLE users (
  id         SERIAL PRIMARY KEY,
  email      VARCHAR(255) UNIQUE NOT NULL,
  password   VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE todos (
  id         SERIAL PRIMARY KEY,
  user_id    INT REFERENCES users(id) ON DELETE CASCADE,
  title      VARCHAR(255) NOT NULL,
  done       BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT NOW()
);
```

---

## application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tododb
spring.datasource.username=postgres
spring.datasource.password=VERVANG_DIT
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080
```

---

## API contract

| Methode | Route           | Auth vereist | Body                          | Response                  |
|---------|-----------------|--------------|-------------------------------|---------------------------|
| POST    | /auth/register  | Nee          | `{ email, password }`         | 201                       |
| POST    | /auth/login     | Nee          | `{ email, password }`         | `{ token }`               |
| GET     | /todos          | Ja (JWT)     | —                             | `[ Todo ]`                |
| POST    | /todos          | Ja (JWT)     | `{ title }`                   | Todo of 400 bij max 5     |
| PUT     | /todos/{id}     | Ja (JWT)     | `{ title, done }`             | Todo of 403                |
| DELETE  | /todos/{id}     | Ja (JWT)     | —                             | 204 of 403                |

De ingelogde user wordt bepaald via:
```java
SecurityContextHolder.getContext().getAuthentication().getName() // geeft email
```

---

## CSS variabelen (design)

```css
:root {
  --bg:      #fdf6f0;
  --surface: #fff8f4;
  --text:    #3d2c2c;
  --accent:  #f4a9a8;
  --accent-2:#b5d5c5;
  --border:  #e8d5d0;
}

[data-theme="dark"] {
  --bg:      #1e1a1a;
  --surface: #2a2424;
  --text:    #f0e6e6;
  --accent:  #c97b7a;
  --accent-2:#7aab96;
  --border:  #3d3030;
}
```

Dark mode wordt geactiveerd via `document.documentElement.setAttribute('data-theme', 'dark')`.
Voorkeur wordt opgeslagen in `localStorage` onder de key `"theme"`.

---

## Taakvolgorde

Werk deze fases af in volgorde. Ga niet verder als een fase niet werkt.

```
FASE 1  — Projectstructuur opzetten
FASE 2  — Database schema uitvoeren
FASE 3  — Backend: User entity, Todo entity, Repositories
FASE 4  — Backend: Auth endpoints (zonder JWT), testen met Postman
FASE 5  — Backend: JWT toevoegen aan login + filter
FASE 6  — Backend: Todo endpoints (CRUD + max-5), testen met Postman
FASE 7  — Frontend: api.js, LoginPage, RegisterPage, App.jsx routing
FASE 8  — Frontend: TodosPage (lijst, aanmaken, editen, verwijderen)
FASE 9  — Design: CSS variabelen, component styling, dark mode toggle
FASE 10 — Testen op mobiel via lokaal IP
```

---

## Testinstructies per fase

**Na fase 4** — Postman: `POST http://localhost:8080/auth/register`
```json
{ "email": "test@test.nl", "password": "test123" }
```
Verwacht: HTTP 201

**Na fase 5** — Postman: `POST http://localhost:8080/auth/login`
```json
{ "email": "test@test.nl", "password": "test123" }
```
Verwacht: `{ "token": "eyJ..." }`

**Na fase 6** — Postman met header `Authorization: Bearer [token]`:
- `GET /todos` → lege array
- `POST /todos` met `{ "title": "Test taak" }` → todo object
- `PUT /todos/1` met `{ "title": "Aangepast", "done": true }` → updated todo
- `DELETE /todos/1` → 204

**Na fase 8** — Browser op `http://localhost:5173`:
- Registreren → inloggen → todos aanmaken, editen, verwijderen
- 6e todo aanmaken → melding zichtbaar

**Na fase 10** — Telefoon op zelfde wifi:
- Open `http://[laptop-ip]:5173`
- Volledig werkend op mobiel scherm

---

## Foutafhandeling

Als je een fout tegenkomt, geef dan:
1. De exacte foutmelding of stacktrace
2. Welke fase je bezig bent
3. Het bestand waar de fout zit

Dan los ik het op voordat we verdergaan.
