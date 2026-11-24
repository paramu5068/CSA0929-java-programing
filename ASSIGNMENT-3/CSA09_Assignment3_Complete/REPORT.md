# Programming in Java – Assignment 3 (CO3)
## Joint Report

**Student A:** `<Name / Register Number>`  
**Student B:** `<Name / Register Number>`  
**Course:** Programming in Java  
**Repository links:** `<Student A GitHub URL>` / `<Student B GitHub URL>`  
**Java version used:** `<Record java -version output>`  
**Submission date:** `<Date>`

> Replace every angle-bracket placeholder. Both students must independently compile and execute every question. Attach actual console screenshots and local benchmark results before submission.

## Q1 – Handling Failure in a Bank ATM Simulation (6 marks)

### Implementation
`Q1_ATM/ATM.java` validates the account and amount, uses a checked `CardReaderException`, unchecked `IllegalArgumentException` and `InsufficientFundsException`, handles malformed numeric input with a multi-catch, and always prints the session-close message in `finally`.

### Student A – exception design
Relevant hierarchy:
```text
Object
 └─ Throwable
    ├─ Error
    └─ Exception
       ├─ IOException (checked)
       ├─ CardReaderException (checked)
       └─ RuntimeException
          ├─ NumberFormatException (unchecked)
          ├─ IllegalArgumentException (unchecked)
          └─ InsufficientFundsException (unchecked)
```
| Failure point | Exception | Checked? | Catch/handling |
|---|---|---|---|
| Non-numeric withdrawal amount | NumberFormatException | No | Numeric-input multi-catch |
| Card/account cannot be read | CardReaderException | Yes | Card error catch |
| Amount is zero/negative | IllegalArgumentException | No | Invalid amount catch |
| Amount exceeds balance | InsufficientFundsException | No | Transaction-declined catch |
| Missing card-reader file in a file-backed implementation | FileNotFoundException | Yes | Catch IOException/FileNotFoundException at I/O boundary |
| JVM/resource exhaustion | OutOfMemoryError or other Error | Error, not an ordinary application exception | Normally not caught as a transaction failure |

Catch blocks should proceed from specific to general. A broad `catch (Exception)` before a narrower catch makes the narrower catch unreachable and causes a compile-time error. `finally` executes on normal completion and ordinary exception propagation, making it suitable for cleanup; it is not an absolute guarantee against JVM termination, process kill or power failure.

### Student B – tests and tracing
Use the five Q1 cases in `evidence/test-tables.md`. For an uncaught exception, temporarily remove the matching catch and enter a negative amount. The stack trace's first line names the exception and message; subsequent `at` lines show method, class and source line locations, followed by caller frames. Restore the catch after collecting evidence.

### Joint answer
Catching only `Exception` can be acceptable at a top-level application boundary for logging and presenting a generic failure message, provided the program still distinguishes recoverable conditions and does not hide programming defects. A single broad catch loses precise recovery behaviour, user-friendly messages and diagnostic classification. It should not be placed before more specific catches.

### Alternative and complexity
With pre-validation (`Scanner.hasNextDouble()`/`hasNextInt()`), invalid tokens can be rejected before conversion and methods can return status codes. This needs explicit checks at each input boundary and can make control flow more verbose. Both approaches are O(1) time and O(1) auxiliary space for a single withdrawal. Returning an error code avoids exception construction and stack-trace capture on failure; throwing an exception has higher constant time and memory overhead because the runtime records a stack trace. The asymptotic complexity remains O(1).

## Q2 – User-Defined Exceptions and Propagation (7 marks)

### Implementation and TODO 1
`Q2_Exceptions/StorageDemo.java` completes the TODO with:
```java
throw new SubmissionStorageException("Could not store " + fileName, e);
```
Expected output begins:
```text
Saved answer01.pdf
Error: Could not store answer02.pdf
Cause: Disk quota exceeded
```
A stack trace follows because the demonstration explicitly calls `printStackTrace`.

### Student A – hierarchy and propagation
Class hierarchy:
```text
Exception
 └─ ExamException (checked)
    └─ SubmissionStorageException (checked)
```
The storage failure is checked because saving is an expected operation that callers should handle. The controller/service can declare `throws ExamException` or the specific subtype and the controller boundary can translate it into a safe message. Propagation:
`Storage.save()` → service method → controller handler → student-facing message.
The storage layer reports a domain-level `SubmissionStorageException`; the low-level IOException remains available as its cause.

