# CSA09 – Programming in Java – Assignment 2 Report

**Name:** ____________________  
**Register Number:** ____________________  
**Submission date:** ____________________  
**GitHub repository link:** ____________________

> **Evidence integrity:** This report provides the implementation, predictions, design analysis and expected complexity. Insert the actual output from your own execution in the evidence tables. Do not present illustrative or machine-independent numbers as measured results.

## Q1 – Building Text Under Load: String, StringBuilder or StringBuffer (6 marks)

### Prediction and design
`String` is immutable. Repeated `s = s + part` creates a new accumulated string on each iteration and copies the previous contents. As fields grow, this causes repeated copying and temporary objects. `StringBuilder` maintains a mutable character buffer and normally needs amortized linear work in the total output length. `StringBuffer` has similar append behaviour but synchronizes its methods, adding overhead.

Workloads implemented:
- A: one short ticket with 6 fields.
- B: 10,000 tickets with 50 fields each.
- C: 100 tickets with 500 fields each.

### Complexity and estimated allocation
Let `n` be the number of fields and assume each appended field contributes a bounded number of characters. Repeated concatenation copies prefixes of lengths proportional to 1, 2, ..., n, giving O(n²) character-copy work for a ticket. `StringBuilder` and `StringBuffer` append the total characters in O(n) amortized time. Repeated concatenation can create O(n) intermediate String objects per ticket and copy O(n²) total characters. A builder uses one mutable accumulator (possibly resized a few times) plus the final immutable String: O(n) space for the result/buffer.

### Measurements
Run `java StringWorkloadBenchmark` in Task01 and paste the three averages here:

| Workload | String + average ms | StringBuilder average ms | StringBuffer average ms |
|---|---:|---:|---:|
| A | ______ | ______ | ______ |
| B | ______ | ______ | ______ |
| C | ______ | ______ | ______ |

The program uses warm-up runs and five measured runs. JVM allocation profiling (for example Java Flight Recorder) is needed for exact allocation counts; source-level counts are estimates only.

### Prediction versus observation
**Your observation:** ______________________________________________  
**Explanation:** A tiny ticket may show negligible differences because fixed overhead and timer noise dominate. Larger workloads should expose repeated copying. Record whether the measurements support this prediction.

### Design decision
Use `StringBuilder` for each ticket because the ticket is local to one request/thread and does not need synchronized append. Use `StringBuffer` only if multiple threads truly share and mutate the same buffer and its synchronized methods are sufficient for the required atomicity. For a shared booking log, a dedicated thread-safe logging mechanism or synchronized/locked append operation is preferable; do not assume individual synchronized append calls make a multi-call log record atomic. Prefer immutable completed ticket strings when passing data between threads.

### Alternative: join / StringJoiner / format
Collect field strings and use `String.join(delimiter, fields)` or `StringJoiner` to build delimited text; these avoid repeated explicit concatenation in a loop and are suitable when fields are already available. `String.format` is readable for fixed layouts but formatting has more overhead and is not generally the fastest high-volume option. For a dynamically assembled ticket, `StringBuilder` offers direct control.

## Q2 – When Is ArrayList an Appropriate Engineering Choice? (4 marks)

### Experiment design
Task02 measures ArrayList and LinkedList at 1,000, 10,000 and 50,000 elements for end insertion, front/middle insertion, 1,000 indexed gets and full iteration. It also measures an ArrayDeque workload with frequent operations at both ends. Run `java PlaylistBenchmark` and retain the output.

### Complexity comparison

| Operation | ArrayList | LinkedList | ArrayDeque |
|---|---:|---:|---:|
| Add at end | O(1) amortized | O(1) | O(1) amortized |
| Insert at front | O(n) shifting | O(1) with iterator/head | O(1) |
| Insert in middle | O(n) search/shift | O(n) to reach node; O(1) link | Not supported by index; O(n) workaround |
| Get k-th | O(1) | O(n) | O(n) traversal |
| Iterate | O(n), cache-friendly | O(n), node traversal | O(n) |
| Space per element | array reference slot | node + two links + item reference | array slot |

ArrayList is appropriate when random access, iteration and appending dominate, and middle/front edits are occasional. Stop recommending it when frequent insertions/removals near the front or both ends dominate. For the revised “Up Next” queue, `ArrayDeque` is usually the better fit because it supports efficient operations at both ends. LinkedList can insert cheaply only after the target node/iterator is already found; locating a middle position remains linear.

A plain array requires manual shifting: indexed access O(1), append O(1) if spare capacity (otherwise resize), and front/middle insert/remove O(n). It has low per-element overhead and good locality, but resizing and shifting are manual.

### Results and operating boundary
Paste actual timing output: __________________________________________  
Based on the measured workload, define the playlist size/edit frequency at which your choice changes: __________________________.

