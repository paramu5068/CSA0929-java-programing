@echo off
cd /d "%~dp0TaskB1_FileStore"
javac PassStore.java BufferedLineCounter.java PassStoreBenchmark.java
if errorlevel 1 exit /b 1
java PassStore
java BufferedLineCounter
java PassStoreBenchmark
cd /d "%~dp0TaskB2_Adversarial"
javac LoginSafetyDemo.java
if errorlevel 1 exit /b 1
java LoginSafetyDemo
pause
