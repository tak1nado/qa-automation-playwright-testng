# Your QA Automation Framework - Corrected Detailed Technical Documentation

## 1. Executive Summary
A production-grade QA automation framework built with Java, Maven, Selenium/Playwright, Cucumber, and Spring. Designed for enterprise-level web application testing with thread-safe concurrent execution, comprehensive reporting, and modular architecture. Uses Spring XML configuration with environment-specific profiles instead of Java `@Bean` annotations.

---

## 2. Technology Stack & Dependencies

### Core Dependencies
- **Java**: JDK 11+
- **Build Tool**: Maven 3.6+
- **Test Framework**: Cucumber JVM (BDD)
- **Web Driver**: Playwright (primary), Selenium WebDriver (alternative)
- **Dependency Injection**: Spring Framework 5.x+ (XML-based configuration)
- **Reporting**: Allure 2.x
- **Logging**: Java Util Logging + Lombok
- **Assertions**: TestNG Assert
- **Configuration**: Spring XML Profiles

### Maven POM Structure
```xml
<properties>
    <java.version>11</java.version>
    <maven.compiler.version>3.8.1</maven.compiler.version>
    <cucumber.version>7.x.x</cucumber.version>
    <playwright.version>1.4x.x</playwright.version>
    <spring.version>5.3.x</spring.version>
    <allure.version>2.20.x</allure.version>
    <lombok.version>1.18.x</lombok.version>
</properties>

<dependencies>
    <!-- Cucumber -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit</artifactId>
    </dependency>
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
    </dependency>

    <!-- Playwright -->
    <dependency>
        <groupId>com.microsoft.playwright</groupId>
        <artifactId>playwright</artifactId>
    </dependency>

    <!-- Spring -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>

    <!-- Allure -->
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-cucumber7-jvm</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <systemPropertyVariables>
                    <spring.profiles.active>${spring.profiles.active}</spring.profiles.active>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

---

## 3. Core Architecture & Design Patterns

### 3.1 Thread-Safe Variable Storage
**File**: `src/main/java/com/project/qa/helpers/ThreadVarsHashMap.java`

**Purpose**: Thread-local storage for test scenario-specific data with type safety.

**Key Characteristics**:
- Generic implementation `<T>`
- Automatic thread isolation
- Cleanup after scenario execution
- No cross-scenario data leakage
- Used with `TestKeyword` enum for type-safe key management

**Usage Pattern**:
```
Thread A (Test 1) -> HashMap (isolated data)
Thread B (Test 2) -> HashMap (isolated data)
Thread C (Test 3) -> HashMap (isolated data)
```

---

### 3.2 Test Keywords Enum
**File**: `src/main/java/com/project/qa/steps/TestKeyword.java`

**Purpose**: Enumeration defining constants for thread-local storage keys. Ensures type-safe and consistent key names across step definitions.

**Current Implementation**:
```java
public enum TestKeyword {
    BOOKING_DATA,
    BOOKING,
    STUDENT_DATA,
    STUDENT;
}
```

**Usage**:
- Keys for storing/retrieving scenario-specific data in `ThreadVarsHashMap`
- Prevents string-based key typos
- Centralized definition of all test data keys

**Example Usage**:
```java
// Store data
threadVarsHashMap.put(TestKeyword.BOOKING_DATA.name(), bookingData);

