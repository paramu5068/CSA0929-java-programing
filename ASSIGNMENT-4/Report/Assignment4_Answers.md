# Assignment 4 – CO4: Event-Driven GUI Design
## Written report

**Name:** B. Anusha Bhaskar  
**Register number:** ENTER YOUR REGISTER NUMBER  
**Course:** Programming in Java (CSA09)  
**Assignment:** 4 – CO4

---

# Task 1 – GUI Design Review

## 1.1 Diagnose the design

1. **UI and business logic are mixed:** database connection, validation and payment logic are inside `actionPerformed`. This makes the window difficult to test and change.
2. **String comparison uses `==`:** Java compares object references, not string contents. The Pay branch may not execute. Use `"Pay".equals(e.getActionCommand())` or separate listeners.
3. **No input validation:** empty registration number, non-numeric amount, zero or negative amount can cause exceptions or invalid payments.
4. **Database work blocks the event-dispatch thread:** connection and SQL operations may freeze the window while the user waits.
5. **`Thread.sleep(3000)` freezes the GUI:** repainting and user interaction stop for three seconds. A background worker should perform slow work.
6. **SQL is concatenated:** user input can alter the SQL statement. A `PreparedStatement` with parameters is safer.
7. **No useful user feedback:** `System.out.println` is not visible as a clear result in the form. Show a status label or dialog.
8. **Receipt button is not implemented:** it falls into the `else` branch and clears the fields instead of showing a receipt.
9. **Clear is confused with every non-Pay action:** Receipt and Clear both execute the same `else` branch.
10. **No exception handling or connection cleanup:** checked exceptions are not handled, and database resources may remain open.
11. **No window-close handling:** the application may not close cleanly or release resources.

## 1.2 Repaired class structure

- `FeeWindow`: collects input, calls the service and displays results.
- `FeeService`: validates the registration number and amount, then carries out payment logic.
- A real application would use a `FeeRepository` for JDBC access. The supplied runnable example uses an in-memory demonstration service so that the GUI can be tested without a database.

Event handling: Pay, Clear and Receipt each have a separate listener. Payment runs in a `SwingWorker` so the event-dispatch thread remains responsive.

## 1.3 Design defence (under 100 words)

The redesigned program separates presentation from payment rules. The window can be tested by checking its inputs and displayed messages, while `FeeService` can be tested without opening a GUI. Changes to payment validation do not require rewriting the screen. Separate listeners make each button's purpose clear and prevent the Receipt action from accidentally behaving like Clear. Background execution keeps the interface responsive during slow operations. This separation reduces duplication and makes defects easier to locate.

## Alternative concept and complexity

With one listener and an if–else chain, dispatch checks up to k action commands, so worst-case dispatch selection is O(k), with O(1) extra space for the listener object. With one listener object per button, the event is routed directly to the matching listener, giving O(1) dispatch selection after the event source is identified, but O(k) listener objects and O(k) total listener storage. A Swing `Action` can be shared by multiple controls and centralises enabled state, name and behaviour.

---

# Task 2 – Requirement to Event-Driven Design

## 2.1 Event table

| Event source | Event object | Listener interface | Action |
|---|---|---|---|
| Patient ID text field | `ActionEvent` | `ActionListener` | Optional lookup / move focus |
| Department combo box | `ItemEvent` | `ItemListener` | Refresh doctor list |
| Doctor combo box | `ItemEvent` | `ItemListener` | Update selected doctor |
| Time-slot combo box | `ItemEvent` | `ItemListener` | Update selected slot |
| Book button | `ActionEvent` | `ActionListener` | Validate and book |
| Window close control | `WindowEvent` | `WindowListener` / `WindowAdapter` | Confirm close if booking is in progress |

## 2.2 Component hierarchy

```text
JFrame AppointmentDesk
└── JPanel root (BorderLayout)
    ├── JPanel heading (NORTH)
    │   └── JLabel "Outpatient Appointment Desk"
    ├── JPanel form (CENTER, GridLayout)
    │   ├── JLabel + JTextField patientId
    │   ├── JLabel + JComboBox department
    │   ├── JLabel + JComboBox doctor
    │   └── JLabel + JComboBox timeSlot
    └── JPanel actions (SOUTH, FlowLayout)
        ├── JButton Book Appointment
        └── JLabel status
```

