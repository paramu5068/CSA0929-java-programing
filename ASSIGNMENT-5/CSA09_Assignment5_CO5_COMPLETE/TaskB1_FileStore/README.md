# Task B1 – Instrument and Implement the File Store

Compile:
```cmd
javac PassStore.java BufferedLineCounter.java PassStoreBenchmark.java
```
Run:
```cmd
java PassStore
java BufferedLineCounter
java PassStoreBenchmark
```

`PassStore.find()` reads sequentially with `BufferedReader`, validates exactly five fields, skips malformed lines, and returns the matching record or `null`. The damaged `P1002,DAMAGED LINE` row has only two fields, so it is ignored and does not stop the search. The program then finds P1003.

`BufferedLineCounter` compares byte-at-a-time and 64 KiB buffered reads. The benchmark's timings are machine-dependent; record your own output. `PassStoreBenchmark` measures a scan for the last record at increasing file sizes. The expected trend for a linear scan is O(n).