// Retrieve data
BookingData data = threadVarsHashMap.getTyped(TestKeyword.BOOKING_DATA.name());
```

---

### 3.3 Browser Session Management

#### 3.3.1 BrowserSessions Singleton
**File**: `src/main/java/com/project/qa/helpers/web/engine/BrowserSessions.java`

**Purpose**: Manages all active browser instances and tab sessions across the test execution. Defined as Spring bean in XML configuration.

**Key Responsibilities**:
- Maintains registry of all open browser sessions
- Tracks active/current browser session
- Manages lifecycle (creation, closure, cleanup)
- Thread-safe singleton via Spring XML `<bean scope="singleton">`
- Collects console errors from all sessions

**Data Structure**:
```
BrowserSessions {
    Map<String, BrowserTabSession> activeSessions (synchronized)
    String activeBrowserId
    List<String> consoleErrorsList (synchronized)
}
```

**Core Methods**:
- `createNewBrowserSession(String sessionId)`: Creates new browser instance
- `getActiveBrowserSession()`: Returns current active session
- `switchBrowserSession(String sessionId)`: Switches between browser instances
- `closeAllBrowsers()`: Cleanup on test completion
- `getConsoleErrorsList()`: Retrieves collected console errors
- `getSessions()`: Returns all active sessions

---

#### 3.3.2 BrowserTabSession
**File**: `src/main/java/com/project/qa/helpers/web/engine/BrowserTabSession.java`

**Purpose**: Represents single browser tab/page with Playwright integration.

**Key Responsibilities**:
- Wraps Playwright `Page` object
- Manages page interactions (navigation, clicks, input)
- Captures screenshots
- Monitors console messages and errors
- Stores console error logs for assertion

**Key Properties**:
- `Page page`: Playwright page instance
- `String sessionId`: Unique session identifier
- `List<String> consoleErrorsList`: Collected browser errors
- `BrowserContext context`: Playwright browser context
- `Browser browser`: Playwright browser instance

**Core Methods**:
- `getPage()`: Returns Playwright page
- `addConsoleError(String error)`: Logs console message
- `getConsoleErrorsList()`: Returns all collected errors
- `captureScreenshot()`: Returns screenshot as byte array
- `navigateTo(String url)`: Navigation wrapper
- `close()`: Cleanup single session

---

### 3.4 Step Definition Classes
**Location**: `src/test/java/com/project/qa/steps/`

**Architecture**:
- Individual step definition classes (NOT extending a base class)
- Each class handles specific feature domain (BookingSteps, StudentSteps, etc.)
- Autowired with `BrowserSessions` and `ThreadVarsHashMap` via Spring XML
- Annotated with Cucumber `@Given`, `@When`, `@Then`

**Typical Implementation**:
```java
@Log
public class BookingSteps {
    @Autowired
    private BrowserSessions browserSessions;

    @Autowired
    private ThreadVarsHashMap threadVarsHashMap;

    @Given("User creates booking")
    public void createBooking() {
        BrowserTabSession session = 
            browserSessions.getActiveBrowserSession();
        // Booking logic
        threadVarsHashMap.put(TestKeyword.BOOKING.name(), bookingObject);
    }

    @When("User enters booking data")
    public void enterBookingData() {
        String bookingRef = (String) threadVarsHashMap
            .get(TestKeyword.BOOKING_DATA.name());
        // Use data in step
    }

    @Then("Booking is created successfully")
    public void verifyBookingCreation() {
        BrowserTabSession session = 
            browserSessions.getActiveBrowserSession();
        assertTrue(session.getPage().isVisible("[id='confirmationMsg']"));
    }
}
```

---

### 3.5 Cucumber Hooks & Lifecycle Management
**File**: `src/test/java/com/project/qa/runners/CucumberHooks.java`

**Purpose**: Manage test lifecycle events and post-test validation.

**Spring Integration**: Autowired via XML configuration, not `@Bean` annotations.

#### Hook Execution Order
1. **@After(order=2)**: Screenshot capture on failure
2. **@After(order=3)**: Browser error validation
3. **@After(order=100)**: Thread variable cleanup

#### Hook Details

**onFailure(order=2)**:
- Triggered on scenario failure
- Captures full-page screenshot
- Attaches browser console errors if present
- Integration with Allure reporting

**checkForBrowserErrors(order=3)**:
- Validates browser console for errors
- Configurable via `console.errors` property from XML
- Throws assertion if errors detected
- Clears error list for next scenario

**clearThreadVars(order=100)**:
- Final cleanup step
- Ensures no data leakage between scenarios
- Runs regardless of test pass/fail

---

## 4. Spring XML Configuration

### 4.1 Base Configuration File
**File**: `src/test/resources/applicationContext.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- Component Scanning -->
    <context:component-scan base-package="com.project.qa.steps,com.project.qa.runners"/>

    <!-- Property Placeholder -->
    <context:property-placeholder location="classpath:application.properties"/>

    <!-- BrowserSessions Singleton -->
    <bean id="browserSessions" class="com.project.qa.helpers.web.engine.BrowserSessions" scope="singleton"/>

    <!-- ThreadVarsHashMap Bean -->
    <bean id="threadVarsHashMap" class="com.project.qa.helpers.ThreadVarsHashMap" scope="singleton"/>

    <!-- Cucumber Hooks -->
    <bean id="cucumberHooks" class="com.project.qa.runners.CucumberHooks">
        <property name="browserSessions" ref="browserSessions"/>
        <property name="threadVarsHashMap" ref="threadVarsHashMap"/>
        <property name="consoleErrorsEnabled" value="${console.errors:false}"/>
    </bean>