## 2.3 Handler pseudocode

### (a) Refresh doctor list

```text
ALGORITHM RefreshDoctors
INPUT selectedDepartment
BEGIN
    CLEAR doctorComboBox
    SET doctors TO doctorMap[selectedDepartment]
    FOR EACH doctor IN doctors
        ADD doctor.name TO doctorComboBox
    END FOR
    IF doctorComboBox has items THEN
        SELECT first doctor
    END IF
END
```

### (b) Validate and book

```text
ALGORITHM BookAppointment
BEGIN
    READ patientId, department, doctor, timeSlot
    IF patientId is empty THEN
        DISPLAY "Enter patient ID"
        RETURN
    END IF
    IF department, doctor or timeSlot is not selected THEN
        DISPLAY "Select department, doctor and time slot"
        RETURN
    END IF
    SET bookingInProgress TO TRUE
    CALL appointmentService.book(patientId, department, doctor, timeSlot)
    IF booking succeeds THEN
        DISPLAY confirmation number
    ELSE
        DISPLAY booking error
    END IF
    SET bookingInProgress TO FALSE
END
```

## 2.4 Event sequence and applet comparison

1. `main` schedules GUI creation using `SwingUtilities.invokeLater`.
2. The frame constructor creates components and registers listeners.
3. `setVisible(true)` displays the frame.
4. User actions generate `ActionEvent`, `ItemEvent` or `WindowEvent`.
5. The registered listener method executes on the event-dispatch thread.
6. On close, `windowClosing` is called. If booking is in progress, ask for confirmation; otherwise dispose the frame.
7. `windowClosed` may be notified after disposal.

An applet has a container-managed lifecycle: `init()` initialises once, `start()` begins or resumes, `paint()` draws, `stop()` pauses and `destroy()` releases resources. A desktop JFrame does not use this applet lifecycle; it uses construction, visibility, event callbacks and window events.

## Alternative concept and complexity

If doctors are fetched from the database after each department change, each change incurs a database round trip plus query and result-transfer time, approximately O(q + r) where q is query/connection latency and r is the number of doctors returned. Loading once into `Map<String,List<Doctor>>` costs O(d + m) time and O(d + m) memory initially, then a department change takes O(r) to populate the combo box without a database round trip. Here d is the number of departments and m is the total number of doctors. The map uses more memory but gives faster repeated changes.

---

# Task 3 – From Sketch to Layout

## 3.1 Layout manager choices

- Main frame: `BorderLayout`. It gives distinct NORTH, CENTER and SOUTH regions and allows the centre to grow.
- Search row: `FlowLayout(LEFT)`. Controls retain preferred sizes and remain arranged in a row.
- Bottom button row: `FlowLayout(CENTER)`. Buttons retain preferred sizes.
- Results area: `BorderLayout` containing a `JScrollPane` around a `JTable`. The scroll pane expands with the centre.
- A `GridLayout` is not appropriate for the whole screen because it gives equal-sized cells. `CardLayout` is useful for switching whole screens, not for this single search layout.

## 3.2 Nested structure

```text
JFrame LibrarySearchScreen (BorderLayout)
├── JPanel titlePanel (NORTH, FlowLayout)
│   └── JLabel title
├── JPanel searchPanel (NORTH, FlowLayout)
│   ├── JTextField query
│   ├── JComboBox category
│   └── JButton Search
├── JScrollPane resultsScrollPane (CENTER)
│   └── JTable resultsTable
└── JPanel footer (SOUTH, BorderLayout)
    ├── JPanel navigation (CENTER, FlowLayout)
    │   ├── JButton Prev
    │   ├── JButton Next
    │   └── JButton Reserve
    └── JLabel status (SOUTH)
```

## 3.3 Implementation

See `Task03_LibraryLayout/LibrarySearchScreen.java`. It uses nested `BorderLayout` and `FlowLayout` panels. The table is inside a `JScrollPane`, so the results region receives the extra space.

## 3.4 Resizing test

Run the program and capture three genuine screenshots:
1. Small window: confirm controls remain visible and the table area becomes smaller.
2. Normal window: confirm the expected proportions.
3. Maximised window: confirm the results table expands while the search and navigation controls keep their preferred sizes.

