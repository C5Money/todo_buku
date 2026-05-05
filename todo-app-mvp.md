# Todo App MVP — Projectplan voor Vince

## Wat ga je bouwen?

Een responsive todo web app met:
- Max **5 taken** per gebruiker (aanmaken, editen, verwijderen)
- **Lichte pastel kleuren** + werkende **dark mode**
- **Login & registratie** per gebruiker
- Werkt op **desktop én mobiel**

Stack die aansluit op wat je al kent: **React (frontend)**, **Java Spring Boot (backend)**, **PostgreSQL (database)**.

---

## Stap 1 — Projectstructuur opzetten

```
todo-app/
├── frontend/        ← React app (Vite)
└── backend/         ← Spring Boot API
```

Open IntelliJ → New Project → Maven of Gradle voor de backend.  
Voor de frontend: open een terminal in IntelliJ en run:

```bash
npm create vite@latest frontend -- --template react
cd frontend && npm install
```

---

## Stap 2 — Database schema (PostgreSQL)

Maak twee tabellen aan. Gebruik dit als startpunt in je database client (bijv. DBeaver of pgAdmin):

```sql
CREATE TABLE users (
  id        SERIAL PRIMARY KEY,
  email     VARCHAR(255) UNIQUE NOT NULL,
  password  VARCHAR(255) NOT NULL,
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

**Regel**: maximaal 5 todos per `user_id` — dit check je in de backend.

---

## Stap 3 — Backend (Java Spring Boot)

### Endpoints die je nodig hebt

| Methode | Route              | Wat het doet                        |
|---------|--------------------|-------------------------------------|
| POST    | `/auth/register`   | Nieuwe gebruiker aanmaken           |
| POST    | `/auth/login`      | Inloggen, JWT token terugkrijgen    |
| GET     | `/todos`           | Alle todos van ingelogde user       |
| POST    | `/todos`           | Nieuwe todo aanmaken (max 5 check)  |
| PUT     | `/todos/{id}`      | Todo editen                         |
| DELETE  | `/todos/{id}`      | Todo verwijderen                    |

### Dependencies voor je `pom.xml`

```xml
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
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.9.1</version>
</dependency>
```

### Max-5 check in de service laag

```java
public Todo createTodo(Long userId, String title) {
    long count = todoRepository.countByUserId(userId);
    if (count >= 5) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max 5 taken bereikt");
    }
    // ... maak todo aan
}
```

---

## Stap 4 — Frontend (React)

### Pagina's / routes

```
/login       ← Inloggen
/register    ← Registreren
/todos       ← Hoofdscherm (beschermd, alleen na login)
```

Installeer react-router voor navigatie:

```bash
npm install react-router-dom axios
```

### Componentenstructuur

```
src/
├── pages/
│   ├── LoginPage.jsx
│   ├── RegisterPage.jsx
│   └── TodosPage.jsx
├── components/
│   ├── TodoItem.jsx       ← Één taak met edit/delete knoppen
│   ├── TodoForm.jsx       ← Invoerveld voor nieuwe taak
│   └── Navbar.jsx
├── api/
│   └── api.js             ← Axios instance met JWT header
└── App.jsx
```

### JWT opslaan en meesturen

```js
// api/api.js
import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080' });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export default api;
```

---

## Stap 5 — Design (pastel + dark mode)

Gebruik CSS variabelen zodat dark mode één toggle is:

```css
:root {
  --bg:        #fdf6f0;
  --surface:   #fff8f4;
  --text:      #3d2c2c;
  --accent:    #f4a9a8;
  --accent-2:  #b5d5c5;
  --border:    #e8d5d0;
}

[data-theme="dark"] {
  --bg:        #1e1a1a;
  --surface:   #2a2424;
  --text:      #f0e6e6;
  --accent:    #c97b7a;
  --accent-2:  #7aab96;
  --border:    #3d3030;
}
```

Toggle in React:

```jsx
const [theme, setTheme] = useState('light');

const toggleTheme = () => {
  const next = theme === 'light' ? 'dark' : 'light';
  setTheme(next);
  document.documentElement.setAttribute('data-theme', next);
};
```

---

## Stap 6 — Lokaal testen

### Backend starten
In IntelliJ: run de `main()` van je Spring Boot applicatie.  
Zorg dat je `application.properties` klopt:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tododb
spring.datasource.username=postgres
spring.datasource.password=jouwwachtwoord
spring.jpa.hibernate.ddl-auto=update
```

### Frontend starten

```bash
cd frontend
npm run dev
```

Open `http://localhost:5173` in je browser.  
Test op mobiel: zorg dat je laptop en telefoon op hetzelfde wifi-netwerk zitten en open `http://[jouw-laptop-ip]:5173`.

---

## Stap 7 — Volgorde van bouwen (MVP first)

Doe dit in volgorde, niet alles tegelijk:

1. **Database** tabellen aanmaken
2. **Backend** register + login endpoints (zonder JWT eerst, test met Postman)
3. **Backend** JWT toevoegen
4. **Backend** todo endpoints + max-5 validatie
5. **Frontend** login + register pagina's
6. **Frontend** todo lijst ophalen en tonen
7. **Frontend** todo aanmaken, editen, verwijderen
8. **Design** pastel thema + dark mode toggle
9. **Testen** op mobiel

---

## Wat je leert in dit project

| Onderwerp            | Waar je het leert                        |
|----------------------|------------------------------------------|
| JWT authenticatie    | Backend login flow + frontend interceptor |
| Protected routes     | React Router met auth guard              |
| REST API integratie  | Axios + Spring Boot controller           |
| CSS theming          | CSS variabelen + dark mode               |
| Database relaties    | Foreign key users → todos                |
| Input validatie      | Max-5 check backend + UI feedback        |

---

## Claude gebruiken als coding copilot

Per stap kun je Claude vragen:
- *"Schrijf de Spring Boot TodoController met deze endpoints"*
- *"Maak een React component TodoItem met inline edit"*
- *"Schrijf de JWT filter klasse voor Spring Security"*

Geef altijd context mee: welke Java versie, wat je al hebt, wat de fout is. Dan krijg je bruikbare code terug, geen generieke voorbeelden.