</beans>
```

### 4.2 Environment-Specific Profiles

#### Development Profile: `applicationContext-dev.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
       profile="dev">

    <!-- Development specific configuration -->
    <bean id="browserConfig" class="com.project.qa.config.BrowserConfig">
        <property name="headless" value="false"/>
        <property name="baseUrl" value="http://localhost:3000"/>
        <property name="timeout" value="30000"/>
    </bean>

</beans>
```

#### Staging Profile: `applicationContext-staging.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
       profile="staging">

    <!-- Staging specific configuration -->
    <bean id="browserConfig" class="com.project.qa.config.BrowserConfig">
        <property name="headless" value="true"/>
        <property name="baseUrl" value="https://staging.example.com"/>
        <property name="timeout" value="30000"/>
    </bean>

</beans>
```

#### Production Profile: `applicationContext-prod.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
       profile="prod">

    <!-- Production specific configuration -->
    <bean id="browserConfig" class="com.project.qa.config.BrowserConfig">
        <property name="headless" value="true"/>
        <property name="baseUrl" value="https://production.example.com"/>
        <property name="timeout" value="60000"/>
        <property name="retryAttempts" value="3"/>
    </bean>

</beans>
```

### 4.3 Application Properties Files

**File**: `src/test/resources/application.properties`
```properties
# Browser Configuration
browser.type=chromium
browser.headless=true
browser.timeout=30000
browser.slowMo=0

# Console Error Handling
console.errors=true

# Reporting
allure.results.directory=target/allure-results

# Logging
logging.level.root=INFO
logging.level.com.project.qa=DEBUG

# Environment (default, overridden by profile)
app.base.url=http://localhost:3000
```

**File**: `src/test/resources/application-dev.properties`
```properties
browser.headless=false
app.base.url=http://localhost:3000
logging.level.com.project.qa=DEBUG
browser.slowMo=500
```

**File**: `src/test/resources/application-staging.properties`
```properties
browser.headless=true
app.base.url=https://staging.example.com
logging.level.com.project.qa=INFO
console.errors=true
```

**File**: `src/test/resources/application-prod.properties`
```properties
browser.headless=true
app.base.url=https://production.example.com
logging.level.root=WARN
logging.level.com.project.qa=INFO
console.errors=true
browser.timeout=60000
```

---

## 5. Detailed File Structure

```
project-root/
├── pom.xml
├── src/
│   ├── main/java/com/project/qa/
│   │   ├── helpers/
│   │   │   ├── ThreadVarsHashMap.java
│   │   │   └── web/
│   │   │       └── engine/
│   │   │           ├── BrowserSessions.java
│   │   │           ├── BrowserTabSession.java
│   │   │           └── BrowserFactory.java
│   │   ├── steps/
│   │   │   └── TestKeyword.java (ENUM)
│   │   ├── config/
│   │   │   └── BrowserConfig.java
│   │   └── utils/
│   │       ├── LoggerUtil.java
│   │       ├── WaitUtil.java
│   │       └── ScreenshotUtil.java
│   ├── test/java/com/project/qa/
│   │   ├── runners/
│   │   │   ├── CucumberHooks.java
│   │   │   └── CucumberRunner.java
│   │   ├── steps/
│   │   │   ├── BookingSteps.java
│   │   │   ├── StudentSteps.java
│   │   │   └── CommonSteps.java
│   │   └── resources/
│   │       ├── applicationContext.xml
│   │       ├── applicationContext-dev.xml
│   │       ├── applicationContext-staging.xml
│   │       ├── applicationContext-prod.xml
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-staging.properties
│   │       ├── application-prod.properties
│   │       └── features/
│   │           ├── booking.feature
│   │           ├── student.feature
│   │           └── common.feature
└── target/
    └── allure-results/