Do not use fabricated screenshots. Save your screenshots under `Task03_LibraryLayout/screenshots/` and add a short note naming the tested window size.

## Alternative concept and complexity

`GridBagLayout` can also resize selected rows and columns, but needs more constraint code. Absolute positioning with `setLayout(null)` and `setBounds` requires manually recalculating every component position; controls can overlap or become clipped when resized or when font/DPI changes. Nested layouts require a few more container objects but adapt automatically. Layout computation is generally proportional to the number of components for these simple layouts; exact runtime depends on component hierarchy and constraints. Absolute positioning may do fewer layout calculations but has poor resize behaviour. Space for nested panels is O(c) for c components and containers; GridBagLayout also stores O(c) constraints; absolute positioning stores bounds O(c).

---

# Task 4 – Event-Handling Forensics

## 4.1 Defect

The method is written `mouseclicked`, but the listener interface method is `mouseClicked` with a capital C. Java is case-sensitive, so the method does not override the callback and the count remains zero. The label is also never updated. In addition, drawing directly with `getGraphics()` is temporary and is not preserved through repainting.

## 4.2 Failing interaction

Click to draw one or more dots, minimise the window, then restore it. The operating system may repaint the window. Because the dots were drawn directly onto the current screen graphics and were not stored, they disappear.

## 4.3 Repair

The corrected program stores each click as a `Point`, updates the label, calls `repaint()`, and draws all stored points in `paint(Graphics)`. This allows the dots to be reconstructed after repainting.

## 4.4 Prevention

`@Override` asks the compiler to verify that the method overrides a superclass or interface method. If `mouseclicked` is used, compilation fails with an error similar to: `method does not override or implement a method from a supertype`. This catches the capitalisation mistake before execution.

## Alternative concept and complexity

An off-screen `BufferedImage` can be created once and used as a drawing surface. On each click, draw the new dot onto the image and call `repaint()`. `paint()` then copies the image to the screen. The point-list design redraws n points per repaint, O(n), and stores O(n) points. The image design copies approximately width × height pixels per repaint (O(W×H)), and stores O(W×H) pixels. The image approach can make repaint cost independent of click count, but consumes more memory and requires careful image initialisation and resizing.

---

# Task 5 – Same Screen, Different Event-Handling Styles

## 5.1 Four implementations

Separate runnable source files are provided:
- `ConverterFrameListener.java` – frame implements `ActionListener`.
- `ConverterNamedInner.java` – named inner listener class.
- `ConverterAnonymous.java` – anonymous inner listener.
- `Converter.java` – lambda version, including the completed TODOs.

All versions use `WindowAdapter` for window closing.

## 5.2 Requirement change

The Convert button and Enter key can invoke the same conversion operation. Each implementation can register an action listener on the text field as well as the button. The dialog is shown with `JOptionPane.showMessageDialog`. The amount parsing is placed in one `convert()` method to avoid duplicated conversion logic.

Approximate code change:
- Frame implements `ActionListener`: add one registration line for Enter and update the shared handler to show a dialog.
- Named inner class: add one registration line using the same handler instance and update its method.
- Anonymous inner class: add a second listener or extract a shared method; otherwise handler code may be duplicated.
- Lambda: reuse the same lambda variable for both controls, or use a method reference; one shared operation avoids duplication.

## 5.3 Comparison

| Style | Readability | Access to frame fields | Reuse | Ease of change |
|---|---|---|---|---|
| Frame implements `ActionListener` | Simple for tiny screens; can become crowded | Direct | Handler tied to frame | Moderate |
| Named inner class | Clear name and separation | Direct | Can be reused inside the outer class | Good |
| Anonymous inner class | Good for one short local handler | Direct | Limited; anonymous object is less convenient to reuse | Moderate |
| Lambda | Concise for a functional interface | Directly captures enclosing fields | Can be stored in a variable and shared | Very good for short actions |

For a large application, I would use a separate controller/action class or a shared Swing `Action`, because the GUI and event behaviour remain modular and reusable.

## Alternative concept and complexity

A method reference such as `this::convert` creates a concise listener that delegates to the existing method. A shared `Action` can be attached to a button, menu item and keyboard binding, so one behaviour serves multiple triggers. Reusing one listener/action reduces duplicate handler objects and keeps the action logic in one place. Adding a trigger becomes one registration/binding operation. Four independent inline listeners may create multiple objects and require repeating or forwarding code. Actual object allocation may be optimised by the compiler/runtime, so the design comparison is more important than assuming a precise byte count.

