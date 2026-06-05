# TapTable

TapTable is a web application for the management of a dining developed for the Web Applications course (A.Y. 2025/2026) provided by the University of Padua.

It allows customers to browse the menu and place orders online, while giving managers and staff dedicated tools to manage dishes, ingredients and promotions.

## Tech Stack

| Layer      | Technology                     |
| ---------- | ------------------------------ |
| Runtime    | Java 25, Jakarta EE (Servlets) |
| Build      | Maven (WAR packaging)          |
| Web server | Apache Tomcat                  |
| Database   | PostgreSQL                     |
| Container  | Docker, Docker Compose         |
| DB admin   | pgAdmin                        |

## How to Run

1. **Build the WAR**

   ```bash
   mvn clean package
   ```

2. **Create the secrets file** (first time only)

   ```bash
   echo "your_password" > secrets/postgres_password.txt
   ```

3. **Start all services**

   ```bash
   docker compose up
   ```

The composer brings up the following containers:

| Service       | Endpoint               |
| ------------- | ---------------------- |
| Apache Tomcat | http://localhost:8080/ |
| PostgreSQL    | localhost:5432         |
| pgAdmin       | http://localhost:5050  |

The database is initialized automatically from `db/CREATE.sql` and populated with initial data from `db/DUMP.sql`.

## Group

| Name             | Student ID |
| ---------------- | ---------- |
| Baldan Fabio     | 2203580    |
| Garberino Alvise | 2196387    |
| Merja Klaudio    | 2197815    |
| Padoan Giancarlo | 2188345    |
| Sanavia Thomas   | 2197484    |

---

## License

All the contents of this repository are shared under the [Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/).

![CC logo](https://i.creativecommons.org/l/by-sa/4.0/88x31.png)