```

---

## 6. Class-by-Class Implementation Details

### 6.1 ThreadVarsHashMap.java
```java
@Getter
public class ThreadVarsHashMap<T> {
    private final ThreadLocal<Map<String, Object>> threadLocalMap =
        ThreadLocal.withInitial(HashMap::new);

    public void put(String key, Object value) {
        threadLocalMap.get().put(key, value);
    }

    public Object get(String key) {
        return threadLocalMap.get().get(key);
    }

    @SuppressWarnings("unchecked")
    public <V> V getTyped(String key) {
        return (V) threadLocalMap.get().get(key);
    }

    public boolean containsKey(String key) {
        return threadLocalMap.get().containsKey(key);
    }

    public void clear() {
        threadLocalMap.remove();
    }
}
```

### 6.2 TestKeyword.java (ENUM)
```java
package com.project.qa.steps;

public enum TestKeyword {
    BOOKING_DATA,
    BOOKING,
    STUDENT_DATA,
    STUDENT;
}
```

### 6.3 BrowserSessions.java
```java
@Getter
@Log
public class BrowserSessions {
    private final Map<String, BrowserTabSession> sessions =
        Collections.synchronizedMap(new HashMap<>());
    private final List<String> consoleErrorsList =
        Collections.synchronizedList(new ArrayList<>());
    private String activeBrowserId;

    public BrowserTabSession createNewBrowserSession(String sessionId) {
        BrowserTabSession session = new BrowserTabSession(sessionId);
        sessions.put(sessionId, session);
        activeBrowserId = sessionId;
        log.info("Created new browser session: " + sessionId);
        return session;
    }

    public BrowserTabSession getActiveBrowserSession() {
        if (activeBrowserId == null) {
            throw new IllegalStateException("No active browser session");
        }
        return sessions.get(activeBrowserId);
    }

    public void switchBrowserSession(String sessionId) {
        if (sessions.containsKey(sessionId)) {
            activeBrowserId = sessionId;
            log.info("Switched to browser session: " + sessionId);
        } else {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
    }

    public void closeAllBrowsers() {
        sessions.values().forEach(session -> {
            try {
                session.close();
            } catch (Exception e) {
                log.warning("Error closing browser: " + e.getMessage());
            }
        });
        sessions.clear();
        consoleErrorsList.clear();
        activeBrowserId = null;
    }
}
```

### 6.4 BrowserTabSession.java
```java
@Getter
@Log
public class BrowserTabSession {
    private final String sessionId;
    private Page page;
    private BrowserContext context;
    private Browser browser;
    private final List<String> consoleErrorsList =
        Collections.synchronizedList(new ArrayList<>());

    public BrowserTabSession(String sessionId) {
        this.sessionId = sessionId;
        initializeBrowser();
    }

