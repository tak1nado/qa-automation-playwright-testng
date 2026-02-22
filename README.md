# QA AUTOMATION TEST PROJECT

## Test framework description

Project consists of two parts: UI and API tests that can be run together or separately.

API tests are build based on TestNG framework and Rest-Assured library. 

UI tests are based on Cucumber but can be moved to TestNG easily. 
Cucumber needed for tests to be more structured, easy maintained and fast extended/implemented.
For better architecture tests should be separated in two different modules and separate module for tests java/main.

Spring introduced for data storing and dependency injection functionality. 
It needed to easily manage beans and build thread-safe framework.

Allure report implemented both for Cucumber and TestNG tests. These reports should be separated by modules in future and joined after all modules are executed.

Framework is based on its own web/user engine core together with Playwright logic inside. This core helps to easily switch between different browsers/user during the test and store/cleanup test data during and after the test.

## Project legend

Framework is built to be extended in the future for:
1) Storefront cockpit - user/customer part of project 
2) Backoffice/Admin cockpit - manage DB, orders, bookings, etc. part of project
3) Restful-api - part of project to speed up UI tests and validate backend integrations

## Framework configuration

1. create package `/src/main/resources/META-INF` (in `.gitignore`)

2. create properties files with project settings and credentials in `META-INF` package:
    1) `META-INF/dev.properties`
    2) `META-INF/qa.properties`
    3) fill them with following params and own credentials for dev and qa environment:
        - api tests dev/qa credentials:

           ```properties
           #for example
           #dev credentials
           booker.storefront.base.url=https://demoqa.com/
           booker.backoffice.base.url=https://backoffice-demoqa.com/
           booker.restful.base.url=https://restful-booker.herokuapp.com
           
           booker.backoffice.admin.role=ADMIN
           booker.backoffice.admin.username=admin
           booker.backoffice.admin.password=*********
           booker.backoffice.admin.cockpit=backoffice
           
           booker.storefront.guest.role=GUEST
           booker.storefront.guest.username=
           booker.storefront.guest.password=
           booker.storefront.guest.cockpit=storefront
           ``` 
    4) create `META-INF/browser.properties`
       - api tests browser setups (browser name and headless mode):

           ```properties
           #for example
           #browser config
           browser.name=chrome
           browser.headless.mode=true
           ```
    5) create `META-INF/playwrightRemoteHub.properties`
        - to run tests in remote environment.:

            ```properties
            #remote hub url needed if we want to run test in different browsers or in different environments in remote VM
            #hub.url=http://localhost:4444/wd/hub
            hub.url=
            ```
                     
3. in project `pom.xml` file find and set **active/default** environment profile `default.active.environment.profile`
      1. **qa** or
      2. **dev**
      
## For tests run

- from commandline: `mvn clean test allure:report`
    1) **clean** - delete old compiled code and artifacts
    2) **test** - compile and run tests
    3) **allure:report** - build report
- also you can run commands separately 
    1) `mvn clean test`  - clean and run 
    2) `mvn allure:report`  - generate report
- then you can open report in your browser using generated report in target/allure-report folder
- by default Spring runs dev profile with dev properties, to run other(f.e. production) profiles, use additional command - **-Dspring.profiles.active=env** :
    ```bash
    mvn clean test allure:report -Dspring.profiles.active=environmentToUse
    ```
- by default Maven runs 1 thread of tests, to run more treads use additional command - **-Dthread.count=3** :
  ```bash
  mvn clean test allure:report -Dthread.count=numberOfConcurrentTests
  ```
- by default Maven runs tests in browser that is set in `META-INF/browser.properties` . You can set browser in command line :
  ```bash
  mvn clean test -Dbrowser.name=chrome -Dbrowser.headless.mode=true allure:report
  ```
- by default Maven runs all the tests UI + API. You can specify what type of tests you want to launch :
  ```bash
  mvn clean test -Dgroups=api allure:report
  or 
  mvn clean test -Dgroups=ui allure:report
  ```
- logging Allure Steps into console:
      `-Dlogging.steps=true`

## CI/CD - GitHub Actions

1. Open Actions
2. Open Run all Automation QA tests
3. Click on Run workflow
4. Select test environment
5. Select type of tests to run
6. Select number of threads
7. Click on Run workflow

8. After tests are executed open report by direct link or in pages-build-deployment schema

## Allure report

Report contains Overview page with test run data:
- Restful endpoint:
- Browser name:
- Browser version:
- Environment:
- OS name:

Trend with overall number of tests and statistics of passed/failed/brocken tests

Behaviors page contains all test suites with test cases inside. Each test-case has:
- Main actions and steps
- Api history (Request, Response)
- Screenshot on failed tests
- Browser console errors if exist

## Dependencies

- download and set up Maven
- `pom.xml` - file with plugins and dependencies

## Documentation

- Swagger UI URL `https://restful-booker.herokuapp.com/apidoc/index.html`
- UI environment `https://demoqa.com/`
- Git repository `https://github.com/tak1nado/qa-automation-assignment`
- Git actions `https://github.com/tak1nado/qa-automation-assignment/actions`
- Git Allure reports `https://tak1nado.github.io/qa-automation-assignment/`

