# Task 6 database setup

1. Install MySQL Server and MySQL Connector/J.
2. Run `sql/schema.sql` in MySQL Workbench or the MySQL command line.
3. Set environment variables in Command Prompt (replace credentials):
   ```bat
   set DB_URL=jdbc:mysql://localhost:3306/campus_events
   set DB_USER=root
   set DB_PASSWORD=your_mysql_password
   ```
4. Compile from this folder, adjusting the Connector/J jar path:
   ```bat
   javac -cp ".;C:\path\mysql-connector-j.jar" *.java
   ```
5. Run:
   ```bat
   java -cp ".;C:\path\mysql-connector-j.jar" EventRegistrationApp
   ```

The duplicate rule is enforced by the database unique key on `(student_id, event_id)`. The UI catches and reports the database error. Do not put real database passwords into GitHub.
