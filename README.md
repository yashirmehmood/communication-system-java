# Player Communication System (Java)

A flexible Java system demonstrating communication between two players,
either:

-   **In the same JVM process** (method calls routing messages through
    an in-memory broker)
-   **In separate JVM processes** (using socket-based communication)

The project includes a robust architecture with Players, Factories,
Routers, Communication Handlers, and extensive input utilities.

------------------------------------------------------------------------

## 🚀 Features

### ✅ Same-Process Mode

-   Two Player instances communicate inside the same JVM
-   Messages sent automatically **or manually**
-   Simple in-memory router
-   No sockets involved

### ✅ Separate-Process Mode

-   Two JVMs communicate over sockets
-   Initiator chooses:
    -   Automatic sending of 10 messages
    -   Manual sending of 10 messages
-   Responder automatically echoes messages with a counter
-   Retry logic ensures connection stability
-   Safe cleanup of resources

### 🎯 General Architecture

-   `Player` → Represents a communicating entity\
-   `Message` → Data model for messages\
-   `PlayerMessageRouter` → Routes messages between players\
-   `PlayerFactory` → Creates player instances\
-   `SameProcessCommunicationHandler` → Handles in-JVM communication\
-   `SeparateProcessCommunicationHandler` → Handles inter-process
    communication\
-   `InputUtils` → Centralized user input handling\
-   `Main` → User interface for mode selection

------------------------------------------------------------------------

## 📂 Project Structure

    src/
     ├── main/
     │   └── java/com/example/playercomm/
     │       ├── core/
     │       ├── handler/
     │       ├── transport/
     │       ├── util/
     │       └── Main.java
     └── test/
         └── java/com/example/playercomm/
             ├── core/
             ├── handler/
             ├── factory/
             └── util/

------------------------------------------------------------------------

## 🧪 Unit Tests

The project includes JUnit 5 tests covering:

-   Player behavior\
-   Router registration and routing\
-   Factory instance selection\
-   Communication flow (simplified)\
-   Input utilities

------------------------------------------------------------------------

## ▶️ How to Run the Application

### **1. Build the project**

``` bash
mvn clean install
```

### **2. Run the application**

``` bash
mvn exec:java -Dexec.mainClass="com.example.playercomm.Main"
```

### **3. Follow the on-screen instructions**

You can run: - **Same-process communication** - **Separate-process
communication (requires 2 terminals)**

------------------------------------------------------------------------

## 💬 Running Separate-Process Mode

### **Terminal 1 (Responder)**

``` bash
mvn exec:java -Dexec.mainClass="com.example.playercomm.Main"
```

Select:

    2 → Separate Process Mode
    r → Responder

### **Terminal 2 (Initiator)**

``` bash
mvn exec:java -Dexec.mainClass="com.example.playercomm.Main"
```

Select:

    2 → Separate Process Mode
    i → Initiator

------------------------------------------------------------------------

## 🛠️ Requirements

-   Java 21+
-   Maven 3.8+
-   JUnit 5

------------------------------------------------------------------------

## 📘 License

This project is provided for educational and interview preparation
purposes.

------------------------------------------------------------------------

## 🙌 Author

Developed by **Yashar Mahmood Lashkar**.
