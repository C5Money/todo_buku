# Claude Taken — Todo App Bouwen via IntelliJ Terminal

Voer deze taken **in volgorde** uit. Kopieer elke prompt naar Claude, voer de gegenereerde code uit, en ga pas naar de volgende stap als het werkt.

---

## FASE 1 — Projectstructuur

### Taak 1.1 — Spring Boot project aanmaken
```
Maak een Spring Boot project aan met Maven voor een todo app.
Java versie: 17
Dependencies: spring-boot-starter-web, spring-boot-starter-security, spring-boot-starter-data-jpa, postgresql driver, jjwt 0.9.1
Geef me de volledige pom.xml en de mappenstructuur die ik moet aanmaken.
```

### Taak 1.2 — React frontend aanmaken
Voer dit zelf uit in de IntelliJ terminal:
```bash
npm create vite@latest frontend -- --template react
cd frontend
npm install react-router-dom axios
```

---

## FASE 2 — Database

### Taak 2.1 — SQL schema
```
Schrijf het PostgreSQL SQL script voor een todo app met twee tabellen:
- users: id, email (unique), password, created_at
- todos: id, user_id (foreign key naar users met cascade delete), title, done (boolean, default false), created_at
Voeg ook een comment toe dat max 5 todos per user_id gehandhaafd wordt in de applicatielaag.
```

### Taak 2.2 — application.properties
```
Schrijf de application.properties voor een Spring Boot app die verbinding maakt met een lokale PostgreSQL database genaamd "tododb".
Gebruikersnaam: postgres
Wachtwoord: [VINK AAN WAT JOUW WACHTWOORD IS]
Zet ddl-auto op update zodat JPA de tabellen beheert.
Zet ook CORS open voor localhost:5173.
```

---

## FASE 3 — Backend: Entiteiten & Repositories

### Taak 3.1 — User entity
```
Schrijf een Java JPA Entity klasse "User" voor Spring Boot met deze velden:
id (Long, auto-generated), email (String, unique, not null), password (String, not null), createdAt (LocalDateTime).
Package: com.vince.todo.model
Implementeer ook UserDetails van Spring Security.
```

### Taak 3.2 — Todo entity
```
Schrijf een Java JPA Entity klasse "Todo" voor Spring Boot met deze velden:
id (Long, auto-generated), userId (Long), title (String, not null), done (boolean, default false), createdAt (LocalDateTime).
Package: com.vince.todo.model
```

### Taak 3.3 — Repositories
```
Schrijf twee Spring Data JPA repository interfaces:
1. UserRepository voor de User entity met een methode findByEmail(String email) die Optional<User> teruggeeft.
2. TodoRepository voor de Todo entity met methoden:
   - findAllByUserId(Long userId)
   - countByUserId(Long userId)
Package: com.vince.todo.repository
```

---

## FASE 4 — Backend: Auth (zonder JWT eerst)

### Taak 4.1 — AuthController basis
```
Schrijf een Spring Boot REST controller "AuthController" met twee endpoints:
POST /auth/register — ontvangt { email, password }, slaat gebruiker op met BCrypt gehashd wachtwoord, geeft 201 terug.
POST /auth/login — ontvangt { email, password }, valideert credentials, geeft voorlopig alleen { message: "login ok", email: "..." } terug (nog geen JWT).
Package: com.vince.todo.controller
Gebruik een AuthService die de logica bevat.
Schrijf ook de AuthService klasse.
```

### Taak 4.2 — Spring Security config (permissief voor nu)
```
Schrijf een Spring Security configuratie klasse voor Spring Boot 3 die:
- /auth/** volledig open zet (geen authenticatie nodig)
- CSRF uitschakelt
- CORS toestaat voor http://localhost:5173
- Voor alle andere routes voorlopig ook open zet (we voegen JWT later toe)
Package: com.vince.todo.config
```

**→ Test nu met Postman: POST http://localhost:8080/auth/register met { "email": "test@test.nl", "password": "test123" }**

---

## FASE 5 — Backend: JWT toevoegen

