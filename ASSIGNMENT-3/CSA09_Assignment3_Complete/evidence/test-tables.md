# Evidence and measurement sheets

Capture genuine screenshots of the console after running each program. Save them in the relevant Q folder as `run.png` or `run.jpg`.

## Q1 – ATM test cases
| Test | Input | Expected path | Actual output / screenshot |
|---|---|---|---|
| Valid withdrawal | account `12345`, amount `500` | Success | Fill after execution |
| Non-numeric amount | `abc` | NumberFormatException catch | Fill |
| Negative amount | `-100` | IllegalArgumentException catch | Fill |
| More than balance | `6000` | InsufficientFundsException catch | Fill |
| Blank account | blank | CardReaderException catch | Fill |

## Q4 – finish order (run at least 10 times)
| Run | Finish order | Priorities used | Notes |
|---|---|---|---|
| 1–10 | Record actual order | Normal / changed | |

## Q5 – timing
| Version | Attempts | Elapsed time (ms) | Duplicate bookings |
|---|---:|---:|---:|
| Unsynchronized | 100 threads | Measure locally | |
| synchronized method | 100 threads | Measure locally | 0 expected |
| synchronized block | 100 threads | Measure locally | 0 expected |

## Q7 – thread-pool benchmark
Create a directory with a hand-checked mix of valid, corrupt and missing files. For each pool size, run:
`java ResultProcessor <directory> <threads>`
| Pool size | Total files | Successes | Failures | Elapsed ms |
|---:|---:|---:|---:|---:|
| 1 | | | | |
| 2 | | | | |
| 4 | | | | |
| 8 | | | | |

Do not invent measurements. Use `System.nanoTime()` around the processing section or a stopwatch and record the actual machine/Java version.