### Student B – scenarios and chaining
| Scenario | Exception | Expected handling |
|---|---|---|
| Exam window closed | ExamWindowClosedException (checked) | Controller rejects late submission |
| Student not registered | StudentNotRegisteredException (checked) | Controller/service rejects request |
| File exceeds limit | SubmissionTooLargeException (checked) | Service rejects before storage |
| Disk quota/I/O failure | SubmissionStorageException (checked, wraps IOException) | Controller reports storage failure |

If the cause is not passed to the constructor, `getCause()` returns `null`; the Cause line in the supplied demonstration would throw a NullPointerException when it calls `.getMessage()`. The original low-level message and stack trace are lost from the wrapper. Exception chaining preserves the causal relationship and diagnostic evidence.

### Joint conclusion
A domain-specific storage exception separates internal file-system details from the user interface, supports consistent handling and avoids exposing raw infrastructure messages. The cause should still be retained for logs and debugging.

### Checked vs unchecked and complexity
If these exceptions extended `RuntimeException`, callers would not be forced by the compiler to catch or declare them; `throws` declarations could be omitted, although documenting them may still help. Checked exceptions make handling obligations explicit. Stack-trace creation is proportional to the captured call-stack depth, O(d) time and O(d) memory for depth d. A chained wrapper adds another exception object and its stack trace, while retaining a reference to the cause; the cause's own trace remains in memory. In the worst case, a chain of k exceptions with depth d_i uses O(sum d_i + k) trace/object storage.

## Q3 – Resource Safety, Debugging and Robust Code (7 marks)

### Implementation
`Q3_Resources/ResourceDemo.java` writes the same report using manual `finally` closure and try-with-resources. It opens one report writer and one input reader at a time. The reader is closed at the end of each iteration; the writer is closed after the report is complete.

### Student A – debugging and experiment
Use a missing/unreadable store file after one valid store file to force a mid-run failure. Set a breakpoint on each open and close operation and step through the `finally` path. Record the number of open handles before and after failure and attach debugger screenshots. Add a log line before opening each file to identify the failing path. Do not claim a file-handle count without measuring it on the local runtime.

### Student B – correctness
A manual implementation can leak if an exception occurs before `close()` is reached or if cleanup is not placed in a `finally` block. Try-with-resources closes every successfully initialized resource automatically, in reverse declaration order. If the main operation throws and close also throws, the close failure is recorded as a suppressed exception and can be retrieved with `getSuppressed()`.

### Joint decision
Try-with-resources does not make the whole report transaction atomic. The report can still be incomplete if an input is missing, corrupted, or the disk fills up. A robust design should validate inputs, write to a temporary output, handle per-file failures, and atomically move the completed report into place where supported.

### Complexity
For f files containing N total characters/bytes, reading and writing is O(N) time. Processing one file at a time uses O(1) additional file handles beyond the output writer: at most two handles (one writer and one reader) in this implementation. Proper manual finally closure has the same asymptotic bound, but is more error-prone. No closing can accumulate O(f) open input handles if all are opened together and can exhaust the process limit. Memory for the report content is streamed rather than loaded all at once.

## Q4 – Thread Creation and Life Cycle (7 marks)

### Implementation
`Q4_Threads/ThreadDemo.java` extends `Thread` for four scanner stations, also demonstrates `Runnable`, `start()`, `sleep()`, `join()` and `isAlive()` concepts, and includes a fixed-pool `ExecutorService` example.

### Student A – design
Extending `Thread` combines task and thread object and prevents extending another class. Implementing `Runnable` separates the task from the execution mechanism and is generally more flexible. Lifecycle:
`NEW → RUNNABLE ↔ (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED`.
Calling `run()` directly is an ordinary method call on the current thread; `start()` asks the JVM to schedule a new thread. `join()` blocks the main thread until the target station terminates, so the final total is printed after all stations finish.

