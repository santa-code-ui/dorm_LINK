# dorm_LINK — Installation & Deployment Guide

Campus Oriented Hostel Manager · HTML/CSS/JS frontend · Java (Jakarta Servlets) backend · MySQL.

## 1. Prerequisites
- **XAMPP** (MySQL + phpMyAdmin)  — or any MySQL 8 server
- **JDK 17+**
- **Apache Tomcat 10.1+**  (Jakarta EE 9+, because the code uses the `jakarta.servlet` namespace)
- **mysql-connector-j** JAR (the MySQL JDBC driver)

## 2. Create the database
Start MySQL in XAMPP, open phpMyAdmin (or the `mysql` CLI) and run, in order:
```sql
SOURCE database/schema.sql;
SOURCE database/seed_data.sql;   -- demo accounts + sample data
```
This creates the `dorm_link` database and all tables/triggers.

## 3. Configure the connection
Edit `backend/config/db.properties` if your MySQL user/password/port differ:
```
db.url=jdbc:mysql://localhost:3306/dorm_link?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=
app.baseUrl=http://localhost:8080/dormlink
```

## 4. Build the backend
The PDF folder layout keeps Java sources in flat packages under `backend/`
(`config, models, utils, dao, services, controllers`) and `api/` holds the route specs.
Compile into a standard web app:
```bash
# from the project root
mkdir -p build/WEB-INF/classes
javac -encoding UTF-8 -cp "TOMCAT/lib/servlet-api.jar:libs/mysql-connector-j.jar" \
      -d build/WEB-INF/classes \
      backend/config/*.java backend/models/*.java backend/utils/*.java \
      backend/dao/*.java backend/services/*.java backend/controllers/*.java
cp backend/config/db.properties build/WEB-INF/classes/
cp libs/mysql-connector-j.jar    build/WEB-INF/lib/
```
> Servlets are mapped with `@WebServlet` annotations, so no `web.xml` is required.

## 5. Assemble & deploy the web app
Lay the web app out so the static site sits at the context root and the API at `/api`:
```
dormlink/                 ->  Tomcat webapps/dormlink/
├── frontend/             (copy the whole frontend/ folder here)
├── uploads/              (copy uploads/ — student photos, receipts, hostel images)
└── WEB-INF/
    ├── classes/          (compiled backend + db.properties)
    └── lib/              (mysql-connector-j.jar)
```
Drop `dormlink/` into Tomcat's `webapps/`, start Tomcat, then open:
```
http://localhost:8080/dormlink/frontend/html/auth/signin.html
```
The frontend calls the API at `http://localhost:8080/dormlink/api/...` (configurable via
`window.DORMLINK_API`).

## 6. Demo logins (from seed_data.sql)
| Role    | Identifier        | Password     |
|---------|-------------------|--------------|
| Admin   | `admin`           | `admin123`   |
| Admin   | `warden`          | `warden123`  |
| Student | `USTM2024CS001`   | `student123` |

## 7. Preview without a backend
Every page degrades gracefully: if the API is unreachable, the UI shows a
"Preview mode" banner and renders sample data. You can therefore open the HTML files
directly in a browser to review the interface before deploying Tomcat/MySQL.

## Notes
- Email OTP is generated server-side and logged (see `utils/OtpUtil#send`). Connect a real
  SMTP/transactional-email provider there to actually deliver codes. A persistent
  `password_reset_otp` table is also provided if you prefer DB-backed OTPs.
- QR images: `utils/QrUtil` builds the URL each gate sticker should encode; generate the PNG
  with any QR library and place it in `frontend/assets/qr/`.