### Taak 5.1 — JWT utility
```
Schrijf een JwtUtil klasse in Spring Boot met deze methoden:
- generateToken(String email): String — maakt een JWT token aan met 24 uur geldigheid
- extractEmail(String token): String — haalt email uit token
- isTokenValid(String token, String email): boolean
Gebruik de jjwt 0.9.1 library (io.jsonwebtoken).
Gebruik een hardcoded secret key voor nu: "mijn-geheime-sleutel-todo-app-2024"
Package: com.vince.todo.security
```

### Taak 5.2 — JWT filter
```
Schrijf een OncePerRequestFilter klasse "JwtAuthFilter" voor Spring Boot die:
- De Authorization header leest (Bearer token)
- Het token valideert via JwtUtil
- Bij geldig token een UsernamePasswordAuthenticationToken zet in de SecurityContext
Package: com.vince.todo.security
Schrijf ook de UserDetailsService implementatie die users ophaalt via UserRepository.
```

### Taak 5.3 — Security config updaten met JWT
```
Update de Spring Security configuratie klasse zodat:
- /auth/** nog steeds open is
- Alle andere routes authenticatie vereisen
- De JwtAuthFilter toegevoegd is voor UsernamePasswordAuthenticationFilter
- De AuthController nu een echte JWT token teruggeeft bij succesvolle login als { "token": "..." }
Geef de volledige bijgewerkte SecurityConfig en AuthController.
```

**→ Test: login geeft nu een JWT token terug. Kopieer die token.**

---

## FASE 6 — Backend: Todo endpoints

### Taak 6.1 — TodoService
```
Schrijf een TodoService klasse in Spring Boot met deze methoden:
- getTodos(Long userId): List<Todo>
- createTodo(Long userId, String title): Todo — gooit een ResponseStatusException BAD_REQUEST als er al 5 of meer todos zijn voor deze user
- updateTodo(Long todoId, Long userId, String title, Boolean done): Todo — gooit 403 als de todo niet van deze user is
- deleteTodo(Long todoId, Long userId): void — gooit 403 als de todo niet van deze user is
Package: com.vince.todo.service
```

### Taak 6.2 — TodoController
```
Schrijf een TodoController in Spring Boot met deze endpoints:
GET    /todos        — geeft alle todos terug van de ingelogde user
POST   /todos        — body: { "title": "..." }, maakt nieuwe todo aan (max 5)
PUT    /todos/{id}   — body: { "title": "...", "done": true/false }, update todo
DELETE /todos/{id}   — verwijdert todo

Haal de ingelogde user op via SecurityContextHolder.getContext().getAuthentication().getName() (dat geeft het email terug).
Gebruik de TodoService.
Package: com.vince.todo.controller
```

**→ Test alle 4 endpoints met Postman. Gebruik de JWT token als Authorization: Bearer [token] header.**

---

## FASE 7 — Frontend: Login & Register

### Taak 7.1 — Axios instance
```
Schrijf een api.js bestand voor een React Vite app (geen TypeScript) dat:
- Een axios instance aanmaakt met baseURL http://localhost:8080
- Een request interceptor heeft die de JWT token uit localStorage pakt (key: "token") en als Authorization header meestuurt
Locatie: src/api/api.js
```

### Taak 7.2 — LoginPage
```
Schrijf een React component LoginPage.jsx voor een todo app.
- Formulier met email en password input
- Bij submit: POST naar /auth/login via de axios instance uit src/api/api.js
- Bij succes: sla de token op in localStorage als "token", redirect naar /todos via useNavigate
- Bij fout: toon foutmelding onder het formulier
- Onderaan een link naar /register
- Geen externe component libraries, alleen plain React en CSS classes
Locatie: src/pages/LoginPage.jsx
```

### Taak 7.3 — RegisterPage
```
Schrijf een React component RegisterPage.jsx voor een todo app.
- Formulier met email en password input
- Bij submit: POST naar /auth/register via de axios instance uit src/api/api.js
- Bij succes: redirect naar /login
- Bij fout: toon foutmelding
- Onderaan een link naar /login
Locatie: src/pages/RegisterPage.jsx
```

### Taak 7.4 — App.jsx met routing
```
Schrijf de App.jsx voor een React Vite app met react-router-dom v6.
Routes:
- / redirect naar /login
- /login → LoginPage
- /register → RegisterPage
- /todos → TodosPage (beschermd: als geen token in localStorage, redirect naar /login)
Maak ook een ProtectedRoute component inline in App.jsx.
Locatie: src/App.jsx
```

