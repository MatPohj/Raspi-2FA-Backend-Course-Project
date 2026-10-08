# Raspi NFC 2FA

A Spring Boot web application that adds a **physical second factor** to an admin login: after an administrator signs in with a username and password, they must tap a registered NFC tag on a Raspberry Pi reader before they are let into any protected page. Regular users log in with just a password.

The app also includes a small CRUD feature for tracking personal game statistics, so there is a real, role-protected resource behind the login to demonstrate the access control.

This was my course project for a backend development course. The goal was to build a working Spring Boot application with real authentication and persistence, and to extend it with something of my own — in this case a hardware-backed 2FA flow driven by a Raspberry Pi and a MFRC522 RFID module.

## How the NFC 2FA flow works

The web server and the Raspberry Pi never talk to each other directly. They are linked by a short-lived **session ID** that the browser and the Pi both reference, so a tap only counts for the login attempt that created it.

```
Browser                    Spring Boot server                 Raspberry Pi
   │                              │                                 │
   │  1. POST /login (user+pass)  │                                 │
   │─────────────────────────────▶                                 │
   │                              │  admin? → not NFC-verified yet  │
   │  2. redirect /nfc-verification                                 │
   │◀─────────────────────────────                                 │
   │                              │  create session id, show it     │
   │  3. poll /api/verification/status?sessionId=…                  │
   │─────────────────────────────▶                                 │
   │                              │                                 │
   │                              │   4. tap tag; POST /api/nfc/validate
   │                              │      {tagId, username, sessionId}│
   │                              │◀────────────────────────────────│
   │                              │  tag belongs to user AND         │
   │                              │  sessionId matches user? → mark  │
   │                              │  session verified                │
   │  5. next poll → completed    │                                 │
   │◀─────────────────────────────                                 │
   │  redirect / (access granted) │                                 │
```

1. The admin submits username and password. Spring Security authenticates the credentials normally.
2. A custom `NfcAuthenticationFilter` runs before every protected request. If the authenticated user has the `ADMIN` role and the session is **not yet** marked `nfcVerified`, the filter redirects them to `/nfc-verification`.
3. The verification page creates a fresh session ID (`VerificationSessionService`), shows it to the user, and polls `/api/verification/status` in the background.
4. On the Pi, `send_data.py` reads the tag and POSTs `{tagId, username, sessionId}` to `/api/nfc/validate`. The server checks that the tag is registered to that user **and** that the session ID belongs to that same user before marking the session complete.
5. The browser's next poll sees `completed: true` and redirects the admin into the application.

Tying the tap to a server-issued session ID is the key detail: a valid tag alone is not enough, it has to arrive against the pending login session for that exact user.

## Tech stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 3.4 |
| Security | Spring Security (form login + custom `OncePerRequestFilter`), BCrypt |
| Persistence | Spring Data JPA — H2 (in-memory) for dev, PostgreSQL for production |
| Views | Thymeleaf + `thymeleaf-extras-springsecurity6` |
| Hardware client | Raspberry Pi, MFRC522 RFID reader, Python (`mfrc522`, `RPi.GPIO`, `requests`) |
| Deployment | Heroku (Procfile), PostgreSQL add-on |

## Project structure

```
src/main/java/com/matpohj/nfc_2fa/
├── config/        DataInitializer (seed users + tags), SecurityConfig
├── controller/    Auth, Home, Nfc, Verification, GameStats
├── model/         User, NfcTag, GameStats (JPA entities)
├── repository/    Spring Data JPA repositories
├── security/      NfcAuthenticationFilter (the 2FA gate)
└── service/       User, Nfc, GameStats, VerificationSession services
src/main/resources/
├── templates/     Thymeleaf pages (login, nfc-verification, game-stats, …)
└── application-{dev,prod}.properties
raspberry scripts/
├── send_data.py   NFC client: read tag → validate against the server
├── read_test.py   quick reader sanity check
└── testscript.py
```

## Running it locally

Requires JDK 21. The bundled Maven wrapper handles the rest.

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile uses an in-memory H2 database (console at `/h2-console`) and seeds two accounts from `application-dev.properties`:

| User | Password | Role | NFC required |
| --- | --- | --- | --- |
| `admin` | `adminpassword` | ADMIN + USER | yes |
| `user` | `userpassword` | USER | no |

> These are seed credentials for local development only. In production the admin password is injected from the `APP_ADMIN_PASSWORD` environment variable, and the demo NFC tag ID in `application-dev.properties` should be replaced with your own.

Open http://localhost:8080, log in as `user` to see the app directly, or as `admin` to be sent through the NFC verification step.

### The Raspberry Pi side

Wire an MFRC522 reader to the Pi over SPI and install the Python dependencies (`mfrc522`, `RPi.GPIO`, `requests`). When an admin reaches the verification page, read the session ID shown there and run:

```sh
python3 send_data.py \
  --server http://<server-ip>:8080 \
  --username admin \
  --session <session-id-from-the-page>
```

Tap the tag registered to that admin and the browser completes the login automatically.

## What I learned

- Extending Spring Security with a custom filter that enforces a second factor **after** the standard authentication, without fighting the framework's own filter chain.
- Coordinating two independent clients (a browser and a hardware device) around a server-issued, single-use session, and why binding the factor to the session matters for the security of the scheme.
- Profile-based configuration (H2 vs. PostgreSQL), seeding data with a `CommandLineRunner`, and deploying a Spring Boot app with an external database to Heroku.

## Notes and limitations

This is a learning project, not a production authentication system. A few things are deliberately simple or left as course-scope shortcuts: CSRF is disabled and the H2 console is exposed in the dev profile, the verification status is polled rather than pushed, and the server trusts that the Pi client is on a controlled network. They are the obvious next things to harden if the project were taken further.
