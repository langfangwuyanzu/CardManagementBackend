# Yolngu Card Backend (Spring Boot)

Email verification service for the Card Registration app.

## Endpoints

- `POST /api/auth/email/send-code` with JSON `{ "email": "user@example.com" }`
- `POST /api/auth/email/verify` with JSON `{ "email": "user@example.com", "code": "123456" }`
  - Returns `{ "verified": true, "token": "..." }` on success.

## Quick Start (Local)

1. **Start Redis and MailHog**:
   ```bash
   docker compose up -d
   # MailHog UI: http://localhost:8025
   ```

2. **Run Backend**
   ```bash
   ./mvnw spring-boot:run
   # or: mvn spring-boot:run
   ```

3. **Test with curl**
   ```bash
   curl -X POST http://localhost:8080/api/auth/email/send-code \
     -H "Content-Type: application/json" \
     -d '{"email":"you@demo.com"}'

   # check MailHog at http://localhost:8025 to get the code

   curl -X POST http://localhost:8080/api/auth/email/verify \
     -H "Content-Type: application/json" \
     -d '{"email":"you@demo.com","code":"123456"}'
   ```

## Real SMTP (Optional)

Edit `src/main/resources/application.yml`:
```yaml
spring:
  mail:
    host: email-smtp.ap-southeast-2.amazonaws.com
    port: 587
    username: YOUR_SMTP_USERNAME
    password: YOUR_SMTP_PASSWORD
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
```

## CORS

Allowed origins:
- http://localhost:5173
- http://localhost:3000

Edit `CorsConfig.java` if you need more.

## Build Jar

```bash
mvn -DskipTests package
java -jar target/yolngu-card-backend-0.0.1-SNAPSHOT.jar
```
# CardManagementBackend