---

# Task 6 – Campus Event Registration Desktop Application

## Stage 1 – Users, screens, data and rules

**Users**
- Student: submits a registration.
- Committee member: views, filters and exports registrations.

**Screens**
- Registration screen.
- Registration management screen with table, event filter and export menu.
- Confirmation/error dialogs.

**Data**
- Student ID, student name, email, event ID, event name, registration timestamp.

**Testable requirements**
1. The student can submit a registration with all required fields.
2. Empty student ID, name, email or event selection is rejected with a clear message.
3. The same student cannot register for the same event twice.
4. The committee can view registrations in a table.
5. Selecting an event filters the table.
6. The user can export the displayed registration list to CSV.
7. Database work runs in a `SwingWorker`, not on the event-dispatch thread.
8. The window remains usable while registrations are loading.

## Stage 2 – Screen and event flow

```text
Main JFrame
├── JMenuBar
│   ├── File
│   │   ├── Refresh
│   │   └── Exit
│   └── Registrations
│       └── Export CSV
├── Registration form panel
│   ├── Student ID / Name / Email
│   ├── Event selector
│   └── Register button
└── Management panel
    ├── Event filter
    ├── Refresh button
    └── JTable in JScrollPane

Register → validate → service → JDBC insert → confirmation/error dialog
Filter → load matching page → update table model
Export → choose file → write CSV
Close → confirm if background operation is active
```

## Stage 3 – Critical decisions

- The form performs basic required-field checks before calling the service.
- The service enforces business rules and calls the repository.
- A database unique constraint on `(student_id, event_id)` is the final protection against duplicates, including simultaneous attempts.
- JDBC work is performed in `SwingWorker.doInBackground()`. UI updates happen in `done()` on the event-dispatch thread.
- Errors are shown in a dialog and a status label.

## Stage 4 – Layered class design

```text
EventRegistrationApp (JFrame / presentation)
        |
        v
RegistrationService (validation and business rules)
        |
        v
RegistrationRepository (JDBC / data access)
        |
        v
MySQL event_registrations table
```

### Register pseudocode

```text
ALGORITHM Register
BEGIN
    READ studentId, name, email, eventId
    IF any required field is empty THEN
        DISPLAY validation message
        RETURN
    END IF
    DISABLE Register button
    START background worker
        CALL service.register(studentId, name, email, eventId)
        SERVICE validates input and inserts using PreparedStatement
        DATABASE unique constraint rejects duplicate student/event pair
    WHEN worker completes
        ENABLE Register button
        DISPLAY success or clear error
        REFRESH registration table
END
```

### Filter pseudocode

```text
ALGORITHM FilterRegistrations
INPUT eventId, pageSize, pageNumber
BEGIN
    START background worker
        offset = pageNumber * pageSize
        rows = repository.findPage(eventId, pageSize, offset)
    WHEN worker completes
        REPLACE table model rows with rows
        UPDATE status with number of rows
END
```

## Stage 5 – Implementation and demonstration

See `Task06_EventRegistration/`. It includes:
- Swing form and table,
- menu bar,
- service and JDBC repository layers,
- duplicate protection through a database unique key,
- background loading with `SwingWorker`,
- paginated filtering with SQL `LIMIT` and `OFFSET`,
- CSV export,
- SQL schema in `sql/schema.sql`.

The application requires a configured MySQL database and Connector/J. Run it locally and capture genuine screenshots for valid, invalid, duplicate, filter and export cases.

## Alternative concept and complexity

Loading all 100,000 rows at once transfers and stores every row. Time is O(N) plus database/network overhead, and client memory is O(N × average row size). The event-dispatch thread must not perform this work; if it does, the window freezes until loading completes. Pagination with page size p loads O(p) rows per request and uses O(p) table memory for the current page. Total work to browse every row remains O(N), but each interaction is bounded to one page and the GUI remains responsive when loading occurs in `SwingWorker`. Pagination also reduces initial network transfer and memory pressure. SQL `LIMIT/OFFSET` can become slower for very large offsets; keyset pagination is a possible later refinement.
