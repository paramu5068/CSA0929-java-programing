# CSA09 – Programming in Java – Assignment 5 (CO5)
## Data Persistence Engineering – Files and Databases

**Student:** B. Anusha Bhaskar  
**Register number:** Replace with your correct register number before submission  
**Java version used for preparation:** Java 27 (the programs use standard Java APIs and should also work on Java 17+)  
**Course:** CSA09 – Programming in Java  
**Outcome:** CO5

> Important: This package contains completed design answers and runnable Java implementations. Database tasks require a locally installed MySQL server and MySQL Connector/J. No benchmark values or screenshots have been fabricated. Run the measurement programs on your own computer and save the actual results in `Evidence/`.

## Folder guide

- `PartA/` – A1 to A5 design answers, pseudocode, trade-offs and complexity analysis.
- `TaskB1_FileStore/` – bus-pass text store, damaged-line handling, buffered/unbuffered line counter and growth benchmark.
- `TaskB2_Adversarial/` – unsafe vs parameterized login demonstration and malformed-file tests.
- `TaskB3_JDBC_Batch/` – SQL schema, JDBC loader and benchmark harness for Statement, PreparedStatement and batch.
- `TaskB4_Migration/` – file-to-database migration and DatabaseMetaData inspection.
- `Evidence/` – blank measurement template and screenshot checklist.

## Windows CMD: compile and run

Open Command Prompt in the extracted `CSA09_Assignment5_CO5_COMPLETE` folder.

### B1 file store and I/O benchmark

```cmd
cd TaskB1_FileStore
javac PassStore.java BufferedLineCounter.java PassStoreBenchmark.java
java PassStore
java BufferedLineCounter
java PassStoreBenchmark
```

### B2 SQL injection demonstration

```cmd
cd ..\TaskB2_Adversarial
javac LoginSafetyDemo.java
java LoginSafetyDemo
```

The SQL demonstration is a safe, local logic simulation of how string concatenation changes the query condition. The actual JDBC comparison is in `LoginJdbcDemo.java` and requires MySQL Connector/J plus the local test database. Do not use real credentials.

### B3 and B4 database tasks

1. Install MySQL Community Server.
2. Download MySQL Connector/J from the official MySQL website.
3. Create the database using `TaskB3_JDBC_Batch\schema.sql`.
4. Set environment variables in CMD (replace values with your local test account):

```cmd
set DB_URL=jdbc:mysql://localhost:3306/co5_transport
set DB_USER=root
set DB_PASSWORD=your_local_test_password
```

5. Put the Connector/J JAR path in the classpath. Example:

```cmd
set MYSQL_JAR=C:\path\to\mysql-connector-j-9.x.x.jar
```

Compile B3:

```cmd
cd TaskB3_JDBC_Batch
javac -cp ".;%MYSQL_JAR%" PassLoader.java JdbcInsertBenchmark.java
java -cp ".;%MYSQL_JAR%" JdbcInsertBenchmark
```

Compile B4:

```cmd
cd ..\TaskB4_Migration
javac -cp ".;%MYSQL_JAR%" PassMigration.java DatabaseInspector.java
java -cp ".;%MYSQL_JAR%;..\TaskB3_JDBC_Batch" PassMigration
java -cp ".;%MYSQL_JAR%" DatabaseInspector
```

`PassMigration` expects `passes.csv` in its current working directory. A small sample file is included in `TaskB4_Migration/`.

## Important implementation notes

- `PassStore` stores five comma-separated fields. This teaching implementation rejects malformed records and documents that commas in names must be quoted/escaped or a robust CSV library must be used. For the assignment's sample records, names do not contain commas.
- The code never treats the included sample password as a real password.
- JDBC examples use `PreparedStatement` for values, explicit transactions for batch loading, rollback on failure, and restore the original auto-commit setting.
- Performance timings depend on the computer, disk, JVM, MySQL configuration and warm-up. Record your actual measurements; do not copy fabricated numbers.
- The code's test output is illustrative only where the operating system, database or timing affects results.

## GitHub submission workflow

Extract this ZIP, then copy the folder `CSA09_Assignment5_CO5_COMPLETE` into your existing local Git repository as `ASSIGNMENT-5`.

From the root of your existing repository:

```cmd
git add ASSIGNMENT-5
git commit -m "Add Java Assignment 5 CO5"
git push origin main
```

For progressive task commits, stage and commit each task folder separately rather than making one final commit. If the course checks progressive history, create task-wise commits while you work.

Before submission:
- Replace the register-number placeholder.
- Execute every Java program.
- Run the JDBC programs against your local test database.
- Add screenshots and actual measurement CSVs under `Evidence/`.
- Confirm the GitHub link and share it with faculty.