### Student B – experiments
Run the program ten times and record finish order. Repeat after changing priorities and after making one station daemon. Thread priority is only a scheduling hint; it does not guarantee finish order. A daemon thread does not keep the JVM alive, so it may stop when all user threads finish. Include actual observations and screenshots in the evidence folder.

### Joint conclusion
Scheduling, operating-system load, timing and runtime decisions make completion order nondeterministic. Correct programs must use synchronization and `join()`/futures for dependencies, not assume a particular output order.

### Executor alternative and complexity
A fixed pool submits one `Callable`/`Runnable` per batch and reuses p worker threads. For n roughly equal independent script tasks, idealized elapsed work is approximately O(n/p) task-time, with scheduling and I/O overhead. Creating one thread per station is manageable for four stations, but a pool controls concurrency and avoids repeated thread creation. Thread-stack memory is approximately O(p × stack-size) for a pool versus O(n × stack-size) if one thread is created per script. Actual elapsed time must be measured; I/O contention can limit speedup.

## Q5 – Race Conditions in Cinema Booking (6 marks)

### Implementation and TODO
`Q5_Synchronization/SeatBooking.java` completes the operation with a `synchronized` method. The expected result is `Seats sold: 10`.

### Student A – race explanation
Without synchronization, two threads can both read `booked[seat] == false` before either writes `true`. Both then increment `bookingsMade`, producing duplicate sales/counting. The critical section is the check, state update and counter increment as one indivisible operation. Use a barrier/latch to make a race easier to reproduce; a simple unsynchronized run may not fail every time.

### Student B – strategy comparison
A synchronized method locks the entire `SeatBooking` instance. A synchronized block can lock only the critical section using the same object (or a dedicated lock). Both prevent concurrent entry for that lock. Static synchronization locks the `Class` object and is useful only when the protected state is static/shared across all instances. Measure 10,000 operations with `System.nanoTime()`; include actual elapsed values in the evidence table.

### Joint decision
For a 20-screen system, use a separate lock/state object per screen or per seat/seat-group to reduce unnecessary contention, while ensuring the check-and-book operation remains atomic. A database uniqueness constraint/transaction is also required in a distributed multi-server cinema system; JVM synchronization protects only one process.

### Atomic alternatives and complexity
`AtomicBoolean.compareAndSet(false, true)` gives each seat an atomic one-time transition. `ReentrantLock` provides explicit lock/unlock in `try/finally`. `ConcurrentHashMap.putIfAbsent(seat, marker)` atomically claims a seat. Each attempt is O(1) expected time; under heavy contention, threads may retry or wait, increasing latency. All approaches use O(s) state for s seats; atomic booleans or map entries add per-seat object/reference overhead. The synchronized boolean array uses O(s) space and simple monitor locking.

## Q6 – Deadlock and Inter-Thread Communication (7 marks)

### Implementation and TODOs
`Q6_ProducerConsumer/TokenCounter.java` completes both TODOs using `while`, `wait()` and `notifyAll()`. The expected sum is 210. `TransferDemo.java` demonstrates deadlock prevention using a consistent wallet lock order.

### Student A – producer/consumer
`wait()` must be called while owning the object's monitor; it releases that monitor while waiting and reacquires it before returning. Use `while`, not `if`, because wakeups can be spurious and another thread may consume/produce before the awakened thread reacquires the lock. `notify()` wakes one arbitrary waiter; `notifyAll()` wakes all, which then re-check the condition. `sleep()` pauses without releasing an owned monitor; `wait()` releases the monitor. The circular buffer count remains in [0,5].

### Student B – deadlock
The four Coffman conditions are mutual exclusion, hold-and-wait, no preemption and circular wait. If thread 1 locks A then B while thread 2 locks B then A, circular wait is possible. Acquiring locks in ascending wallet ID order removes circular wait, preventing this deadlock pattern.

### Joint conclusion
Both problems require coordination over shared state. Producer/consumer uses monitor waiting to ensure the buffer condition is valid before proceeding; ordered locking ensures transfers acquire resources in a consistent order and do not form a cycle. Progress still depends on threads not being permanently blocked by unrelated failures.