    private void initializeBrowser() {
        try {
            browser = new BrowserFactory().createBrowser();
            context = browser.newContext();
            page = context.newPage();

            // Console error listener
            page.onConsoleMessage(msg -> {
                if ("error".equals(msg.type())) {
                    String errorMsg = msg.text();
                    consoleErrorsList.add(errorMsg);
                    log.warning("Browser console error: " + errorMsg);
                }
            });
            log.info("Browser session initialized: " + sessionId);
        } catch (Exception e) {
            log.severe("Failed to initialize browser: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public byte[] captureScreenshot() {
        try {
            return page.screenshot(new Page.ScreenshotOptions());
        } catch (Exception e) {
            log.warning("Screenshot capture failed: " + e.getMessage());
            return new byte[0];
        }
    }

    public void close() {
        try {
            if (page != null) page.close();
            if (context != null) context.close();
            if (browser != null) browser.close();
            log.info("Browser session closed: " + sessionId);
        } catch (Exception e) {
            log.warning("Error closing browser session: " + e.getMessage());
        }
    }
}
```

### 6.5 CucumberHooks.java (Complete with XML Configuration)
```java
@Log
public class CucumberHooks {
    private BrowserSessions browserSessions;
    private ThreadVarsHashMap threadVarsHashMap;
    private boolean consoleErrorsEnabled;

    // Setters for XML injection
    public void setBrowserSessions(BrowserSessions browserSessions) {
        this.browserSessions = browserSessions;
    }

    public void setThreadVarsHashMap(ThreadVarsHashMap threadVarsHashMap) {
        this.threadVarsHashMap = threadVarsHashMap;
    }

    public void setConsoleErrorsEnabled(boolean consoleErrorsEnabled) {
        this.consoleErrorsEnabled = consoleErrorsEnabled;
    }

    @After(order = 2)
    public synchronized void onFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            log.info("Scenario failed: " + scenario.getName());
            captureScreenshot(scenario);
            
            List<String> errors = browserSessions.getActiveBrowserSession()
                .getConsoleErrorsList();
            if (!errors.isEmpty()) {
                attachBrowserLogs();
            }
        }
    }

    @After(order = 3)
    public synchronized void checkForBrowserErrors(Scenario scenario) {
        try {
            List<String> errors = browserSessions.getActiveBrowserSession()
                .getConsoleErrorsList();

            if (consoleErrorsEnabled && !errors.isEmpty()) {
                attachBrowserLogs();
                String errorMsg = "Critical browser errors detected:\n" +
                    String.join("\n--------------\n", errors);
                log.severe(errorMsg);
                throw new AssertionError(errorMsg);
            }
        } catch (Exception e) {
            log.warning("Error checking for browser errors: " + e.getMessage());
        }
    }

    @After(order = 100)
    public synchronized void clearThreadVars(Scenario scenario) {
        try {
            threadVarsHashMap.clear();
            browserSessions.closeAllBrowsers();
            log.info("Cleanup completed for scenario: " + scenario.getName());
        } catch (Exception e) {
            log.warning("Error during cleanup: " + e.getMessage());
        }
    }

    private void captureScreenshot(Scenario scenario) {
        try {
            BrowserTabSession session = browserSessions.getActiveBrowserSession();
            byte[] screenshotBytes = session.captureScreenshot();
            
            if (screenshotBytes.length > 0) {
                Allure.addAttachment(
                    "Failure Screenshot - " + scenario.getName(),
                    "image/png",
                    new ByteArrayInputStream(screenshotBytes),
                    "png"
                );
                log.info("Screenshot captured for scenario: " + scenario.getName());
            }
        } catch (Exception e) {
            log.warning("Screenshot capture failed: " + e.getMessage());
        }
    }

    private void attachBrowserLogs() {
        try {
            List<String> errors = browserSessions.getActiveBrowserSession()
                .getConsoleErrorsList();
            if (!errors.isEmpty()) {
                String logs = String.join("\n--------------\n", errors);
                Allure.addAttachment(
                    "Browser Console Errors",
                    "text/plain",
                    new ByteArrayInputStream(logs.getBytes()),
                    "txt"
                );
            }
        } catch (Exception e) {
            log.warning("Failed to attach browser logs: " + e.getMessage());
        }
    }
}
```

### 6.6 Example Step Definition: BookingSteps.java
```java
@Log
public class BookingSteps {
    private BrowserSessions browserSessions;
    private ThreadVarsHashMap threadVarsHashMap;

    // Setters for XML injection
    public void setBrowserSessions(BrowserSessions browserSessions) {
        this.browserSessions = browserSessions;
    }

    public void setThreadVarsHashMap(ThreadVarsHashMap threadVarsHashMap) {
        this.threadVarsHashMap = threadVarsHashMap;
    }

    @Given("User navigates to booking page")
    public void navigateToBookingPage() {
        BrowserTabSession session = browserSessions.getActiveBrowserSession();
        session.getPage().navigate("https://app.example.com/booking");
        log.info("Navigated to booking page");
    }

