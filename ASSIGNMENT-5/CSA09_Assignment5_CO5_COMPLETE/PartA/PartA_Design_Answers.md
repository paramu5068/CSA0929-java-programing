# PART A – Persistence Design & Pseudocode (25 Marks)

**Student:** B. Anusha Bhaskar  
**Register number:** Replace with your correct register number

## A1 – Design a File Format (7 marks)

### A1.1 Record representations

**Delimited text format (UTF-8):**

Field order:
`passId,regNo,name,routeNo,validityDate,amountPaid`

Example:
`P1001,192311001,Arun,12,2026-12-31,4500.00`

Use one record per line. To support commas, quotes or newlines in a name, use a defined CSV quoting convention: enclose a field containing comma/quote/newline in double quotes and double every embedded quote. A simple `split(",")` parser is not sufficient for general CSV. The implementation in B1 intentionally uses a simple five-field teaching format and rejects malformed rows; a production system should use a CSV parser or an escaped length-prefixed format.

**Binary format using `DataOutputStream`:**

For each record, write fields in this exact order:
1. `writeUTF(passId)`
2. `writeUTF(regNo)`
3. `writeUTF(name)`
4. `writeInt(routeNo)`
5. `writeUTF(validityDate)` (ISO `yyyy-MM-dd`)
6. `writeDouble(amountPaid)`

`writeUTF` writes a length-prefixed modified-UTF representation, so string lengths need not be fixed. Numeric fields use fixed-width binary representations. The reader must read fields in precisely the same order and detect `EOFException` as an incomplete final record.

### A1.2 Operations – standard pseudocode

**Algorithm ADD_PASS(pass):**
1. OPEN a `BufferedWriter` over a `FileWriter` in append mode.
2. VALIDATE required fields and numeric values.
3. ENCODE the record using the agreed delimiter/escaping rule.
4. WRITE the encoded record and a newline.
5. FLUSH and CLOSE the writer using try-with-resources.
6. REPORT success.

**Algorithm FIND_PASS(passId):**
1. IF the file does not exist, RETURN NOT_FOUND (or report missing store).
2. OPEN a `BufferedReader` over a `FileReader`.
3. WHILE a line can be read:
   - PARSE the line.
   - IF the line is damaged or has an invalid field count/type, record or skip the damaged line.
   - ELSE IF the parsed pass ID equals `passId`, RETURN the record.
4. CLOSE the reader.
5. RETURN NOT_FOUND.

**Algorithm LIST_ROUTE(route):**
1. IF the file does not exist, RETURN an empty result and report that no store exists.
2. OPEN a `BufferedReader` over a `FileReader`.
3. WHILE a line can be read:
   - PARSE and validate the record.
   - SKIP damaged records and log their line numbers.
   - IF route number equals the requested route, ADD record to result.
4. CLOSE reader and RETURN result.

`FileReader`/`FileWriter` provide character-oriented text access. `BufferedReader`/`BufferedWriter` reduce repeated low-level reads/writes and provide efficient line handling. `FileInputStream`/`FileOutputStream` are byte-oriented and are appropriate for binary data or explicit byte encoding; for text, use `InputStreamReader`/`OutputStreamWriter` with UTF-8 when explicit charset control is required.

### A1.3 Damaged data

- **Damaged line:** validate field count, date, route and amount. Skip the record, record line number and reason in an error log; do not stop the complete scan.
- **Missing file:** distinguish `Files.notExists(path)` from an empty file. For a find/list operation, report “store not created” or return an empty result according to the application contract. For add, create the file and parent directory if permitted.
- **Power failure/incomplete write:** write a complete record to a temporary file and atomically replace the old file for full-file rewrites. For append-only records, flush and optionally call `FileChannel.force(true)`; include a checksum or record length/version marker and ignore a truncated final record during recovery. A database transaction is preferable where atomic multi-record updates are required.

### Alternative: fixed-length RandomAccessFile

If every record has a fixed size `R`, record `i` begins at byte offset `i × R`; `seek(i * R)` reads it in O(1) once the record index is known. However, searching by pass ID without an index still requires scanning O(n) records. A separate ID-to-record-number index (for example, a hash index) is needed for expected O(1) ID lookup. Fixed-width strings must be padded or truncated, wasting space when values are shorter than their allocated fields. A variable-length name complicates fixed-size records.

## A2 – Don’t Read What You Don’t Need (5 marks)

### A2.1 Predict the cost

A 50 MiB file contains 52,428,800 bytes. A loop calling `FileInputStream.read()` once per byte makes approximately 52.4 million Java read calls. The JVM/OS may cache file data, so not every call necessarily causes a physical disk operation, but the per-call overhead, bounds checks and repeated native transitions make this approach inefficient.

### A2.2 Buffered redesign

Use `BufferedInputStream` with a buffer (for example, 8 KiB or 64 KiB) and read chunks. The program below counts newline bytes in chunks. With a 64 KiB buffer, approximately 800 successful chunk reads are needed for 50 MiB rather than 52.4 million single-byte reads. Complexity remains O(N) time and O(B) extra memory, where N is file size and B is buffer size.

### A2.3 When buffering gives little benefit

