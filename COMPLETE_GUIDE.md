# Complete Guide: DevOps CI/CD Pipeline Project

## Table of Contents

1. [What is CI/CD?](#1-what-is-cicd)
2. [What is DevOps?](#2-what-is-devops)
3. [Project Overview](#3-project-overview)
4. [Technology Stack Explained](#4-technology-stack-explained)
5. [Project Structure — File by File](#5-project-structure--file-by-file)
6. [The Application Code Explained](#6-the-application-code-explained)
7. [The CI/CD Pipeline — Step by Step](#7-the-cicd-pipeline--step-by-step)
8. [The Dockerfile — Containerization Explained](#8-the-dockerfile--containerization-explained)
9. [Security Tools in the Pipeline](#9-security-tools-in-the-pipeline)
10. [How GitHub Actions Works](#10-how-github-actions-works)
11. [How to Run This Project Locally](#11-how-to-run-this-project-locally)
12. [Common Interview / Viva Questions & Answers](#12-common-interview--viva-questions--answers)

---

## 1. What is CI/CD?

### CI — Continuous Integration

Continuous Integration is a development practice where developers **frequently merge their code changes into a shared repository** (like GitHub). Every time code is pushed, an **automated process** builds the project, runs tests, and checks for errors.

**Without CI:** Developer writes code -> pushes to GitHub -> nobody knows if it broke something -> bugs pile up -> deployment day becomes a nightmare.

**With CI:** Developer writes code -> pushes to GitHub -> automated pipeline immediately builds and tests the code -> if something is broken, the developer gets notified within minutes.

**Key benefits of CI:**

- Bugs are caught early (within minutes of pushing code)
- The codebase is always in a working state
- Developers get fast feedback on their changes
- Integration problems are detected immediately, not weeks later

### CD — Continuous Delivery / Continuous Deployment

**Continuous Delivery** means the code is always in a deployable state. After CI passes, the artifact (e.g., a Docker image) is ready to be deployed at any time with a single click.

**Continuous Deployment** goes one step further — every change that passes the pipeline is **automatically deployed** to production with zero human intervention.

In this project, we implement **Continuous Delivery**: the pipeline builds, tests, scans, and packages the application into a Docker image. The image is then pushed to DockerHub (a container registry), ready to be pulled and deployed anywhere.

### The CI/CD Pipeline Visualized

```
Developer pushes code to GitHub
        |
        v
  +------------------+
  | GitHub detects    |
  | the push event    |
  +------------------+
        |
        v
  +------------------+     +---------------------+
  | Job 1: CodeQL    |     | Job 2: Build &      |
  | (SAST Security   | RUN | Verify              |
  |  Analysis)       | IN  | (Build, Test, Scan, |
  |                  | PAR | Docker, Deploy)      |
  +------------------+ ALL +---------------------+
        |    EL              |
        v                    v
  Finds code-level       1. Checkstyle (Linting)
  vulnerabilities        2. Unit Tests (JUnit)
  like SQL injection,    3. OWASP Dependency Check
  XSS, etc.              4. Build JAR
                         5. Build Docker Image
                         6. Trivy Container Scan
                         7. Smoke Test (run container)
                         8. Push to DockerHub
```

---

## 2. What is DevOps?

DevOps is a **culture and set of practices** that brings together **software development (Dev)** and **IT operations (Ops)**. The goal is to shorten the development lifecycle and deliver high-quality software continuously.

### Traditional Approach vs DevOps

| Aspect         | Traditional              | DevOps                    |
| -------------- | ------------------------ | ------------------------- |
| Releases       | Every few months         | Multiple times per day    |
| Testing        | Manual, at the end       | Automated, at every stage |
| Deployment     | Manual, error-prone      | Automated, repeatable     |
| Feedback       | Weeks/months             | Minutes                   |
| Team structure | Dev and Ops are separate | Dev and Ops collaborate   |
| Infrastructure | Manual server setup      | Infrastructure as Code    |

### Key DevOps Practices Used in This Project

1. **Version Control (Git/GitHub)** — All code is tracked in Git
2. **CI/CD Pipelines (GitHub Actions)** — Automated build, test, and deploy
3. **Containerization (Docker)** — Application packaged in a container for consistency
4. **Infrastructure as Code** — Pipeline defined as YAML code (ci.yml)
5. **Shift-Left Security** — Security checks happen early in the pipeline, not after deployment
6. **Automated Testing** — Unit tests run automatically on every push

---

## 3. Project Overview

This project is a **Spring Boot web application** with a fully automated CI/CD pipeline using **GitHub Actions**. The application itself is simple (a REST API that returns a welcome message), but the focus is on the **DevOps pipeline** that automates everything from code quality checks to container security scanning.

### What the Application Does

It's a Java Spring Boot web server that:

- Starts on port 8080
- Has one endpoint: `GET /` which returns `"Welcome to the DevOps CI/CD Project!"`
- Has health check endpoints via Spring Actuator (e.g., `/actuator/health`)

### What the Pipeline Does

Every time code is pushed to `main`:

1. Checks for security vulnerabilities in the code (CodeQL — SAST)
2. Checks code style/formatting (Checkstyle — Linting)
3. Runs unit tests (JUnit 5)
4. Scans dependencies for known CVEs (OWASP Dependency Check — SCA)
5. Compiles and packages the application into a JAR file
6. Builds a Docker container image
7. Scans the Docker image for OS-level vulnerabilities (Trivy)
8. Runs a smoke test (starts the container and hits the endpoint)
9. Pushes the image to DockerHub (container registry)

---

## 4. Technology Stack Explained

### Java 21 (LTS)

- **What:** The programming language used to write the application
- **Why 21:** It's the latest Long-Term Support (LTS) version — gets security updates for years
- **LTS vs non-LTS:** LTS versions (8, 11, 17, 21) get long-term support; non-LTS versions (18, 19, 20) are only supported for 6 months

### Spring Boot 3.4.1

- **What:** A Java framework that makes it extremely easy to create production-ready web applications
- **Why:** Provides embedded web server (Tomcat), auto-configuration, dependency injection, and production features like health checks (Actuator)
- **Spring Boot vs plain Java:** Without Spring Boot, you'd need to manually configure a web server, handle HTTP requests, manage dependencies — Spring Boot does all of that automatically

### Maven 3.9.12

- **What:** A build tool and dependency manager for Java projects
- **Why:** Manages project dependencies (libraries), compiles code, runs tests, packages the app into a JAR file
- **pom.xml:** The Maven configuration file — lists all dependencies and build plugins
- **Maven Wrapper (mvnw):** A script that downloads and uses the correct Maven version automatically — so you don't need Maven installed on your machine

### Docker

- **What:** A platform for packaging applications into **containers** — lightweight, portable, isolated environments
- **Why:** "It works on my machine" problem is solved. The container runs the same way everywhere — your laptop, CI server, production server
- **Image:** A blueprint/template (like a class in Java)
- **Container:** A running instance of an image (like an object in Java)
- **Dockerfile:** Instructions to build an image

### GitHub Actions

- **What:** GitHub's built-in CI/CD platform
- **Why:** Free for public repos, tightly integrated with GitHub, no separate CI server needed
- **Workflow:** A YAML file that defines the automation pipeline
- **Runner:** A virtual machine (Ubuntu) provided by GitHub that executes the workflow

### DockerHub

- **What:** A public container registry (like GitHub but for Docker images)
- **Why:** After building a Docker image, we push it to DockerHub so it can be pulled and deployed anywhere

---

## 5. Project Structure — File by File

```
devops-cicd/
|
├── .github/
│   └── workflows/
│       └── ci.yml                    # THE CI/CD PIPELINE DEFINITION
│                                     # This is the heart of the project.
│                                     # GitHub Actions reads this file and
│                                     # executes the pipeline automatically.
│
├── devops/                           # THE APPLICATION DIRECTORY
│   │
│   ├── src/
│   │   ├── main/java/com/example/devops/
│   │   │   ├── DevopsApplication.java       # Entry point — starts the app
│   │   │   └── controller/
│   │   │       └── HomeController.java      # REST API controller — handles HTTP requests
│   │   │
│   │   └── test/java/com/example/devops/
│   │       └── DevopsApplicationTests.java  # Unit test — verifies app starts correctly
│   │
│   ├── .mvn/wrapper/
│   │   └── maven-wrapper.properties         # Specifies Maven version (3.9.12)
│   │
│   ├── mvnw                          # Maven wrapper script (Linux/Mac)
│   ├── mvnw.cmd                      # Maven wrapper script (Windows)
│   ├── pom.xml                       # Maven config — dependencies, plugins, build settings
│   ├── Dockerfile                    # Instructions to build the Docker image
│   ├── checkstyle.xml                # Code style rules (like a linter config)
│   └── target/                       # Build output directory (generated, not committed)
│
├── Project_Report.md                 # Project report
├── COMPLETE_GUIDE.md                 # This file — comprehensive guide
└── .git/                             # Git metadata (hidden directory)
```

---

## 6. The Application Code Explained

### DevopsApplication.java — The Entry Point

```java
@SpringBootApplication
public class DevopsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevopsApplication.class, args);
    }
}
```

**Line by line:**

- `@SpringBootApplication` — This annotation tells Spring Boot: "This is the main class. Scan for components, auto-configure everything, and start the web server."
- It's actually a combination of 3 annotations:
  - `@Configuration` — This class can define beans (objects managed by Spring)
  - `@EnableAutoConfiguration` — Spring Boot automatically configures things based on dependencies (e.g., since we have `spring-boot-starter-web`, it auto-configures an embedded Tomcat server)
  - `@ComponentScan` — Scans the package for other annotated classes (like our controller)
- `SpringApplication.run(...)` — Bootstraps the Spring Boot application: creates the application context, starts the embedded Tomcat server on port 8080, registers all controllers

### HomeController.java — The REST API

```java
@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to the DevOps CI/CD Project!";
    }
}
```

**Line by line:**

- `@RestController` — Marks this class as a REST API controller. Every method returns data directly (as JSON or plain text), not a web page view.
- `@GetMapping("/")` — Maps HTTP GET requests to the root URL (`/`) to this method
- When someone visits `http://localhost:8080/`, this method runs and returns the welcome string

### DevopsApplicationTests.java — The Unit Test

```java
@SpringBootTest
class DevopsApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

**What it does:**

- `@SpringBootTest` — Loads the full Spring application context (starts the entire app in test mode)
- `contextLoads()` — This test verifies that the application **starts up without errors**. If any bean fails to initialize, any configuration is wrong, or any dependency is missing, this test will fail.
- It might look empty, but it's actually a powerful test — it validates that the entire application can bootstrap correctly

### pom.xml — Maven Configuration

**Key sections:**

- **Parent:** `spring-boot-starter-parent:3.4.1` — Inherits default configurations from Spring Boot (dependency versions, plugin settings, etc.)
- **Properties:** `java.version: 21` — Tells Maven to compile with Java 21
- **Dependencies:**
  - `spring-boot-starter-web` — Includes Spring MVC, embedded Tomcat, JSON handling (Jackson)
  - `spring-boot-starter-actuator` — Adds production endpoints: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
  - `spring-boot-starter-test` — Includes JUnit 5, Mockito, Spring Test utilities (scope: test — only used during testing, not in production)
- **Build Plugins:**
  - `spring-boot-maven-plugin` — Packages the app as an executable JAR (includes embedded Tomcat)
  - `maven-checkstyle-plugin:3.6.0` — Runs code style checks using rules from `checkstyle.xml`
  - `dependency-check-maven:12.0.1` — OWASP Dependency Check with `failBuildOnCVSS: 7` (fails if any dependency has a vulnerability with CVSS score >= 7)

---

## 7. The CI/CD Pipeline — Step by Step

The pipeline is defined in `.github/workflows/ci.yml`. Here's every single step explained:

### Pipeline Trigger

```yaml
on:
  push:
    branches:
      - main
      - master
  workflow_dispatch:
```

- The pipeline runs automatically when code is pushed to `main` or `master`
- `workflow_dispatch` allows you to manually trigger the pipeline from the GitHub Actions UI (click "Run workflow")

### Job 1: CodeQL Analysis (SAST)

**What is SAST?** Static Application Security Testing — analyzes the **source code** (without running it) to find security vulnerabilities like SQL injection, cross-site scripting (XSS), path traversal, etc.

```yaml
codeql:
  name: CodeQL Analysis (SAST)
  runs-on: ubuntu-latest
  permissions:
    security-events: write # Needed to upload results to GitHub Security tab
    contents: read # Needed to read the repository code
```

**Steps:**

1. **Checkout Code** — Downloads the repository code onto the runner (virtual machine)
2. **Initialize CodeQL** — Sets up the CodeQL analysis engine for Java with `build-mode: manual` (we control the build ourselves)
3. **Setup Java 21** — Installs Java 21 (Temurin distribution) on the runner. Uses Maven caching to speed up dependency downloads.
4. **Build for CodeQL** — Compiles the Java code (`./mvnw clean compile`). CodeQL instruments the build process to understand the code flow.
5. **Perform CodeQL Analysis** — Runs the actual analysis and uploads results to the GitHub "Security" tab

**What CodeQL can detect:**

- SQL injection
- Cross-site scripting (XSS)
- Path traversal
- Insecure deserialization
- Hard-coded credentials
- Missing authentication
- And hundreds more...

### Job 2: Build & Verify

This is the main pipeline job. It runs **in parallel** with CodeQL (both jobs start at the same time).

```yaml
build:
  name: Build & Verify
  runs-on: ubuntu-latest
  env:
    DOCKERHUB_USERNAME: ${{ secrets.DOCKERHUB_USERNAME }}
```

The `env` block stores the DockerHub username from GitHub Secrets into an environment variable. This is used later to check if DockerHub credentials are configured.

#### Step 1: Checkout Code

```yaml
- name: Checkout Code
  uses: actions/checkout@v4
```

Downloads the repository code onto the GitHub-hosted runner (an Ubuntu VM). Without this, the runner has no files to work with.

#### Step 2: Setup Java 21

```yaml
- name: Setup Java 21
  uses: actions/setup-java@v4
  with:
    distribution: "temurin"
    java-version: "21"
    cache: "maven"
```

- Installs the **Temurin** distribution of Java 21 (Temurin = Eclipse's free, production-ready JDK)
- `cache: 'maven'` — Caches the Maven `.m2` repository so dependencies don't need to be re-downloaded on every run (saves ~30 seconds)

#### Step 3: Grant Execute Permission

```yaml
- name: Grant Execute Permission to Maven Wrapper
  run: chmod +x devops/mvnw
```

Git doesn't always preserve file permissions. The Maven wrapper script needs execute permission to run.

#### Step 4: Checkstyle (Linting)

```yaml
- name: Run Checkstyle (Linting)
  run: ./mvnw checkstyle:check -B
  working-directory: devops
```

- **What is Linting?** Checking code for style and formatting issues
- **What is Checkstyle?** A Java tool that enforces coding standards defined in `checkstyle.xml`
- **What it checks:** Indentation (4 spaces), naming conventions (camelCase for methods, PascalCase for classes), brace placement, whitespace, import ordering, Javadoc formatting, etc.
- `-B` flag means "batch mode" — non-interactive, no download progress bars (cleaner CI logs)
- If any code violates the rules, **the pipeline fails here**

#### Step 5: Unit Tests

```yaml
- name: Run Unit Tests
  run: ./mvnw test -B
  working-directory: devops
```

- Runs all unit tests using JUnit 5 via Maven Surefire plugin
- In our case, it runs `DevopsApplicationTests.contextLoads()` which verifies the app starts correctly
- If any test fails, **the pipeline fails here**

#### Step 6: OWASP Dependency Check (SCA)

```yaml
- name: OWASP Dependency Check (SCA)
  run: ./mvnw dependency-check:check -B
  working-directory: devops
  continue-on-error: true
```

- **What is SCA?** Software Composition Analysis — scans your project's **dependencies** (third-party libraries) for known security vulnerabilities
- **How it works:**
  1. Downloads the entire National Vulnerability Database (NVD) — 330,000+ records
  2. Checks every dependency in `pom.xml` against this database
  3. Reports any matches with their CVE ID and CVSS score
- **CVSS Score:** Common Vulnerability Scoring System (0-10). Score of 7+ is HIGH severity.
- `failBuildOnCVSS: 7` (in pom.xml) — Would fail the build if any dependency has a CVSS >= 7
- `continue-on-error: true` — Even if this step fails (e.g., NVD download timeout), the pipeline continues. This is because the NVD download can sometimes be flaky.
- **This step takes ~20 minutes** because it downloads the entire NVD database without an API key

#### Step 7: Upload Dependency Check Report

```yaml
- name: Upload Dependency Check Report
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: dependency-check-report
    path: devops/target/dependency-check-report.html
    retention-days: 7
```

- `if: always()` — Runs even if previous steps failed
- Uploads the HTML report as a **build artifact** — you can download it from the GitHub Actions run page
- `retention-days: 7` — The report is available for 7 days then auto-deleted

#### Step 8: Build Application

```yaml
- name: Build Application
  run: ./mvnw clean package -DskipTests -B
  working-directory: devops
```

- `clean` — Deletes previous build output (`target/` directory)
- `package` — Compiles the code and packages it into a JAR file
- `-DskipTests` — Skips tests (already ran in step 5, no need to run again)
- The output is a file like `devops/target/devops-0.0.1-SNAPSHOT.jar`

#### Step 9: Upload Build Artifact

```yaml
- name: Upload Build Artifact
  uses: actions/upload-artifact@v4
  with:
    name: app-jar
    path: devops/target/*.jar
    retention-days: 7
```

Uploads the built JAR file as a downloadable artifact.

#### Step 10: Set up Docker Buildx

```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v3
```

- **Buildx** is Docker's extended build tool with extra features: caching, multi-platform builds, etc.
- Sets up the Docker build environment on the runner

#### Step 11: Build Docker Image

```yaml
- name: Build Docker Image
  uses: docker/build-push-action@v5
  with:
    context: devops
    push: false
    load: true
    tags: devops-demo:${{ github.sha }}
```

- `context: devops` — The build context is the `devops/` directory (where the Dockerfile is)
- `push: false` — Don't push to any registry yet
- `load: true` — Load the image into the local Docker daemon (so we can use it for testing)
- `tags: devops-demo:<commit-sha>` — Tags the image with the Git commit SHA for traceability. Example: `devops-demo:ea411cb...`
- Uses the Dockerfile to build a multi-stage image (explained in section 8)

#### Step 12: Trivy Image Scan (Container Security)

```yaml
- name: Trivy Image Scan (Container Security)
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: devops-demo:${{ github.sha }}
    format: "table"
    exit-code: "0"
    severity: "CRITICAL,HIGH"
```

- **What is Trivy?** An open-source vulnerability scanner for containers
- **What it scans:** The Docker image's OS packages (Ubuntu/Debian packages), application libraries, configuration files
- `format: 'table'` — Outputs results as a readable table in the CI logs
- `exit-code: '0'` — Don't fail the pipeline even if vulnerabilities are found (informational only)
- `severity: 'CRITICAL,HIGH'` — Only report critical and high severity issues

**Difference from OWASP Dependency Check:**

- OWASP checks **Java dependencies** (libraries in pom.xml)
- Trivy checks **the Docker image** (OS packages, system libraries, the JRE itself)

#### Step 13: Container Smoke Test

```yaml
- name: Run Container Smoke Test
  run: |
    docker run -d --name test-container -p 8080:8080 devops-demo:${{ github.sha }}
    sleep 20
    curl --fail http://localhost:8080/ || exit 1
    docker stop test-container
    docker rm test-container
```

- **What is a Smoke Test?** A basic sanity check — "does the application actually start and respond?"
- `docker run -d` — Starts the container in detached mode (background)
- `-p 8080:8080` — Maps port 8080 on the runner to port 8080 in the container
- `sleep 20` — Waits 20 seconds for Spring Boot to fully start
- `curl --fail http://localhost:8080/` — Makes an HTTP GET request to the root endpoint. `--fail` means curl returns a non-zero exit code if the HTTP response is an error (4xx/5xx)
- If curl fails, `exit 1` stops the pipeline
- Finally, cleans up by stopping and removing the container

#### Step 14: Login to DockerHub

```yaml
- name: Login to DockerHub
  id: docker-login
  if: github.event_name != 'pull_request' && env.DOCKERHUB_USERNAME != ''
  uses: docker/login-action@v3
  continue-on-error: true
  with:
    username: ${{ secrets.DOCKERHUB_USERNAME }}
    password: ${{ secrets.DOCKERHUB_TOKEN }}
```

- `if:` condition — Only runs on pushes (not PRs) AND only if DockerHub credentials are configured
- `continue-on-error: true` — If login fails (wrong credentials), the pipeline still passes
- `id: docker-login` — Gives this step an ID so the next step can check if it succeeded
- Uses GitHub **Secrets** — encrypted variables stored in repo settings, never exposed in logs

#### Step 15: Push to DockerHub

```yaml
- name: Push to DockerHub
  if: steps.docker-login.outcome == 'success'
  uses: docker/build-push-action@v5
  with:
    context: devops
    push: true
    tags: |
      ${{ secrets.DOCKERHUB_USERNAME }}/devops-demo:${{ github.sha }}
      ${{ secrets.DOCKERHUB_USERNAME }}/devops-demo:latest
```

- Only runs if the login step **actually succeeded**
- Pushes the image with two tags:
  - `username/devops-demo:<commit-sha>` — Specific version, fully traceable
  - `username/devops-demo:latest` — Always points to the most recent build

---

## 8. The Dockerfile — Containerization Explained

### What is Docker?

Docker is a platform that packages an application and all its dependencies into a **container** — a lightweight, portable, isolated environment. Think of it like a shipping container: no matter what's inside, it fits on any ship (any server).

### Why Docker?

- **Consistency:** "It works on my machine" is eliminated. The container runs identically everywhere.
- **Isolation:** The application doesn't interfere with other applications on the same server.
- **Portability:** Build once, run anywhere (local machine, cloud, CI server).
- **Scalability:** Easy to run multiple copies (instances) of the container.

### The Dockerfile Explained

```dockerfile
# ===== STAGE 1: BUILD =====
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml checkstyle.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ===== STAGE 2: RUNTIME =====
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Multi-Stage Build — What & Why

This Dockerfile uses a **multi-stage build** — two separate stages in one file:

**Stage 1 (Build):**

- Uses `eclipse-temurin:21-jdk-jammy` — a full JDK (Java Development Kit) with compiler, Maven, etc.
- This image is ~400MB because it includes everything needed to compile Java code
- Steps:
  1. `WORKDIR /app` — Sets the working directory inside the container
  2. `COPY .mvn/ .mvn/` and `COPY mvnw pom.xml checkstyle.xml ./` — Copies build configuration first
  3. `RUN ./mvnw dependency:resolve` — Downloads all dependencies. This is done BEFORE copying source code so Docker can **cache this layer**. Dependencies change rarely; source code changes often. This optimization means dependencies aren't re-downloaded on every build.
  4. `COPY src ./src` — Now copies the actual source code
  5. `RUN ./mvnw clean package -DskipTests` — Compiles and packages the application into a JAR

**Stage 2 (Runtime):**

- Uses `eclipse-temurin:21-jre-jammy` — a minimal JRE (Java Runtime Environment), **no compiler**
- This image is ~200MB — much smaller because it only has what's needed to RUN Java, not compile it
- Steps:
  1. `COPY --from=build /app/target/*.jar app.jar` — Copies ONLY the built JAR from stage 1. The entire build toolchain (Maven, compiler, source code) is discarded.
  2. `EXPOSE 8080` — Documents that the app listens on port 8080
  3. `ENTRYPOINT ["java", "-jar", "app.jar"]` — The command that runs when the container starts

**Why multi-stage?**

- **Security:** The final image has no compiler, no source code, no build tools — smaller attack surface
- **Size:** Final image is ~200MB instead of ~400MB+
- **Speed:** Smaller images are faster to push, pull, and deploy

### Docker Layer Caching

Each instruction in a Dockerfile creates a **layer**. Docker caches layers and only rebuilds from the point where something changed.

```
COPY pom.xml ./              <-- Layer 1: Cached if pom.xml hasn't changed
RUN ./mvnw dependency:resolve <-- Layer 2: Cached if Layer 1 is cached
COPY src ./src                <-- Layer 3: Changes every time code changes
RUN ./mvnw clean package      <-- Layer 4: Must rebuild because Layer 3 changed
```

This is why dependencies are resolved BEFORE copying source code — to maximize cache hits.

---

## 9. Security Tools in the Pipeline

This pipeline implements a **"Shift Left"** security approach — security checks happen early in the development process, not after deployment.

### Tool Comparison

| Tool                | Type           | What it Scans          | What it Finds                         | When it Runs        |
| ------------------- | -------------- | ---------------------- | ------------------------------------- | ------------------- |
| **CodeQL**          | SAST           | Source code            | SQL injection, XSS, hardcoded secrets | Parallel with build |
| **Checkstyle**      | Linting        | Source code            | Style violations, bad practices       | Before tests        |
| **OWASP Dep Check** | SCA            | Dependencies (pom.xml) | Known CVEs in libraries               | After tests         |
| **Trivy**           | Container Scan | Docker image           | OS vulnerabilities, misconfigurations | After Docker build  |

### SAST vs SCA vs Container Scanning

**SAST (Static Application Security Testing)** — CodeQL

- Analyzes YOUR code without executing it
- Finds bugs YOU wrote (SQL injection, XSS, insecure crypto)
- Example: Finds `String query = "SELECT * FROM users WHERE id = " + userInput;` (SQL injection)

**SCA (Software Composition Analysis)** — OWASP Dependency Check

- Analyzes THIRD-PARTY libraries you depend on
- Finds known vulnerabilities (CVEs) in libraries like Spring, Jackson, Log4j
- Example: Detects if you're using a version of Log4j with the Log4Shell vulnerability (CVE-2021-44228)

**Container Scanning** — Trivy

- Analyzes the DOCKER IMAGE (OS packages, system libraries)
- Finds vulnerabilities in the base image's operating system
- Example: Detects if the Ubuntu base image has an unpatched OpenSSL vulnerability

### What is CVSS?

**Common Vulnerability Scoring System** — A standardized way to rate vulnerability severity:

| Score      | Rating   | Example                                |
| ---------- | -------- | -------------------------------------- |
| 0.0        | None     | —                                      |
| 0.1 - 3.9  | Low      | Information disclosure                 |
| 4.0 - 6.9  | Medium   | Cross-site scripting                   |
| 7.0 - 8.9  | High     | SQL injection                          |
| 9.0 - 10.0 | Critical | Remote code execution (like Log4Shell) |

Our pipeline: `failBuildOnCVSS: 7` — Blocks the build if any dependency has a HIGH or CRITICAL vulnerability.

---

## 10. How GitHub Actions Works

### Key Concepts

**Workflow:** A YAML file in `.github/workflows/` that defines the automation. A repo can have multiple workflows.

**Event/Trigger:** What starts the workflow. Examples: `push`, `pull_request`, `workflow_dispatch` (manual), `schedule` (cron).

**Job:** A set of steps that run on the same runner. Jobs run **in parallel** by default (unless you set `needs:` to create dependencies).

**Step:** A single task within a job. Can be:

- `run:` — Execute a shell command
- `uses:` — Use a pre-built GitHub Action (reusable automation)

**Runner:** A virtual machine that executes the workflow. `ubuntu-latest` is a GitHub-hosted Ubuntu VM with Docker, Git, Java, and many tools pre-installed.

**Secrets:** Encrypted environment variables stored in repo Settings > Secrets. Accessed via `${{ secrets.SECRET_NAME }}`. Never printed in logs (masked as `***`).

**Artifacts:** Files produced by the pipeline that can be downloaded later. Example: dependency check report, built JAR.

### YAML Syntax Basics

```yaml
name: CI Pipeline # Workflow name (shown in GitHub UI)

on: # Trigger events
  push:
    branches: [main] # Only on pushes to main

jobs: # List of jobs
  build: # Job ID (any name)
    name: Build & Verify # Display name
    runs-on: ubuntu-latest # Runner type

    steps: # Steps within the job
      - name: Step Name # Display name for the step
        uses: action@v4 # Use a pre-built action
        with: # Input parameters for the action
          key: value

      - name: Another Step
        run: echo "Hello" # Run a shell command
        working-directory: devops # Change directory before running
        continue-on-error: true # Don't fail the job if this step fails
```

### How `if:` Conditions Work

```yaml
if: github.event_name != 'pull_request' && env.DOCKERHUB_USERNAME != ''
```

- `github.event_name` — The event that triggered the workflow (e.g., `push`, `pull_request`)
- `env.VARIABLE` — An environment variable
- `steps.step-id.outcome` — Result of a previous step (`success`, `failure`, `skipped`)
- `always()` — Always run, even if previous steps failed
- `success()` — Only run if all previous steps succeeded (default behavior)

### How Secrets Work

1. Go to repo Settings > Secrets and variables > Actions
2. Add a new secret (e.g., `DOCKERHUB_USERNAME` = `myusername`)
3. In the workflow, access it: `${{ secrets.DOCKERHUB_USERNAME }}`
4. GitHub automatically masks secret values in logs (shown as `***`)

---

## 11. How to Run This Project Locally

### Prerequisites

- Java 21 installed
- Docker installed

### Run the Application Directly

```bash
cd devops
chmod +x mvnw
./mvnw spring-boot:run
```

Then visit: `http://localhost:8080/`

### Run Tests

```bash
cd devops
./mvnw test
```

### Run Checkstyle

```bash
cd devops
./mvnw checkstyle:check
```

### Build the JAR

```bash
cd devops
./mvnw clean package -DskipTests
java -jar target/devops-0.0.1-SNAPSHOT.jar
```

### Build and Run with Docker

```bash
cd devops
docker build -t devops-demo .
docker run -p 8080:8080 devops-demo
```

Then visit: `http://localhost:8080/`

---

## 12. Common Interview / Viva Questions & Answers

### Q: What is the difference between CI and CD?

**A:** CI (Continuous Integration) is about automatically building and testing code every time a developer pushes changes. CD (Continuous Delivery) extends this by ensuring the code is always in a deployable state, with automated packaging and optional deployment. CI catches bugs early; CD ensures you can release at any time.

### Q: Why did you use GitHub Actions instead of Jenkins?

**A:** GitHub Actions is natively integrated with GitHub — no separate server to maintain, free for public repos, and the workflow is defined as code (YAML) right in the repository. Jenkins requires setting up and maintaining a separate server, but offers more flexibility for complex enterprise pipelines.

### Q: What is a multi-stage Docker build and why did you use it?

**A:** A multi-stage build uses multiple `FROM` statements in one Dockerfile. The first stage compiles the app using a full JDK (with compiler). The second stage uses a minimal JRE (runtime only) and copies just the built JAR. Benefits: smaller image size (~200MB vs ~400MB), better security (no build tools in production), and cleaner separation of build vs runtime.

### Q: What does "Shift Left" mean in DevOps security?

**A:** "Shift Left" means moving security checks earlier (to the left) in the development lifecycle. Instead of testing for vulnerabilities after deployment, we check during the CI pipeline itself — before the code ever reaches production. In our pipeline, we do SAST (CodeQL), SCA (OWASP), and container scanning (Trivy) all before deployment.

### Q: What is the difference between SAST and SCA?

**A:** SAST (Static Application Security Testing) analyzes your own source code for vulnerabilities you wrote (like SQL injection). SCA (Software Composition Analysis) analyzes third-party libraries/dependencies for known CVEs that others discovered. SAST finds YOUR bugs; SCA finds bugs in code YOU DEPEND ON.

### Q: What is a Docker image vs a Docker container?

**A:** An image is a read-only template (like a class in OOP). A container is a running instance of an image (like an object). You can create multiple containers from the same image. Images are built from Dockerfiles; containers are started with `docker run`.

### Q: Why does the OWASP Dependency Check take so long?

**A:** It downloads the entire NVD (National Vulnerability Database) — 330,000+ vulnerability records — before scanning. Without an NVD API key, this download is rate-limited and takes ~20 minutes. With a free API key, it would take ~2-3 minutes.

### Q: What is a smoke test?

**A:** A smoke test is a basic sanity check that verifies the most fundamental functionality works. In our pipeline, we start the Docker container and make an HTTP request to verify the app responds. It doesn't test business logic — it just confirms "the app starts and responds to requests." The term comes from hardware testing: "plug it in and see if smoke comes out."

### Q: What happens if a step fails in the pipeline?

**A:** By default, if any step fails, all subsequent steps are skipped and the job is marked as failed. However, we can override this with:

- `continue-on-error: true` — The step is marked as failed but the job continues
- `if: always()` — The step runs regardless of previous step outcomes
- `if: failure()` — The step runs only if a previous step failed (useful for cleanup/notifications)

### Q: Why do we tag Docker images with the Git commit SHA?

**A:** For **traceability**. If a bug is found in production, we can look at the Docker image tag, find the exact Git commit that produced it, and see exactly what code is running. The `latest` tag is convenient but ambiguous — the SHA tag is precise.

### Q: What are GitHub Secrets and why are they needed?

**A:** GitHub Secrets are encrypted environment variables stored in repository settings. They're used for sensitive data like passwords, API keys, and tokens. In our pipeline, we store DockerHub credentials as secrets so the pipeline can push images without exposing the password. Secrets are masked in logs (shown as `***`) and are never exposed to forked repos.

### Q: What is Spring Boot Actuator and why is it in the project?

**A:** Spring Boot Actuator adds production-ready endpoints to the application: `/actuator/health` (is the app running?), `/actuator/info` (app metadata), `/actuator/metrics` (performance data). These are essential for monitoring in production and for health checks in container orchestration platforms like Kubernetes.

### Q: What would you add to improve this pipeline?

**A:** Possible improvements:

1. **NVD API Key** — Speed up OWASP dependency check from 20 min to 2 min
2. **Integration tests** — Test API endpoints with actual HTTP requests
3. **Code coverage** — Use JaCoCo to measure test coverage percentage
4. **Kubernetes deployment** — Auto-deploy to a K8s cluster after image push
5. **Slack/Email notifications** — Alert the team on pipeline failure
6. **Branch protection rules** — Require CI to pass before merging to main
7. **Caching** — Cache Docker layers between builds for faster pipeline runs

### Q: What is the purpose of `continue-on-error: true` on the DockerHub login step?

**A:** It makes the DockerHub push **truly optional**. If DockerHub credentials are not configured or are invalid, the login fails but the pipeline still passes. Without it, the entire pipeline would fail just because of a DockerHub authentication issue — even though the code was built, tested, and scanned successfully. The push step then checks `steps.docker-login.outcome == 'success'` to only push if login actually worked.

### Q: Explain the flow when a developer pushes code to main.

**A:**

1. Developer runs `git push origin main`
2. GitHub receives the push and checks `.github/workflows/ci.yml`
3. GitHub spins up two Ubuntu VMs (runners) — one for each job
4. **Job 1 (CodeQL):** Compiles the code with CodeQL instrumentation, analyzes for security vulnerabilities, uploads results to the Security tab
5. **Job 2 (Build & Verify):** Runs checkstyle (linting) -> unit tests -> OWASP dependency scan -> builds JAR -> builds Docker image -> Trivy container scan -> smoke test (starts container, hits endpoint) -> pushes to DockerHub
6. Both jobs run in parallel to save time
7. If everything passes, the pipeline shows a green checkmark
8. If anything fails, the developer gets a notification email
9. The Docker image is now on DockerHub, ready to be deployed anywhere with `docker pull`

---

## Summary

This project demonstrates a complete, production-grade CI/CD pipeline that follows DevOps best practices:

- **Automation:** Zero manual steps from code push to deployable artifact
- **Quality Gates:** Code must pass linting, tests, and security scans before being packaged
- **Security:** Four layers of security scanning (SAST, SCA, Container Scan, Smoke Test)
- **Containerization:** Application packaged as a Docker image for consistent deployment
- **Traceability:** Every image tagged with the Git commit SHA
- **Resilience:** Optional steps (DockerHub push) don't break the core pipeline