    @When("User enters booking reference {string}")
    public void enterBookingReference(String bookingRef) {
        threadVarsHashMap.put(TestKeyword.BOOKING_DATA.name(), bookingRef);
        BrowserTabSession session = browserSessions.getActiveBrowserSession();
        session.getPage().fill("[id='bookingRef']", bookingRef);
        log.info("Entered booking reference: " + bookingRef);
    }

    @When("User selects travel date {string}")
    public void selectTravelDate(String date) {
        BrowserTabSession session = browserSessions.getActiveBrowserSession();
        session.getPage().fill("[id='travelDate']", date);
        log.info("Selected travel date: " + date);
    }

    @When("User clicks search button")
    public void clickSearchButton() {
        BrowserTabSession session = browserSessions.getActiveBrowserSession();
        session.getPage().click("[id='searchBtn']");
        session.getPage().waitForLoadState(LoadState.NETWORKIDLE);
        log.info("Clicked search button");
    }

    @Then("Booking results should be displayed")
    public void verifyBookingResults() {
        BrowserTabSession session = browserSessions.getActiveBrowserSession();
        boolean resultsVisible = session.getPage()
            .isVisible("[id='bookingResults']");
        
        if (!resultsVisible) {
            throw new AssertionError("Booking results not displayed");
        }
        log.info("Booking results displayed successfully");
    }

    @Then("Booking data should be saved")
    public void verifyBookingSaved() {
        Object bookingData = threadVarsHashMap
            .get(TestKeyword.BOOKING_DATA.name());
        
        if (bookingData == null) {
            throw new AssertionError("Booking data not found in thread storage");
        }
        log.info("Booking data verified: " + bookingData);
    }
}
```

---

## 7. Spring Bean Configuration Setup (XML Dependency Injection)

### Setup in Cucumber Hooks
```java
@Log
public class CucumberHooks {
    // Properties injected via XML
    private BrowserSessions browserSessions;
    private ThreadVarsHashMap threadVarsHashMap;
    private boolean consoleErrorsEnabled;

    // Setter-based injection (required for XML)
    public void setBrowserSessions(BrowserSessions browserSessions) {
        this.browserSessions = browserSessions;
    }

    public void setThreadVarsHashMap(ThreadVarsHashMap threadVarsHashMap) {
        this.threadVarsHashMap = threadVarsHashMap;
    }

    public void setConsoleErrorsEnabled(boolean consoleErrorsEnabled) {
        this.consoleErrorsEnabled = consoleErrorsEnabled;
    }
}
```

### Setup in Step Definition Classes
```java
@Log
public class BookingSteps {
    // Properties injected via XML
    private BrowserSessions browserSessions;
    private ThreadVarsHashMap threadVarsHashMap;

    // Setter-based injection (required for XML)
    public void setBrowserSessions(BrowserSessions browserSessions) {
        this.browserSessions = browserSessions;
    }

    public void setThreadVarsHashMap(ThreadVarsHashMap threadVarsHashMap) {
        this.threadVarsHashMap = threadVarsHashMap;
    }
}
```

### XML Bean Registration
```xml
<bean id="bookingSteps" class="com.project.qa.steps.BookingSteps">
    <property name="browserSessions" ref="browserSessions"/>
    <property name="threadVarsHashMap" ref="threadVarsHashMap"/>
</bean>

<bean id="studentSteps" class="com.project.qa.steps.StudentSteps">
    <property name="browserSessions" ref="browserSessions"/>
    <property name="threadVarsHashMap" ref="threadVarsHashMap"/>
</bean>
```

---

## 8. Maven Profile Configuration

### POM Profile for Environment Selection
```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>