## Q3 – Engineer the hashCode–equals Contract (6 marks)

### Three tests and predictions
1. Distinct IDs: expected size 2 because both IDs differ.
2. Repeated ID: expected size 2 because the second scan has the same identity key.
3. Same registration number but different display name: expected size 1 because identity is defined by `regNo`, not `name`.

The corrected `Student` class uses a final `regNo`, `equals` based on `regNo`, and `hashCode` based on the same field. The completed program prints `Unique students: 2`.

### Contract explanation
HashSet uses `hashCode()` to select a bucket and then `equals()` to distinguish matching keys within that bucket. If two objects are equal but have different hash codes, they may be placed in different buckets and the set may retain both. A hash collision is allowed: unequal objects can share a hash code; equals then distinguishes them. A field used by hashCode must not be mutated while the object is stored in a hash-based collection, because its new hash may point to a different bucket and lookup/removal can fail. Making identity fields final prevents this class of bug.

### Before/after TODO
With the supplied broken version (equals but no hashCode override), repeated objects can appear as duplicates because Object.hashCode is identity-based. Run a separate copy without the override to record your actual output, then run Task03 corrected version. The exact broken-version outcome is not guaranteed by the API for all inputs/JVMs; the contract violation is the defect.

### Alternatives
A `TreeSet<Student>` with a comparator on regNo performs add/search in O(log n), but comparator equality (compare returns zero) must match the intended identity. A `HashMap<String,Student>` keyed by regNo gives average O(1) lookup/insert and makes the unique key explicit. For n unique students, both structures store O(n) entries; TreeSet has tree-node overhead, HashMap has table/bucket overhead.

## Q4 – Choosing a Map for a Pharmacy Inventory (6 marks)

### Expected behaviour
- HashMap: put/get average O(1); iteration O(n), no guaranteed order.
- TreeMap: put/get O(log n); iteration O(n) in sorted key order.
- LinkedHashMap: put/get average O(1); iteration O(n) in insertion order, with linked-entry overhead.

The implementation creates three map types at 1,000, 10,000 and 50,000 medicine codes and times 100,000 lookups plus code-order and insertion-order report preparation. Run `java PharmacyMaps` and paste output.

| Medicines | Map | 100k lookups ms | Code report ms | Insertion report ms |
|---:|---|---:|---:|---:|
| 1,000 | HashMap | ____ | ____ | ____ |
| 1,000 | TreeMap | ____ | ____ | ____ |
| 1,000 | LinkedHashMap | ____ | ____ | ____ |
| 10,000 | HashMap | ____ | ____ | ____ |
| 10,000 | TreeMap | ____ | ____ | ____ |
| 10,000 | LinkedHashMap | ____ | ____ | ____ |
| 50,000 | HashMap | ____ | ____ | ____ |
| 50,000 | TreeMap | ____ | ____ | ____ |
| 50,000 | LinkedHashMap | ____ | ____ | ____ |

`TreeMap` naturally produces code order. `LinkedHashMap` naturally produces insertion order. A HashMap must be copied/sorted for code order and cannot reconstruct true insertion order. If both reports are frequent, maintain a primary HashMap plus explicit ordered views/indexes, or choose the map matching the dominant report and derive the other when needed.

### Deployment decision
Define acceptable lookup latency: ______ ms per lookup. Compare this with measured average per lookup (`100k lookup time / 100000`). A practical design may use HashMap for dominant stock lookups and a TreeMap/ordered report view for scheduled code-order reports. `Hashtable` is legacy synchronized API; modern code generally uses HashMap when external synchronization is unnecessary or ConcurrentHashMap for concurrent access.

### Sorted-array alternative
Binary search lookup is O(log n). Inserting into a sorted array is O(n) due to shifting. Producing the code-order report is O(n), and storage is O(n) with compact array slots. It may be useful for mostly static inventories with many reads, but frequent stock additions/removals make insertion costly.

## Q5 – Derive a Generic Ranking Component (7 marks)

### Design
The `Leaderboard<T>` stores a `List<T>` and receives a `Comparator<? super T>`. The comparator is external to the generic container, so the same leaderboard can rank teams, players, colleges or quiz teams using different rules. `Comparator<? super T>` accepts a comparator of T or a supertype of T. `Collection<? extends T>` is useful when adding a collection of subtype elements.

The ranking method copies the list and sorts the copy, leaving insertion order unchanged. Complexity is O(n log n) time and O(n) extra space for the copied list (excluding sorting implementation details).

### Comparator and correctness
Sort by points descending, goal difference descending, and name ascending. The comparator chain performs lexicographic comparison: the next key is considered only when the previous key ties. Each key comparator is transitive, and lexicographic composition preserves transitivity. `Integer.compare`-based comparator factories avoid overflow risk from subtraction.

