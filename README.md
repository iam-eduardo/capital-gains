# Capital Gains Tax Calculator

This project implements a capital gains tax calculator based on the business rules described in the Nubank backend challenge. It reads a sequence of operations from standard input (stdin) and outputs the tax to be paid for each one.

---

## 🧮 Technical and Architectural Decisions

- **Clean Architecture Principles:** The codebase is divided into `domain` (business logic), `application` (DTOs/services), and `infrastructure` (parsing and CLI).
- **Pure business logic:** The core calculation is encapsulated in a pure, stateless use case (`CalculateCapitalGainsTax`).
- **Referential transparency:** All calculations are isolated and deterministic.
- **DTO boundaries:** External representations are handled in the application layer to decouple business logic from I/O concerns.
- **Minimal dependencies:** Only Jackson is used for JSON parsing, and JUnit for testing.

---

## 🧰 Frameworks and Libraries Used

- **Jackson (com.fasterxml.jackson):** For serializing/deserializing JSON input and output.
- **JUnit 5:** For unit and integration testing.

---

## ⚙️ Build and Run (Unix / macOS)

### Prerequisites:
- Java 17 or newer
- Maven

### Build:
```bash
mvn clean package
```

### Run (stdin):
```bash
java -cp target/capital-gains.jar org.finance.Main < input.json
```

### Run (interactive):
```bash
java -cp target/capital-gains.jar org.finance.Main
# Paste a valid JSON array and press enter
```

---

## ✅ Running Tests

```bash
mvn test
```

This will run the entire test suite:
- 9 business rule scenarios from the challenge
- Edge cases (isolation, tax exemption, loss accumulation, etc.)
- DTO and integration layer validations

---

## 🐳 Running with Docker

### Dockerfile:
```Dockerfile
# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

```

### Build Image:
```bash
docker build -t capital-gains .
```

### Run:
```bash
docker run -i capital-gains < input.json
```

---

## 📥 Input and Output Format

### Input Format
The program expects a JSON array of operations, each with the following fields:

```json
[
  {"operation": "buy", "unit-cost": 10.00, "quantity": 100},
  {"operation": "sell", "unit-cost": 15.00, "quantity": 50}
]
```

- `operation`: either `buy` or `sell`
- `unit-cost`: price per unit
- `quantity`: number of units in the transaction

### Output Format
A JSON array containing the `tax` to be paid for each operation, in the same order:

```json
[
  {"tax": 0.00},
  {"tax": 0.00}
]
```

All tax values are rounded to two decimal places using `HALF_UP` rounding.

---

## 📋 Business Rules Implemented

- Weighted average cost is used to calculate capital gains.
- Sales below R$20,000 are exempt from tax.
- A 20% tax applies only to the profit exceeding any accumulated loss.
- Accumulated losses are only deducted in taxable operations.
- Losses are preserved in exempt operations and carried forward.
- When stock quantity reaches zero, the average cost is reset.

---

## 🛡️ Anonymization Notice

All personally identifiable metadata has been stripped:

- No author names
- No IDE or editor comments or templates
- No version control metadata
- Package names are generic (`org.finance`)
- Containerized execution guarantees clean environment

---

## 📝 Final Notes

This project emphasizes correctness, functional purity, and maintainability. All logic is thoroughly tested and adheres strictly to the rules provided in the specification. No third-party frameworks beyond Jackson and JUnit are used to preserve transparency and traceability.