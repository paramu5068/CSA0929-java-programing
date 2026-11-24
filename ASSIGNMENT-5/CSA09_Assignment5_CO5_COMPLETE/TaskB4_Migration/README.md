# Task B4 – Migration and Final Engineering Decision

Run `schema.sql` from B3 first. Compile both B3's `PassLoader.java` and the B4 classes with Connector/J on the classpath.

From this directory, with the B3 directory also on the classpath:
```cmd
javac -cp ".;%MYSQL_JAR%;..\TaskB3_JDBC_Batch" PassMigration.java DatabaseInspector.java ..\TaskB3_JDBC_Batch\PassLoader.java
java -cp ".;%MYSQL_JAR%;..\TaskB3_JDBC_Batch" PassMigration
java -cp ".;%MYSQL_JAR%" DatabaseInspector
```

`passes.csv` includes one intentionally malformed row so the migration prints a rejection message. The valid rows are loaded using `PassLoader` in one transaction. The production recommendation is a relational database for 12 campuses, concurrent counters, indexed searches, constraints, transactions, backups and access control. Text files are useful for export/import and small local stores; Java serialization is not a suitable shared multi-campus system of record.