---

## FASE 8 — Frontend: Todo pagina

### Taak 8.1 — TodosPage
```
Schrijf een React component TodosPage.jsx voor een todo app.
Functionaliteit:
- Bij laden: GET /todos, zet resultaat in state
- Toon alle todos in een lijst
- Boven de lijst: invoerveld + knop om nieuwe todo toe te voegen (POST /todos), alleen zichtbaar als er minder dan 5 todos zijn
- Elke todo heeft: tekst, checkbox voor done (PUT /todos/{id}), edit knop (inline editen), delete knop (DELETE /todos/{id})
- Logout knop die localStorage leegmaakt en redirect naar /login
- Toon een melding "Je hebt het maximum van 5 taken bereikt" als er al 5 todos zijn
Locatie: src/pages/TodosPage.jsx
Gebruik de axios instance uit src/api/api.js
```

---

## FASE 9 — Design

### Taak 9.1 — CSS variabelen en dark mode
```
Schrijf een index.css bestand voor een React app met:
- CSS variabelen voor een pastel light theme: zachte achtergrondkleur (roomwit/lichtroze), zachte accenten (zalmroze, mintgroen), donkere tekst
- CSS variabelen voor dark mode onder [data-theme="dark"]: donkere achtergronden, dezelfde accentkleuren iets donkerder
- Basis body styling met de variabelen
- Responsive layout: max-width 480px gecentreerd, werkt ook op mobiel
Locatie: src/index.css
```

### Taak 9.2 — Dark mode toggle toevoegen
```
Voeg een dark mode toggle toe aan de TodosPage.jsx.
- Een knop (🌙 / ☀️) die toggle tussen light en dark
- Sla de voorkeur op in localStorage als "theme"
- Laad de voorkeur bij het mounten van het component
- Zet het data-theme attribuut op document.documentElement
Geef alleen de aanpassingen die ik moet doen in TodosPage.jsx, niet het hele bestand opnieuw.
```

### Taak 9.3 — Styling van componenten
```
Schrijf CSS klasses voor deze elementen van een todo app, gebruik makend van CSS variabelen (--bg, --surface, --text, --accent, --accent-2, --border):
- .container: gecentreerde card met padding, afgeronde hoeken, subtiele schaduw
- .input: text input veld, full width, met border en focus state
- .btn-primary: primaire knop met accent kleur
- .btn-danger: verwijder knop, subtiel rood
- .btn-ghost: transparante knop voor edit
- .todo-item: een todo rij met checkbox, tekst en knoppen naast elkaar
- .todo-item.done: doorgestreepte tekst als done=true
Voeg dit toe aan src/index.css
```

---

## FASE 10 — Testen op mobiel

### Taak 10.1 — Je lokale IP vinden
Voer dit uit in je IntelliJ terminal:
```bash
# Windows:
ipconfig

# Mac/Linux:
ifconfig | grep "inet "
```
Zoek het IP dat begint met `192.168.` of `10.`.

Open op je telefoon: `http://[jouw-ip]:5173`

### Taak 10.2 — Als het niet werkt op mobiel
```
Mijn React Vite app draait op localhost:5173 maar is niet bereikbaar op mijn telefoon via het lokale netwerk.
Hoe configureer ik Vite zodat het op alle netwerk interfaces luistert?
Geef de aanpassing in vite.config.js
```

---

## Volgorde checklist

- [ ] 1.1 pom.xml aangemaakt
- [ ] 1.2 React frontend aangemaakt
- [ ] 2.1 SQL uitgevoerd in database
- [ ] 2.2 application.properties ingesteld
- [ ] 3.1–3.3 Entities en repositories
- [ ] 4.1–4.2 Auth zonder JWT — getest met Postman
- [ ] 5.1–5.3 JWT — token teruggekregen via Postman
- [ ] 6.1–6.2 Todo endpoints — alle 4 getest met Postman
- [ ] 7.1–7.4 Frontend auth pagina's
- [ ] 8.1 Todo pagina
- [ ] 9.1–9.3 Styling
- [ ] 10 Getest op mobiel
