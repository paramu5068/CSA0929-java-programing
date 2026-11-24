# Task B3 – Statement vs PreparedStatement vs Batch

Run `schema.sql` in MySQL Workbench or the MySQL command-line client first. Set `DB_URL`, `DB_USER`, `DB_PASSWORD`, and `MYSQL_JAR` as described in the root README.

Compile:
```cmd
javac -cp ".;%MYSQL_JAR%" PassLoader.java JdbcInsertBenchmark.java
```
Run:
```cmd
java -cp ".;%MYSQL_JAR%" JdbcInsertBenchmark
```

The benchmark deletes the table before each approach and uses distinct primary-key prefixes. It compares 10, 1,000, 10,000 and 100,000 rows. The batch approach commits all rows as one transaction in chunks of 1,000. Timings vary; preserve the console output and record it in the evidence CSV.

To test rollback, modify a generated record to contain a non-numeric route or negative/invalid data enforced by the DB, call `PassLoader.loadAll`, catch the exception, and query `SELECT COUNT(*) FROM bus_pass`; the transaction should leave no rows inserted by that call. Use a fresh/empty table for an unambiguous test.