### Alternative and complexity
`ArrayBlockingQueue<Integer>(5)` directly provides a bounded, thread-safe buffer; `put()` and `take()` block as required. It is less error-prone than manually implementing wait/notify. A `ReentrantLock.tryLock(timeout)` can prevent indefinite waiting by allowing a transfer to abort/retry if both locks cannot be acquired. Queue put/take are O(1) per item, with O(5) buffer space. A transfer is O(1) work plus lock-wait/retry overhead.

## Q7 – Concurrent, Fault-Tolerant Result Processor (10 marks)

### Stage 1 – requirements (joint)
- Independent score files can be processed concurrently.
- Each task reads one file, validates a score in [0,100], computes a grade and returns an outcome.
- Missing, unreadable, empty, malformed and out-of-range files must be counted as failures; one failure must not stop other tasks.
- At completion, successes + failures must equal the number of submitted files.
- The result summary must be deterministic in counts even though task completion order is not.

### Stage 2 – design (Student A)
`ResultProcessor.java` uses `ExecutorService`, `Callable<Outcome>` and `Future<Outcome>`. Each callable isolates file-level exceptions and returns a failure outcome. A `ConcurrentLinkedQueue<Outcome>` stores shared results safely. The main thread retrieves every future and adds an unexpected task failure as a failure outcome. It then shuts down the pool and prints the summary.

Class outline:
```text
ResultProcessor
 ├─ ScoreTask implements Callable<Outcome>
 ├─ Outcome (file, success, message)
 ├─ ExecutorService (fixed pool)
 ├─ Future<Outcome> collection
 └─ ConcurrentLinkedQueue<Outcome>
```
Pseudocode:
```text
list input files
create fixed pool(p)
for each file: submit ScoreTask(file), retain Future
for each Future:
    try get result and add to concurrent result collection
    catch task failure and add failure outcome
shutdown pool and await termination
count successful and failed outcomes
print each outcome and final summary
```

### Stage 3 – implementation and measurement (Student B)
The supplied implementation creates a small demo folder when no arguments are provided. To test missing files, corrupt files and large datasets, prepare an input folder with known cases and pass it as the first argument. Run with pool sizes 1, 2, 4 and 8. Record elapsed time using `System.nanoTime()` around submission and collection. Verify totals against a manually counted input set. Include raw data and screenshots; do not substitute invented timings.

### Stage 4 – defend the design (joint)
Choose the pool size that gives the best measured throughput without excessive memory or I/O contention. More threads may not help when disk bandwidth is the bottleneck, when tasks are very small, or when context switching increases. Each file produces exactly one outcome, so counting the queue after all futures complete gives a complete summary. One limitation is that all outcome objects are retained in memory; for very large workloads, results could be streamed to a durable summary store.

### Alternative and complexity
Parallel streams are concise but use the common ForkJoinPool by default and provide less direct control over pool sizing and per-task recovery. One thread per file creates up to 10,000 stacks and risks resource exhaustion; a bounded executor limits worker threads to p. For total input size N, useful processing work is O(N); idealized time is O(N/p) for balanced tasks, but disk I/O and scheduling affect real performance. Pool stack memory is O(p × stack-size), while one-thread-per-file stack memory is O(n × stack-size). A failed file should be handled inside its task or while collecting its Future; otherwise an unchecked failure may surface through `Future.get()` as `ExecutionException`. The supplied task converts expected per-file failures into individual outcomes.

## Individual Contribution Record

| Question | Student A – primary responsibility | Student B – primary responsibility | Joint work |
|---|---|---|---|
| Q1 | Throwable hierarchy, failure table, catch order | Test design, catch tracing, stack trace | ATM implementation and conclusion |
| Q2 | Exception hierarchy and propagation | Scenarios and exception chaining evidence | Complete TODO and conclusion |
| Q3 | Debugging and experimental analysis | Resource inventory and correctness | Both implementations |
| Q4 | Thread design and lifecycle | Ten-run and priority/daemon experiments | Thread implementations and conclusion |
| Q5 | Race reproduction and interleaving | Synchronization comparison and timing | Booking implementation and decision |
| Q6 | Producer-consumer design | Deadlock analysis/prevention | Both implementations and conclusion |
| Q7 | Architecture and pseudocode | Implementation and measurements | Requirements and design defence |

**Declaration:** Both students must independently execute and understand all seven questions. The primary-responsibility column identifies the deeper evidence each student prepares; it does not mean the other student skips that question.