Buffering may offer little benefit when the data is already in memory, when each read is very large and already chunked, or when the bottleneck is computation/network latency rather than small file reads. It can also add an unnecessary copy for a one-time tiny file.

### Alternative: Files.readAllLines

`Files.readAllLines()` reads all lines and stores them in a List<String>. Time is O(N), but memory is O(N) plus per-line object overhead. A streaming buffered reader is O(B) auxiliary memory and is preferable for a 50 MiB or unbounded log. `readAllLines()` is convenient for small, bounded files when the complete contents are needed at once.

## A3 – Discover the Data-Access Design Pattern (5 marks)

### A3.1 Repeated pattern

Repeated steps: obtain connection, create SQL, bind values, execute, iterate `ResultSet`, map rows to objects, handle SQL exceptions, close resources. Differences: table name, columns, key type, entity mapping and SQL statements.

### A3.2 Generic DAO

```java
public interface Dao<T, K> {
    void insert(T entity) throws SQLException;
    Optional<T> findById(K id) throws SQLException;
    List<T> findAll() throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(K id) throws SQLException;
}
```

`StudentDao implements Dao<Student, String>` and owns SQL and row mapping for the student table. The service layer calls the interface and does not know SQL details. Use try-with-resources for `PreparedStatement` and `ResultSet`.

### A3.3 Student mapping

| Java field | SQL column | Notes |
|---|---|---|
| `registerNo` | `reg_no` | Primary key |
| `name` | `name` | VARCHAR |
| `email` | `email` | VARCHAR |
| `department` | `department` | VARCHAR |

### Alternative: SQL in service/GUI

If SQL is embedded in every service or GUI, a renamed column may require edits in multiple classes and tests. With DAO, the change is normally isolated to the DAO mapping and SQL. Both designs have the same asymptotic database-operation cost because the database executes equivalent SQL; DAO adds a small constant abstraction/mapping overhead and does not change O(time) or O(space) for the query itself. DAO improves maintainability and testability.

## A4 – What Should Be Serialized? (4 marks)

### A4.1 Persistable fields

Serialize the `Hostel`, its serializable `Room` list and serializable `Resident` list as one reachable object graph. Mark the live JDBC `Connection` `transient`: it is an external resource, not durable application state, and generally is not serializable. Passwords should not be serialized in plaintext; preferably persist a salted password hash in a secure database. If a password field remains in the class, it should be `transient` and reloaded/verified through a secure authentication service. Other nonessential caches or runtime handles should also be transient.

When the root `Hostel` is serialized, Java traverses references to reachable serializable objects, preserving object identity and shared references. Every non-transient reachable object must implement `Serializable`, otherwise `NotSerializableException` occurs.

### A4.2 Version change

When a new `phoneNumber` field is added, an older serialized stream has no value for it; Java initializes the missing field to its default (`null` for String). An explicit stable `serialVersionUID` allows compatible class evolution when the changes are compatible. If the UID differs, deserialization throws `InvalidClassException`. Custom `readObject` or migration logic can assign defaults and validate older data.

### Alternative: CSV/JSON

Text formats are generally more portable, inspectable and easier to migrate, but may be larger and require parsing. Serialization can be faster/smaller for a Java-only object graph but couples the stored representation to Java class definitions and is unsafe for untrusted input. Both save and load O(n) resident data; actual constants and file sizes depend on fields and encoding. JSON with optional fields can handle added fields more gracefully; CSV needs a schema/header or migration rule. Never deserialize untrusted data.

## A5 – Design for Correctness and Safety (4 marks)

### A5.1 JDBC transaction pseudocode

1. GET connection.
2. SAVE original auto-commit value.
3. SET auto-commit FALSE.
4. TRY:
   - UPDATE hostel_wallet SET balance = balance - 500 WHERE wallet_id = ? AND balance >= 500.
   - IF affected rows != 1, THROW insufficient-funds error.
   - UPDATE canteen_wallet SET balance = balance + 500 WHERE wallet_id = ?.
   - IF affected rows != 1, THROW update error.
   - COMMIT.
5. CATCH any exception:
   - ROLLBACK.
   - REPORT transfer failure.
6. FINALLY restore original auto-commit value.

The two updates are atomic as one transaction: both persist or neither does.

### A5.2 SQL injection and safe query

With concatenation, input such as an ID containing `' OR '1'='1' -- ` can change the WHERE expression so the password condition is bypassed (exact behaviour depends on SQL dialect and comment syntax). The query is parsed as SQL, not treated as literal data.

Use:

```java
String sql = "SELECT * FROM users WHERE id = ? AND pwd_hash = ?";
try (PreparedStatement ps = con.prepareStatement(sql)) {
    ps.setString(1, id);
    ps.setString(2, passwordHash);
    try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
    }
}
```

Store salted password hashes, not plaintext passwords. PreparedStatement binds values separately from SQL syntax, preventing user input from becoming SQL code.

### Alternative: auto-commit updates

If the debit commits and the application fails before the credit, ₹500 disappears from the two-wallet system. If credit happens first and debit fails, ₹500 is created. Separate auto-commit statements each have their own transaction; one explicit transaction protects correctness. A transaction has some logging/coordination overhead, but asymptotic time per pair of updates remains O(1) with indexed wallet IDs. Correctness risk is substantially higher without atomicity.