    <profile>
        <id>staging</id>
        <properties>
            <spring.profiles.active>staging</spring.profiles.active>
        </properties>
    </profile>

    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

### Running Tests with Profiles
```bash
# Development environment (default)
mvn clean test

# Staging environment
mvn clean test -P staging

# Production environment
mvn clean test -P prod
```

---

## 9. Cucumber Feature Files Structure

### Example: booking.feature
```gherkin
Feature: Booking ManagementBackground:
    Given User navigates to booking page

  Scenario: Search and create new booking
    When User enters booking reference "REF123"
    And User selects travel date "2024-12-15"
    And User clicks search button
    Then Booking results should be displayed
    And Booking data should be saved

  Scenario: Modify existing booking
    When User enters booking reference "EXISTING123"
    And User selects travel date "2024-12-20"
    And User clicks search button
    Then Booking results should be displayed
```

---

## 10. Test Execution Flow

```
1. Maven executes with profile (dev/staging/prod)
                    ↓
2. Spring loads appropriate XML configuration
                    ↓
3. Environment-specific beans created
                    ↓
4. CucumberRunner triggers feature file parsing
                    ↓
5. Spring Context initialized with XML config
                    ↓
6. BrowserSessions & ThreadVarsHashMap singletons created
                    ↓
7. Step definition beans instantiated and dependencies injected
                    ↓
8. Feature scenarios parsed
                    ↓
9. For each Scenario:
   a. @Before hooks execute
   b. @Given steps execute
   c. @When steps execute
   d. @Then steps execute
   e. @After(order=2) - Screenshot on failure
   f. @After(order=3) - Browser error validation
   g. @After(order=100) - Thread cleanup & browser close
                    ↓
10. Allure report generated with attachments
```

---

## 11. Running Tests with Different Configurations

### Command Line Examples
```bash
# Run all tests with dev environment
mvn clean test -P dev

# Run specific feature file with staging environment
mvn clean test -P staging -Dcucumber.filter.tags="@smoke"

# Run production tests with custom timeout
mvn clean test -P prod -Dbrowser.timeout=60000

# Run parallel tests (4 threads)
mvn clean test -P dev -Dcucumber.execution.parallel.enabled=true
```

---

## 12. Known Issues & Solutions

### Issue 1: ConcurrentModificationException
**Location**: ArrayList iteration during concurrent modifications  
**Cause**: Non-synchronized collection access  
**Solution**: Framework uses `Collections.synchronizedMap()` and `Collections.synchronizedList()`

### Issue 2: Console Error Saving (TODO)
**Location**: `CucumberHooks.java` attachBrowserLogs()  
**Current Status**: Basic list-based implementation  
**Improvement Needed**: Enhanced error categorization and filtering

### Issue 3: Spring Profile Not Activating
**Solution**: Ensure `spring.profiles.active` system property is set via Maven surefire plugin

---

## 13. Best Practices

1. **Thread Safety**: Always use `browserSessions` for browser interactions (thread-safe)
2. **Test Data**: Store scenario data in `threadVarsHashMap` with `TestKeyword` enum keys
3. **Cleanup**: Rely on `@After` hooks for resource cleanup (automatic)
4. **Configuration**: Use environment-specific XML and property files for different environments
5. **Logging**: Use `@Log` annotation from Lombok for consistent logging
6. **Dependency Injection**: Use setter-based injection in XML for step definitions and hooks
7. **Feature Files**: Keep scenarios atomic and focused
8. **Error Handling**: Let hooks capture and report errors automatically

---

## 14. Adding New Features to Framework

### Adding New Step Definition Class
```
1. Create NewFeatureSteps.java class
2. Add setter methods for BrowserSessions and ThreadVarsHashMap
3. Define @Given, @When, @Then methods
4. Register bean in applicationContext.xml
5. Use browserSessions for page interactions
6. Use threadVarsHashMap with TestKeyword enum for test data
```

### Adding New Test Keyword
```
1. Add new constant to TestKeyword enum
2. Use in step definitions: threadVarsHashMap.put(TestKeyword.NEW_KEY.name(), value)
3. Retrieve: threadVarsHashMap.get(TestKeyword.NEW_KEY.name())
```

### Adding New Environment Profile
```
1. Create applicationContext-newenv.xml
2. Create application-newenv.properties
3. Add Maven profile <id>newenv</id> in pom.xml
4. Run with: mvn clean test -P newenv
```

---