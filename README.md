# GoRest API Automation Tests

Automated API test suite for the [GoRest](https://gorest.co.in) public API,
built with **Java**, **RestAssured**, **TestNG**, and **Allure Report**.

---

## 📁 Project Structure

```
gorest-api-tests/
├── src/
│   └── test/
│       ├── java/
│       │   ├── tests/
│       │   │   └── UserApiTest.java       # All test cases
│       │   └── utils/
│       │       ├── ApiConfig.java         # Base URL & token config (reads .env)
│       │       ├── RequestHelper.java     # Reusable HTTP methods
│       │       ├── TestDataLoader.java    # Loads test data from testData.json
│       │       └── UserPayload.java       # User request/response model
│       └── resources/
│           ├── testData.json              # Externalized test data (user IDs & payloads)
│           └── testng.xml                 # TestNG suite configuration
├── .env                                   # Your local token (git-ignored)
├── .env.example                           # Template — commit this, not .env
├── pom.xml                                # Maven dependencies
└── README.md
```

---

## 🧪 Test Coverage

| # | Method | Endpoint | What is Validated |
|---|--------|----------|-------------------|
| 1 | GET | `/users/{id}` | Status 200, id / name / email present, count active users |
| 2 | POST | `/users` | Status 201, all payload fields match, status=inactive, verified by GET |
| 3 | PUT | `/users/{id}` | Status 200, name & email updated, status=active, active count increases |
| 4 | DELETE | `/users/{id}` | Status 204, subsequent GET returns 404 |

---

## ⚙️ Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Java JDK | 11 or higher |
| Apache Maven | 3.8 or higher |
| GoRest API Token | Free account at gorest.co.in |

---

## 🔑 Setup: Configure Your API Token

The project reads your token from a `.env` file to keep credentials out of source code.

### Step 1 — Get a token

1. Register a free account at [https://gorest.co.in](https://gorest.co.in)
2. Copy your Bearer token from your profile page

### Step 2 — Create your `.env` file

Copy the example template:

```bash
cp .env.example .env
```

Then open `.env` and replace the placeholder with your actual token:

```env
GOREST_TOKEN=your_actual_token_here
```

> `.env` is listed in `.gitignore` and will **never** be committed. Only `.env.example` is tracked by git.

### How it works

`ApiConfig.java` loads the token automatically via the `dotenv-java` library:

```java
private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
public static final String BEARER_TOKEN = dotenv.get("GOREST_TOKEN", System.getenv("GOREST_TOKEN"));
```

It checks in this order:

1. `.env` file in the project root
2. System environment variable `GOREST_TOKEN`

---

## ▶️ How to Run

### Run all tests

```bash
mvn clean test
```

### Run and generate Allure report

```bash
mvn clean test
mvn allure:serve
```

> `allure:serve` automatically opens the report in your browser.

### Generate report without serving

```bash
mvn allure:report
# Report is saved to: target/site/allure-maven-plugin/
```

---

## 📊 Allure Report

After running `mvn allure:serve`, the report shows:

- ✅ Pass / Fail status per test
- 📋 Request & response logs for failed tests
- 🏷️ Test categorized by Epic → Feature → Story
- 🔍 Step-by-step breakdown with assertions

---

## 🔧 Configuration

| Variable | Where | Description |
| -------- | ----- | ----------- |
| `GOREST_TOKEN` | `.env` | Your GoRest Bearer token |
| `BASE_URL` | `ApiConfig.java` | `https://gorest.co.in/public/v2` |
| `createUser` | `testData.json` | Name, gender, and initial status for POST test |
| `updateUser` | `testData.json` | Name, gender, and updated status for PUT test |

---

## 📝 Notes

- Each test run generates a **random unique email** (UUID-based) for the created user to prevent GoRest duplicate-email rejections across runs.
- `testData.json` stores the base payload for POST and PUT. The `status` field in `createUser` follows the assignment sample (`"active"`), but the POST assertion validates `"inactive"` per the requirement — a mismatch will cause a deliberate test failure to surface the discrepancy.
- Tests run in fixed order: GET → POST → PUT → DELETE.
- Test data (user IDs and payloads) is managed in `src/test/resources/testData.json` — no code changes needed for data adjustments.
- Never commit your `.env` file — use `.env.example` as the template for other contributors.
