# CSA09 – Programming in Java – Assignment 2

**Name:** ____________________  
**Register Number:** ____________________  
**GitHub repository:** `CSA09-<RegisterNumber>-Assignment2`  
**Java version used for development:** Java 17 (the programs use standard Java APIs)

## Important execution note
This project contains implementations, test data, benchmark harnesses and an analysis report. **Do not submit invented timing values.** Run each benchmark on your own computer, paste the generated CSV/results into `evidence/`, and update the report with your measurements. Timings vary with CPU, JVM warm-up, garbage collection and background load. The report marks machine-specific observations that must be completed by you.

## Compile and run
Open a terminal in the folder for a task. For example:

```bash
cd Task01
javac StringWorkloadBenchmark.java
java StringWorkloadBenchmark
```

Run each task:

| Folder | Compile | Run |
|---|---|---|
| Task01 | `javac StringWorkloadBenchmark.java` | `java StringWorkloadBenchmark` |
| Task02 | `javac PlaylistBenchmark.java` | `java PlaylistBenchmark` |
| Task03 | `javac Attendance.java` | `java Attendance` |
| Task04 | `javac PharmacyMaps.java` | `java PharmacyMaps` |
| Task05 | `javac LeaderboardDemo.java` | `java LeaderboardDemo` |
| Task06 | `javac Triage.java` | `java Triage` |
| Task07 | `javac RedesignBenchmark.java` | `java RedesignBenchmark` |

The benchmark programs print repeated-run averages and/or CSV-style results. Run them more than once and retain the raw output. The programs intentionally use moderate workload sizes so they can run on an ordinary laptop.

## Evidence checklist
- [ ] Fill in name, register number and submission date.
- [ ] Execute every program and capture terminal screenshots.
- [ ] Save raw benchmark output under `evidence/` (one file per task).
- [ ] Replace the clearly marked machine-specific blanks in `REPORT.md`.
- [ ] Create the required GitHub repository and push the folders progressively (at least one commit per task).
- [ ] Add the course faculty as collaborator if the repository is private.
- [ ] Paste the working GitHub URL into the report and submission cover page.

## Suggested progressive Git workflow
```bash
git init
git add README.md Task01
git commit -m "Add Task 1 string construction experiments"
git add Task02
git commit -m "Add Task 2 playlist collection experiments"
# Repeat for Task03 through Task07, then push to GitHub.
```

All code is original teaching code written for this assignment brief. Check that you understand it and can explain it in your viva before submission.