Expected output:
`[Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]`

Bisons is above Panthers because points and goal difference tie, so alphabetical name order decides (`B` before `P`).

### Alternative approaches
If Team implements Comparable<Team>, the natural order is embedded in Team. That is suitable when there is one universal ranking, but less reusable when different competitions need different criteria. Three stable sorts (name ascending, then goal difference descending, then points descending) also produce the required lexicographic priority, but perform three O(n log n) sorts rather than one comparator-chain sort. The comparator chain is clearer and easier to inject for other ranked types.

## Q6 – Priority, Order and Fairness in a Queue (8 marks)

### Design and hand check
A FIFO queue alone cannot choose a more severe patient who arrived later. The PriorityQueue comparator orders severity ascending (1 is critical) and then arrival counter ascending. The counter is monotonically increasing, so equal-severity patients are served in arrival order.

Expected output:
`Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)`

Printing a PriorityQueue directly exposes its heap iteration order, not a fully sorted treatment sequence. Repeated `poll()` returns elements in priority order.

### Complexity and scaling
PriorityQueue insertion and removal are O(log n); peek is O(1); storage is O(n). A sorted LinkedList has O(n) insertion to find the position and O(1) removal at head. Five FIFO queues have O(1) admission and O(1) service if the next non-empty severity queue can be found with a small fixed scan (at most five checks); storage remains O(n).

For 5,000 arrivals/day, a heap is easily within a typical operational scale, but real deployment also needs persistence, concurrency control, audit trail, validation, clinical governance and monitoring. The code is an in-memory data-structure demonstration, not a production hospital system.

### Starvation and fairness
A continuous stream of severity 1–4 cases can indefinitely delay severity 5. One policy is bounded aging: after each configured waiting interval, improve a waiting patient's effective priority by one level, never above severity 1. Implement aging using periodic priority refresh/rebuild or a scheduler; account for the additional update cost and ensure clinical severity remains the primary safety rule. Aging must be approved by clinical policy rather than silently overriding triage decisions.

## Q7 – Diagnose, Redesign and Validate (13 marks)

### A. Diagnose the bottleneck
Selected limitation: Q1 repeated `+` concatenation inside the field loop. Each iteration copies the accumulated prefix into a new String. The total copied characters grow quadratically with field count, and intermediate Strings create allocation pressure.

### B. Objective
For identical ticket counts and field counts, reduce construction time and temporary allocations while preserving byte-for-byte ticket output. Measurable target: show lower average runtime than repeated concatenation on the larger workloads; report the measured percentage/speedup rather than assuming a result.

### C. Revised design
Replace the immutable accumulation loop with a local `StringBuilder`, optionally pre-sized from an estimated output length. Append each field and call `toString()` once per ticket. This exploits mutable capacity and amortized append. It removes repeated whole-prefix copies. Trade-offs: the builder owns a mutable buffer and capacity growth may copy the buffer occasionally; pre-sizing uses an estimate and may reserve excess memory.

Pseudocode:
```
for each ticket:
    builder = new StringBuilder(estimatedCapacity)
    for each field:
        builder.append(field)
    completedTicket = builder.toString()
```

### D. Prediction
For large field counts, StringBuilder should reduce runtime and intermediate allocation compared with repeated `+`. For tiny tickets, the difference may be negligible.

### E. Validation
Run `java RedesignBenchmark` in Task07. It uses the same workloads and seven measured runs for both implementations.

| Workload | Repeated + avg ms | StringBuilder avg ms | Speedup |
|---|---:|---:|---:|
| 1 ticket × 6 fields | ____ | ____ | ____ |
| 100 tickets × 50 fields | ____ | ____ | ____ |
| 1,000 tickets × 50 fields | ____ | ____ | ____ |

Paste raw output in `evidence/Q7.txt`. Ensure both versions generate equivalent output; the benchmark consumes the result length to prevent dead-code elimination.

### F. Final defence
**Did the prediction hold?** ____________________  
**Measured improvement:** ____________________  
**Trade-offs:** builder allocation and final conversion remain; a short ticket may not justify optimization.  
**When original is preferable:** simple concatenation is readable and acceptable for a small fixed number of operands or one-off short strings. For repeated accumulation in loops, StringBuilder is the defensible choice.

## Overall conclusion
Collection and string-class selection should follow the dominant workload and correctness requirements. ArrayList suits indexed playlists; ArrayDeque suits double-ended queues; HashSet requires the equals/hashCode contract; map choice depends on lookup and report-order needs; Comparator injection supports reusable rankings; PriorityQueue handles severity ordering with stable tie-breaking. The redesign decision must be validated using repeatable measurements on the student's own machine.
