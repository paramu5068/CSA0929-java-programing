# Programming in Java – Assignment 3 (CO3)

**Repository name:** `CSA09-<RegisterNumber>-Assignment3`  
**Student A:** `<Name> – <Register Number>`  
**Student B:** `<Name> – <Register Number>`  
**Java version:** Java 17 or later (the programs use standard Java APIs).

## Contents
- `Q1_ATM` – exception hierarchy, validation, multi-catch and finally
- `Q2_Exceptions` – user-defined checked exceptions, propagation and chaining
- `Q3_Resources` – manual resource management vs try-with-resources
- `Q4_Threads` – Thread/Runnable, lifecycle, join and ExecutorService comparison
- `Q5_Synchronization` – race condition and synchronized booking
- `Q6_ProducerConsumer` – bounded buffer with wait/notifyAll and deadlock prevention
- `Q7_ConcurrentProcessor` – fault-tolerant ExecutorService/Callable/Future processor
- `REPORT.md` – joint report, individual responsibilities, analysis and conclusions
- `evidence/` – test/measurement templates. Replace or supplement with screenshots from your own runs.

## Compile and run
Open Command Prompt/PowerShell in the relevant question folder. Example:
```bash
javac ATM.java
java ATM
```
For each folder, use the source filename shown in that folder. Q7:
```bash
javac ResultProcessor.java
java ResultProcessor <path-to-input-folder> 4
```
If no input folder is supplied, Q7 creates a small demonstration dataset in a temporary folder.

## GitHub submission
1. Create a separate repository for each student named `CSA09-<RegisterNumber>-Assignment3`.
2. Put this folder's contents in the repository root (or rename this folder to the required repository name).
3. Update the student details in this README and in `REPORT.md`.
4. Execute every program individually on both students' computers.
5. Save genuine console screenshots in the corresponding `evidence` folder.
6. Commit progressively: one meaningful commit per question, plus README/report updates.
7. Add the faculty as collaborator if the repository is private and submit both repository links.

**Important:** The report includes expected/illustrative behaviour and measurement procedures. Do not present the included sample timing values or expected-output text as measurements from your own computer. Record actual measurements and attach genuine screenshots/logs before submission.
