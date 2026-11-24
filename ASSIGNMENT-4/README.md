# SIMATS Engineering – Programming in Java (CSA09)
## Assignment 4 – CO4: Event-Driven GUI Design

**Student name:** B. Anusha Bhaskar  
**Register number:** ENTER YOUR REGISTER NUMBER  
**Java version:** Java 27 (update if your installed version differs)  
**GitHub repository:** Create `CSA09-<RegisterNumber>-Assignment4`

## Project structure
- `Task01_FeeWindow/` – service/UI separation and event dispatch
- `Task02_AppointmentDesk/` – event model, component hierarchy and handler pseudocode
- `Task03_LibraryLayout/` – resizable AWT library search screen
- `Task04_DrawPad/` – mouse events, repaint-safe drawing and completed TODOs
- `Task05_Converter/` – four listener styles and completed lambda TODO program
- `Task06_EventRegistration/` – layered Swing/JDBC event-registration application
- `Report/` – written answers, pseudocode, comparisons and test plan
- `sql/` – MySQL schema for Task 6

## Compile and run
Open Command Prompt in the assignment root. Example for Task 4:

```bat
cd Task04_DrawPad
javac DrawPad.java
java DrawPad
```

Task 3:
```bat
cd Task03_LibraryLayout
javac LibrarySearchScreen.java
java LibrarySearchScreen
```

Task 5:
```bat
cd Task05_Converter
javac Converter.java
java Converter
```

Task 1:
```bat
cd Task01_FeeWindow
javac *.java
java FeeWindow
```
Task 1 uses a demonstration service (no database dependency) so the GUI can be tested independently. The `FeeService` class is the boundary where a real JDBC repository can be connected.

Task 6 requires MySQL and MySQL Connector/J:
1. Run `sql/schema.sql` in MySQL.
2. Set environment variables `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. Add MySQL Connector/J to the classpath. Example (adjust jar path):
```bat
javac -cp ".;C:\path\mysql-connector-j.jar" *.java
java -cp ".;C:\path\mysql-connector-j.jar" EventRegistrationApp
```

## Important execution/evidence instructions
The Java source and written design answers are supplied. You must run each GUI on your own computer and capture genuine screenshots. Do not submit screenshots from another machine or claim a database test passed unless you actually ran it. Add screenshots to each task's `screenshots/` folder and explain which requirement each screenshot proves.

For Task 6, test:
- valid registration,
- missing/invalid input,
- duplicate registration for the same student and event,
- event filter,
- export CSV,
- large data loading / page navigation.

For Task 3, capture the window at three sizes (small, normal, maximised). For Task 4, capture before and after minimising/restoring. For Task 5, capture a valid conversion and invalid amount.

## GitHub submission
Create the repository named `CSA09-<RegisterNumber>-Assignment4`. Commit progressively, at least one commit per task. Example:
```bat
git init
git add Task01_FeeWindow
git commit -m "Complete Task 1 fee window design"
```
Repeat `git add`, `git commit` for Tasks 2–6, Report and SQL. Then add your GitHub remote and push:
```bat
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/CSA09-REGISTER-Assignment4.git
git push -u origin main
```
Replace the sample URL with your actual repository URL.
