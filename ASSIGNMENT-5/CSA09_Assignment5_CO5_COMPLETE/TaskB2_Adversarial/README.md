# Task B2 – Adversarial Inputs

Compile/run the offline demonstration:
```cmd
javac LoginSafetyDemo.java
java LoginSafetyDemo
```

Optional JDBC test requires MySQL Connector/J and the environment variables in the root README:
```cmd
javac -cp ".;%MYSQL_JAR%" LoginJdbcDemo.java
java -cp ".;%MYSQL_JAR%" LoginJdbcDemo
```

The test creates and drops a disposable `login_demo` table. It uses fake sample data only. The crafted ID illustrates why concatenation is unsafe; PreparedStatement binds the entire input as a literal value. `damaged_passes.csv` includes wrong field count, non-numeric route, and an empty line. The B1 parser skips these rows. Add your own test-run screenshots and explain the observed output.

Escaping quotes or regex validation is weaker than parameter binding: it is easy to miss encoding, comment, whitespace, or database-specific syntax cases. Validation is still useful for business rules, but it is not a substitute for PreparedStatement. Validation is O(k) in input length; parameter binding is also linear in input length at most, but keeps values separate from SQL syntax.
